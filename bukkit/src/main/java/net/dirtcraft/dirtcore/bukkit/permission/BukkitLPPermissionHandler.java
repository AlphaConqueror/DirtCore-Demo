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
import net.dirtcraft.dirtcore.common.permission.AbstractLPPermissionHandler;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitLPPermissionHandler extends AbstractLPPermissionHandler<CommandSender> {

    private final DirtCoreBukkitPlugin plugin;

    public BukkitLPPermissionHandler(final DirtCoreBukkitPlugin plugin) {this.plugin = plugin;}

    @Override
    public @NonNull Optional<String> getPrefixFormatted(@NonNull final UUID uniqueId) {
        return super.getPrefixFormatted(uniqueId).map(s -> s.replace('&', '§'));
    }

    @Override
    public @NonNull Optional<String> getPrefixUnformatted(@NonNull final UUID uniqueId) {
        return this.getPrefixFormatted(uniqueId)
                .map(s -> this.plugin.getPlatformFactory().stripFormatting(s));
    }

    @Override
    public @NonNull Optional<String> getSuffixFormatted(@NonNull final UUID uniqueId) {
        return super.getSuffixFormatted(uniqueId).map(s -> s.replace('&', '§'));
    }

    @Override
    public @NonNull Optional<String> getSuffixUnformatted(@NonNull final UUID uniqueId) {
        return this.getSuffixFormatted(uniqueId)
                .map(s -> this.plugin.getPlatformFactory().stripFormatting(s));
    }

    @Override
    protected @NonNull UUID getUUID(final CommandSender sender) {
        return this.plugin.getSenderFactory().getUniqueId(sender);
    }

    @Override
    protected boolean isConsole(final CommandSender sender) {
        return this.plugin.getSenderFactory().isConsole(sender);
    }
}
