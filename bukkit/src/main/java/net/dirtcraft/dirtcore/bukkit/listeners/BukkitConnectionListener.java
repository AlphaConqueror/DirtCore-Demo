/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.listeners;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitConnectionListener extends AbstractConnectionListener<AsyncPlayerPreLoginEvent> implements Listener {

    private final DirtCoreBukkitPlugin plugin;
    private final Set<UUID> deniedAsyncLogin = Collections.synchronizedSet(new HashSet<>());
    private final Set<UUID> deniedLogin = Collections.synchronizedSet(new HashSet<>());

    public BukkitConnectionListener(final DirtCoreBukkitPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerPreLogin(final AsyncPlayerPreLoginEvent event) {
        /* Called when the player first attempts a connection with the server.
           Listening on LOW priority to allow plugins to modify username / UUID data here. (auth
           plugins)
           Also, give other plugins a chance to cancel the event. */

        /* Wait for the plugin to enable. Because these events are fired async, they can be
        called before the plugin has enabled.  */
        try {
            //noinspection ResultOfMethodCallIgnored
            this.plugin.getBootstrap().getEnableLatch().await(60, TimeUnit.SECONDS);
        } catch (final InterruptedException e) {
            this.plugin.getLogger().severe("Caught exception during awaiting of enable latch. ", e);
        }

        final UUID uniqueId = event.getUniqueId();

        if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
            this.deniedAsyncLogin.add(uniqueId);
            return;
        }

        final String username = event.getName();
        this.onPlayerNegotiation(event, uniqueId, username);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPreLoginMonitor(final AsyncPlayerPreLoginEvent e) {
        /* Listen to see if the event was cancelled after we initially handled the connection
           If the connection was cancelled here, we need to do something to clean up the data
           that was loaded. */

        // Check to see if this connection was denied at LOW.
        if (this.deniedAsyncLogin.remove(e.getUniqueId())) {
            // their data was never loaded at LOW priority, now check to see if they have been
            // magically allowed since then.

            // This is a problem, as they were denied at low priority, but are now being allowed.
            if (e.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                this.plugin.getLogger()
                        .severe("Player connection was re-allowed for " + e.getUniqueId());
                e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "");
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event) {
        /* Called when the player starts logging into the server.
           At this point, the users data should be present and loaded. */

        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final String username = player.getName();

        this.plugin.getStorage().performTask(context -> {
            final User user = this.plugin.getUserManager().getUser(context, uniqueId);

            /* User instance is null for whatever reason.
             Could be that it was unloaded between asyncpre and now. */
            if (user == null) {
                this.deniedLogin.add(player.getUniqueId());

                if (!this.getUniqueConnections().contains(uniqueId)) {
                    this.plugin.getLogger().warn("User " + uniqueId + " - " + username
                            + " doesn't have data pre-loaded, they have never been "
                            + "processed during pre-login in this context.session().");
                } else {
                    this.plugin.getLogger().warn("User " + uniqueId + " - " + username
                            + " doesn't currently have data pre-loaded, but they have "
                            + "been processed before in this context.session().");
                }

                final Component component = Components.LOADING_STATE_ERROR.build();
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,
                        this.plugin.getPlatformFactory().transformToString(component));
            }
        });
    }

    @Override
    protected void disconnect(@NonNull final AsyncPlayerPreLoginEvent connection,
            @NonNull final Component component) {
        connection.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                this.plugin.getPlatformFactory().transformToString(component));
    }

    @Override
    protected String getIPAddress(@NonNull final AsyncPlayerPreLoginEvent connection) {
        return connection.getAddress().getHostAddress();
    }
}
