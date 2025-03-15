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
