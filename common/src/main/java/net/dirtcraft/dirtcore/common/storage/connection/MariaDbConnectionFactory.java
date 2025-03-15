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

package net.dirtcraft.dirtcore.common.storage.connection;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.storageutils.StorageCredentials;
import net.dirtcraft.storageutils.sql.connection.hikari.AbstractDriverBasedHikariConnectionFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class MariaDbConnectionFactory extends AbstractDriverBasedHikariConnectionFactory {

    private final DirtCorePlugin plugin;

    public MariaDbConnectionFactory(final DirtCorePlugin plugin,
            final StorageCredentials configuration) {
        super(configuration);
        this.plugin = plugin;
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '`'); // use backticks for quotes
    }

    @Override
    protected void handleClassloadingError(final Throwable throwable) {
        final List<String> noteworthyClasses =
                ImmutableList.of("org.slf4j.LoggerFactory", "org.slf4j.ILoggerFactory",
                        "org.apache.logging.slf4j.Log4jLoggerFactory",
                        "org.apache.logging.log4j.spi.LoggerContext",
                        "org.apache.logging.log4j.spi.AbstractLoggerAdapter",
                        "org.slf4j.impl.StaticLoggerBinder", "org.slf4j.helpers.MessageFormatter");
        final Logger logger = this.plugin.getLogger();

        logger.warn("A " + throwable.getClass().getSimpleName()
                + " has occurred whilst initialising Hikari. This is likely due to classloading "
                + "conflicts between other plugins.");
        logger.warn("Please check for other plugins below (and try loading DirtCore without them "
                + "installed) before reporting the issue.");

        for (final String className : noteworthyClasses) {
            final Class<?> clazz;

            try {
                clazz = Class.forName(className);
            } catch (final Exception e) {
                continue;
            }

            final ClassLoader loader = clazz.getClassLoader();
            String loaderName;

            try {
                loaderName = this.plugin.getBootstrap().identifyClassLoader(loader) + " ("
                        + loader.toString() + ")";
            } catch (final Throwable e) {
                loaderName = loader.toString();
            }

            logger.warn("Class " + className + " has been loaded by: " + loaderName);
        }
    }

    @Override
    protected String defaultPort() {
        return "3306";
    }

    @Override
    protected @NonNull String getPoolName() {
        return "dirtcore-hikari";
    }

    @Override
    protected String driverClassName() {
        return "org.mariadb.jdbc.Driver";
    }

    @Override
    protected String driverJdbcIdentifier() {
        return "mariadb";
    }
}
