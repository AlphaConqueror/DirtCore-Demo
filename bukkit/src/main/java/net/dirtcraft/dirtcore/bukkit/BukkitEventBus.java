/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit;

import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class BukkitEventBus extends AbstractEventBus<Plugin> implements Listener {

    public BukkitEventBus(final DirtCoreBukkitPlugin plugin,
            final DirtCoreApiProvider apiProvider) {
        super(plugin, apiProvider);

        // register listener
        final DirtCoreBukkitBootstrap bootstrap = plugin.getBootstrap();
        bootstrap.getServer().getPluginManager().registerEvents(this, bootstrap.getLoader());
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent e) {
        final Plugin plugin = e.getPlugin();
        this.unregisterHandlers(plugin);
    }

    @Override
    protected Plugin checkPlugin(final @NotNull Object plugin) throws IllegalArgumentException {
        if (plugin instanceof Plugin) {
            return (Plugin) plugin;
        }

        throw new IllegalArgumentException(
                "Object " + plugin + " (" + plugin.getClass().getName() + ") is not a plugin.");
    }
}
