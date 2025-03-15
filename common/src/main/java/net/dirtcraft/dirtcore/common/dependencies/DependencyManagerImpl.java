/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.dependencies;

import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import net.dirtcraft.dirtcore.common.dependencies.classloader.IsolatedClassLoader;
import net.dirtcraft.dirtcore.common.dependencies.relocation.Relocation;
import net.dirtcraft.dirtcore.common.dependencies.relocation.RelocationHandler;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.classpath.ClassPathAppender;
import net.dirtcraft.dirtcore.common.util.MoreFiles;
import net.dirtcraft.storageutils.StorageType;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public class DependencyManagerImpl implements DependencyManager {

    /**
     * A registry containing plugin specific behaviour for dependencies.
     */
    private final DependencyRegistry registry;
    /**
     * The path where library jars are cached.
     */
    private final Path cacheDirectory;
    /**
     * The classpath appender to preload dependencies into
     */
    private final ClassPathAppender classPathAppender;
    /**
     * The executor to use when loading dependencies
     */
    private final Executor loadingExecutor;

    /**
     * A map of dependencies which have already been loaded.
     */
    private final EnumMap<Dependency, Path> loaded = new EnumMap<>(Dependency.class);
    /**
     * A map of isolated classloaders which have been created.
     */
    private final Map<ImmutableSet<Dependency>, IsolatedClassLoader> loaders = new HashMap<>();
    private final DirtCorePlugin plugin;
    /**
     * Cached relocation handler instance.
     */
    private @MonotonicNonNull RelocationHandler relocationHandler = null;

    public DependencyManagerImpl(final DirtCorePlugin plugin) {
        this.plugin = plugin;
        this.registry = new DependencyRegistry();
        this.cacheDirectory = setupCacheDirectory(plugin);
        this.classPathAppender = plugin.getBootstrap().getClassPathAppender();
        this.loadingExecutor = plugin.getBootstrap().getScheduler().async();
    }

    private static Path setupCacheDirectory(final DirtCorePlugin plugin) {
        final Path cacheDirectory = plugin.getBootstrap().getDataDirectory().resolve("libs");

        try {
            MoreFiles.createDirectoriesIfNotExists(cacheDirectory);
        } catch (final IOException e) {
            throw new RuntimeException("Unable to create libs directory", e);
        }

        final Path oldCacheDirectory = plugin.getBootstrap().getDataDirectory().resolve("lib");

        if (Files.exists(oldCacheDirectory)) {
            try {
                MoreFiles.deleteDirectory(oldCacheDirectory);
            } catch (final IOException e) {
                plugin.getLogger().warn("Unable to delete lib directory", e);
            }
        }

        return cacheDirectory;
    }

    @Override
    public void loadDependencies(final Set<Dependency> dependencies) {
        final CountDownLatch latch = new CountDownLatch(dependencies.size());

        for (final Dependency dependency : dependencies) {
            if (this.loaded.containsKey(dependency)) {
                latch.countDown();
                continue;
            }

            this.loadingExecutor.execute(() -> {
                try {
                    this.loadDependency(dependency);
                } catch (final Throwable e) {
                    this.plugin.getLogger()
                            .severe("Unable to load dependency " + dependency.name(), e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void loadStorageDependencies(final Set<StorageType> storageTypes) {
        this.loadDependencies(this.registry.resolveStorageDependencies(storageTypes));
    }

    @Override
    public ClassLoader obtainClassLoaderWith(final Set<Dependency> dependencies) {
        final ImmutableSet<Dependency> set = ImmutableSet.copyOf(dependencies);

        for (final Dependency dependency : dependencies) {
            if (!this.loaded.containsKey(dependency)) {
                throw new IllegalStateException("Dependency " + dependency + " is not loaded.");
            }
        }

        synchronized (this.loaders) {
            IsolatedClassLoader classLoader = this.loaders.get(set);
            if (classLoader != null) {
                return classLoader;
            }

            final URL[] urls = set.stream().map(this.loaded::get).map(file -> {
                try {
                    return file.toUri().toURL();
                } catch (final MalformedURLException e) {
                    throw new RuntimeException(e);
                }
            }).toArray(URL[]::new);

            classLoader = new IsolatedClassLoader(urls);
            this.loaders.put(set, classLoader);
            return classLoader;
        }
    }

    @Override
    public void close() {
        IOException firstEx = null;

        for (final IsolatedClassLoader loader : this.loaders.values()) {
            try {
                loader.close();
            } catch (final IOException ex) {
                if (firstEx == null) {
                    firstEx = ex;
                } else {
                    firstEx.addSuppressed(ex);
                }
            }
        }

        if (firstEx != null) {
            this.plugin.getLogger().severe("Caught exception.", firstEx);
        }
    }

    private synchronized RelocationHandler getRelocationHandler() {
        if (this.relocationHandler == null) {
            this.relocationHandler = new RelocationHandler(this);
        }
        return this.relocationHandler;
    }

    private void loadDependency(final Dependency dependency) throws Exception {
        if (this.loaded.containsKey(dependency)) {
            return;
        }

        final Path file = this.remapDependency(dependency, this.downloadDependency(dependency));

        this.loaded.put(dependency, file);

        if (this.classPathAppender != null && this.registry.shouldAutoLoad(dependency)) {
            this.classPathAppender.addJarToClasspath(file);
        }
    }

    private Path downloadDependency(
            final Dependency dependency) throws DependencyDownloadException {
        final Path file = this.cacheDirectory.resolve(dependency.getFileName(null));

        // if the file already exists, don't attempt to re-download it.
        if (Files.exists(file)) {
            return file;
        }

        DependencyDownloadException lastError = null;

        // attempt to download the dependency from each repo in order.
        for (final DependencyRepository repo : DependencyRepository.values()) {
            try {
                repo.download(dependency, file);
                return file;
            } catch (final DependencyDownloadException e) {
                lastError = e;
            }
        }

        throw Objects.requireNonNull(lastError);
    }

    private Path remapDependency(final Dependency dependency,
            final Path normalFile) throws Exception {
        final List<Relocation> rules = new ArrayList<>(dependency.getRelocations());
        this.registry.applyRelocationSettings(dependency, rules);

        if (rules.isEmpty()) {
            return normalFile;
        }

        final Path remappedFile = this.cacheDirectory.resolve(dependency.getFileName(
                DependencyRegistry.isGsonRelocated() ? "remapped-legacy" : "remapped"));

        // if the remapped source exists already, just use that.
        if (Files.exists(remappedFile)) {
            return remappedFile;
        }

        this.getRelocationHandler().remap(normalFile, remappedFile, rules);
        return remappedFile;
    }

}
