/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.player;

import java.util.Collection;
import java.util.List;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.AbstractEntity;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of {@link Player} using a {@link PlayerFactory}.
 *
 * @param <E> the entity type
 * @param <P> the player type
 */
public final class AbstractPlayer<E, P extends E> extends AbstractEntity<E> implements Player {

    private final PlayerFactory<?, E, P> playerFactory;
    private final P player;

    public AbstractPlayer(final DirtCorePlugin plugin, final EntityFactory<?, E> entityFactory,
            final PlayerFactory<?, E, P> playerFactory, final P player) {
        super(plugin, entityFactory, player);
        this.playerFactory = playerFactory;
        this.player = player;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return this.playerFactory.getDisplayName(this.player);
    }

    @Override
    public boolean hasDisconnected() {
        return this.playerFactory.hasDisconnected(this.player);
    }

    @Override
    public @NonNull ItemStack getItemInHand() {
        return this.playerFactory.getItemInHand(this.player);
    }

    @Override
    public @NonNull List<ItemStack> getItems() {
        return this.playerFactory.getItems(this.player);
    }

    @Override
    public boolean kick(@NonNull final Component reason) {
        return this.getPlugin().getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.playerFactory.kick(this.player, reason));
    }

    @Override
    public void sendMessage(@NonNull final Component message) {
        this.playerFactory.sendMessage(this.player, message);
    }

    @Override
    public void sendMessage(@NonNull final Iterable<Component> messages) {
        this.playerFactory.sendMessage(this.player, messages);
    }

    @Override
    public void performCommand(final String command) {
        this.playerFactory.performCommand(this.player, command);
    }

    @Override
    public int getFreeInventorySpace() {
        return this.playerFactory.getFreeInventorySpace(this.player);
    }

    @Override
    public boolean isCreative() {
        return this.playerFactory.isCreative(this.player);
    }

    @Override
    public void openContainer(@NonNull final Container container) {
        this.getPlugin().getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.playerFactory.openContainer(this.player, container));
    }

    @Override
    public void playSound(@NonNull final Sound sound) {
        this.playerFactory.playSound(this.player, sound);
    }

    @Override
    public void addItem(@NonNull final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }

        this.getPlugin().getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.playerFactory.addItem(this.player, itemStack));
    }

    @Override
    public boolean teleport(@NonNull final World world, final double x, final double y,
            final double z, final float yRot, final float xRot) {
        return this.getPlugin().getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.playerFactory.teleport(this.player, world, x, y, z, yRot, xRot));
    }

    @Override
    public boolean hasPermission(final @NonNull String permission) {
        return this.getPlugin().getPermissionHandler()
                .hasPermission(this.getUniqueId(), permission);
    }

    @Override
    public boolean isPartOfGroup(@NonNull final String name) {
        return this.getPlugin().getPermissionHandler().isPartOfGroup(this.getUniqueId(), name);
    }

    @Override
    public @NonNull Collection<String> getGroups() {
        return this.getPlugin().getPermissionHandler().getGroups(this.getUniqueId());
    }
}
