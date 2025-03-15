/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.plugin.classpath;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import net.dirtcraft.dirtcore.common.loader.JarInJarClassLoader;
import net.dirtcraft.dirtcore.common.logging.Logger;

public class JarInJarClassPathAppender implements ClassPathAppender {

    private final Logger logger;
    private final JarInJarClassLoader classLoader;

    public JarInJarClassPathAppender(final Logger logger, final ClassLoader classLoader) {
        if (!(classLoader instanceof JarInJarClassLoader)) {
            throw new IllegalArgumentException(
                    "Loader is not a JarInJarClassLoader: " + classLoader.getClass().getName());
        }

        this.logger = logger;
        this.classLoader = (JarInJarClassLoader) classLoader;
    }

    @Override
    public void addJarToClasspath(final Path file) {
        try {
            this.classLoader.addJarToClasspath(file.toUri().toURL());
        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.classLoader.deleteJarResource();

        try {
            this.classLoader.close();
        } catch (final IOException e) {
            this.logger.severe("Caught exception when closing ", e);
        }
    }
}
