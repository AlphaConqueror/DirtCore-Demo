/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.api;

import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.api.DirtCore;
import net.dirtcraft.dirtcore.api.DirtCoreProvider;
import net.dirtcraft.dirtcore.api.discord.DiscordManager;
import net.dirtcraft.dirtcore.common.api.implementation.ApiDiscordManager;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.BootstrappedWithLoader;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Implements the DirtCore API using the plugin instance
 */
public class DirtCoreApiProvider implements DirtCore {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final DiscordManager discordManager;

    public DirtCoreApiProvider(@NonNull final DirtCorePlugin plugin) {
        this.plugin = plugin;
        this.discordManager = new ApiDiscordManager(plugin);
    }

    public void ensureApiWasLoadedByPlugin() {
        final DirtCoreBootstrap bootstrap = this.plugin.getBootstrap();
        final ClassLoader pluginClassLoader;

        if (bootstrap instanceof BootstrappedWithLoader) {
            pluginClassLoader =
                    ((BootstrappedWithLoader) bootstrap).getLoader().getClass().getClassLoader();
        } else {
            pluginClassLoader = bootstrap.getClass().getClassLoader();
        }

        for (final Class<?> apiClass : new Class[] {DirtCore.class, DirtCoreProvider.class}) {
            final ClassLoader apiClassLoader = apiClass.getClassLoader();

            if (!apiClassLoader.equals(pluginClassLoader)) {
                String guilty = "unknown";

                try {
                    guilty = bootstrap.identifyClassLoader(apiClassLoader);
                } catch (final Exception ignored) {}

                final Logger logger = this.plugin.getLogger();

                logger.warn(
                        "It seems that the DirtCore API has been (class)loaded by a plugin other"
                                + " than DirtCore!");
                logger.warn("The API was loaded by " + apiClassLoader + " (" + guilty + ") and the "
                        + "DirtCore plugin was loaded by " + pluginClassLoader.toString() + ".");
                logger.warn("This indicates that the other plugin has incorrectly \"shaded\" the "
                        + "DirtCore API into its jar file. This can cause errors at runtime and "
                        + "should be fixed.");
                return;
            }
        }
    }

    @Override
    public @NonNull DiscordManager getDiscordManager() {
        return this.discordManager;
    }


    @Override
    public @NonNull AbstractEventBus<?> getEventBus() {
        return this.plugin.getEventDispatcher().getEventBus();
    }

    @Override
    public @NonNull CompletableFuture<Void> runUpdateTask() {
        return this.plugin.getSyncTaskBuffer().request();
    }
}
