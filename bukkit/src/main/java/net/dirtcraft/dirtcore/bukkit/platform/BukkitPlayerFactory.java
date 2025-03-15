/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.platform;

import java.util.UUID;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.platform.minecraft.player.PlayerFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitPlayerFactory extends PlayerFactory<DirtCoreBukkitPlugin, Player> {


    public BukkitPlayerFactory(final DirtCoreBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NonNull UUID getUniqueId(@NonNull final Player player) {
        return player.getUniqueId();
    }

    @Override
    protected boolean kick(@NonNull final Player player, @NonNull final Component reason) {
        player.kickPlayer(this.getPlugin().getPlatformFactory().transformToString(reason));
        return true;
    }

    @Override
    protected void sendMessage(@NonNull final Player player, @NonNull final Component message) {
        player.spigot()
                .sendMessage(this.getPlugin().getPlatformFactory().transformComponent(message));
    }

    @Override
    protected void sendMessage(@NonNull final Player player,
            @NonNull final Iterable<Component> message) {
        for (final Component c : message) {
            this.sendMessage(player, c);
        }
    }

    @Override
    protected boolean hasPermission(final Player player, final String node) {
        return this.getPlugin().getPermissionHandler().hasPermission(player, node);
    }

    @Override
    protected void performCommand(@NonNull final Player player, @NonNull final String command) {
        this.getPlugin().getBootstrap().getServer().dispatchCommand(player, command);
    }

    @Override
    protected int getFreeInventorySpace(@NonNull final Player player) {
        int freeSpace = 0;

        for (final ItemStack item : player.getInventory()) {
            if (item == null) {
                freeSpace++;
            }
        }

        return freeSpace;
    }

    @Override
    protected void openContainer(@NonNull final Player player, @NonNull final Container container) {

    }
}
