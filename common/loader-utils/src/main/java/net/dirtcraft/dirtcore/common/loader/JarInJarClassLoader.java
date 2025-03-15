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

package net.dirtcraft.dirtcore.common.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Classloader that can load a jar from within another jar file.
 *
 * <p>The "loader" jar contains the loading code and public API classes,
 * and is class-loaded by the platform.</p>
 *
 * <p>The inner "plugin" jar contains the plugin itself, and is class-loaded
 * by the loading code and this classloader.</p>
 */
public class JarInJarClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    /**
     * Creates a new jar-in-jar class loader.
     *
     * @param loaderClassLoader the loader plugin's classloader (setup and created by the platform)
     * @param jarResourcePath   the path to the jar-in-jar resource within the loader jar
     * @throws LoadingException if something unexpectedly bad happens
     */
    public JarInJarClassLoader(final ClassLoader loaderClassLoader,
            final String jarResourcePath) throws LoadingException {
        super(new URL[] {extractJar(loaderClassLoader, jarResourcePath)}, loaderClassLoader);
    }

    /**
     * Extracts the "jar-in-jar" from the loader plugin into a temporary file,
     * then returns a URL that can be used by the {@link JarInJarClassLoader}.
     *
     * @param loaderClassLoader the classloader for the "host" loader plugin
     * @param jarResourcePath   the inner jar resource path
     * @return a URL to the extracted file
     */
    private static URL extractJar(final ClassLoader loaderClassLoader,
            final String jarResourcePath) throws LoadingException {
        // get the jar-in-jar resource
        final URL jarInJar = loaderClassLoader.getResource(jarResourcePath);

        if (jarInJar == null) {
            throw new LoadingException("Could not locate jar-in-jar");
        }

        // create a temporary file
        // on posix systems by default this is only read/writable by the process owner
        final Path path;

        try {
            path = Files.createTempFile("DirtCore-jarinjar", ".jar.tmp");
        } catch (final IOException e) {
            throw new LoadingException("Unable to create a temporary file", e);
        }

        // mark that the file should be deleted on exit
        path.toFile().deleteOnExit();

        // copy the jar-in-jar to the temporary file path
        try (final InputStream in = jarInJar.openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (final IOException e) {
            throw new LoadingException("Unable to copy jar-in-jar to temporary path", e);
        }

        try {
            return path.toUri().toURL();
        } catch (final MalformedURLException e) {
            throw new LoadingException("Unable to get URL from path", e);
        }
    }

    /**
     * Adds the jar to the classpath.
     *
     * @param url the url to the jar
     */
    public void addJarToClasspath(final URL url) {
        this.addURL(url);
    }

    /**
     * Deletes the jar resource.
     */
    public void deleteJarResource() {
        final URL[] urls = this.getURLs();

        if (urls.length == 0) {
            return;
        }

        try {
            final Path path = Paths.get(urls[0].toURI());
            Files.deleteIfExists(path);
        } catch (final Exception e) {
            // ignore
        }
    }

    /**
     * Creates a new plugin instance.
     *
     * @param <T>              the type of the loader plugin
     * @param bootstrapClass   the name of the bootstrap plugin class
     * @param loaderPluginType the class of the loader plugin type
     * @param loaderPlugin     the loader plugin
     * @return the instantiated bootstrap plugin
     */
    @SuppressWarnings("unchecked")
    public <T> LoaderBootstrap<?, ?, ?> instantiatePlugin(final String bootstrapClass,
            final Class<T> loaderPluginType, final T loaderPlugin) throws LoadingException {
        final Class<? extends LoaderBootstrap<?, ?, ?>> plugin;

        try {
            plugin = (Class<? extends LoaderBootstrap<?, ?, ?>>) this.loadClass(bootstrapClass)
                    .asSubclass(LoaderBootstrap.class);
        } catch (final ReflectiveOperationException e) {
            throw new LoadingException("Unable to load bootstrap class", e);
        }

        final Constructor<? extends LoaderBootstrap<?, ?, ?>> constructor;

        try {
            constructor = plugin.getConstructor(loaderPluginType);
        } catch (final ReflectiveOperationException e) {
            throw new LoadingException("Unable to get bootstrap constructor", e);
        }

        try {
            return constructor.newInstance(loaderPlugin);
        } catch (final ReflectiveOperationException e) {
            throw new LoadingException("Unable to create bootstrap plugin instance", e);
        }
    }
}
