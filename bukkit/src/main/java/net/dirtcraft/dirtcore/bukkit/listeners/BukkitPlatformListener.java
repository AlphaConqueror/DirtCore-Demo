/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.listeners;

import java.util.Arrays;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.platform.sender.Sender;
import org.bukkit.advancement.Advancement;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitPlatformListener implements Listener {

    @NonNull
    private final DirtCoreBukkitPlugin plugin;

    public BukkitPlatformListener(@NonNull final DirtCoreBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAchievement(final PlayerAdvancementDoneEvent event) {
        final Advancement advancement = event.getAdvancement();
        final String rawAdvancementName = advancement.getKey().getKey();

        if (this.plugin.getConfiguration().get(ConfigKeys.BLACKLISTED_ADVANCEMENTS).stream()
                .anyMatch(s -> Pattern.matches(s, rawAdvancementName))) {
            return;
        }

        final String advancementName = Arrays.stream(
                        rawAdvancementName.substring(rawAdvancementName.lastIndexOf("/") + 1).toLowerCase()
                                .split("_")).map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
                .collect(Collectors.joining(" "));
        final Player player = event.getPlayer();

        this.plugin.getLogger()
                .info("Dispatching player achievement event for key '{}'.", rawAdvancementName);
        this.plugin.getEventDispatcher()
                .dispatchPlayerAchievement(player.getUniqueId(), player.getName(), advancementName,
                        null);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onServerCommand(final ServerCommandEvent event) {
        final CommandSender sender = event.getSender();
        final UUID uniqueId;
        final String username;

        if (sender instanceof Player) {
            final Player player = (Player) sender;

            uniqueId = player.getUniqueId();
            username = player.getName();
        } else {
            uniqueId = Sender.CONSOLE_UUID;
            username = Sender.CONSOLE_NAME;
        }

        final String commandLine = event.getCommand();
        final boolean cancel = this.plugin.getEventDispatcher()
                .dispatchServerCommand(false, uniqueId, username, commandLine);

        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final String deathMessage = event.getDeathMessage();

        if (deathMessage == null) {
            return;
        }

        final Entity entity = event.getEntity();

        this.plugin.getEventDispatcher().dispatchPlayerDeath(entity.getUniqueId(), entity.getName(),
                this.plugin.getPlatformFactory().stripFormatting(deathMessage));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(final PlayerJoinEvent event) {
        final UUID uniqueId = event.getPlayer().getUniqueId();
        final String username = event.getPlayer().getName();

        this.plugin.getEventDispatcher().dispatchPlayerLogin(uniqueId, username);
        this.plugin.getEventDispatcher().dispatchPlayerPostLogin(uniqueId, username);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.plugin.getEventDispatcher()
                .dispatchPlayerLogout(player.getUniqueId(), player.getName());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onServerChatEarly(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        // this is only called if it wasn't cancelled before already
        final boolean cancel = this.plugin.getEventDispatcher()
                .dispatchPlayerChatEarly(false, player.getUniqueId(), player.getName(),
                        player.getDisplayName(), event.getMessage());

        event.setCancelled(cancel);
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onServerChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String displayName = player.getDisplayName();

        this.plugin.getEventDispatcher()
                .dispatchPlayerChat(player.getUniqueId(), player.getName(), displayName,
                        this.plugin.getPlatformFactory().stripFormatting(displayName),
                        event.getMessage());
        // cancel, will be handled by messaging manager
        event.setCancelled(true);
    }
}
