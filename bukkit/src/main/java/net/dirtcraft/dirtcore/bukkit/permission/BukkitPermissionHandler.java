/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.permission;

import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.permission.PermissionHandler;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitPermissionHandler implements PermissionHandler<CommandSender> {

    private final DirtCoreBukkitPlugin plugin;

    public BukkitPermissionHandler(final DirtCoreBukkitPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(@NonNull final CommandSender sender,
            @NonNull final String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean hasPermission(@NonNull final UUID uniqueId, @NonNull final String permission) {
        final Player player = this.plugin.getBootstrap().getServer().getPlayer(uniqueId);
        return player != null && player.hasPermission(permission);
    }

    @Override
    public @NonNull Optional<String> getPrefixFormatted(@NonNull final UUID uniqueId) {
        return this.plugin.getBootstrap().getServer().getOperators().stream()
                .anyMatch(offlinePlayer -> offlinePlayer.getUniqueId().equals(uniqueId))
                ? Optional.of("[OP] ") : Optional.empty();
    }

    @Override
    public @NonNull Optional<String> getPrefixUnformatted(@NonNull final UUID uniqueId) {
        return this.getPrefixFormatted(uniqueId)
                .map(s -> this.plugin.getPlatformFactory().stripFormatting(s));
    }

    @Override
    public @NonNull Optional<String> getSuffixFormatted(@NonNull final UUID uniqueId) {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<String> getSuffixUnformatted(@NonNull final UUID uniqueId) {
        return this.getSuffixFormatted(uniqueId)
                .map(s -> this.plugin.getPlatformFactory().stripFormatting(s));
    }
}
