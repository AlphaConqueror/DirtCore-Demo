/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.player;

import java.util.List;
import java.util.Objects;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe player instance.
 *
 * @param <P> the plugin type
 * @param <T> the player type
 * @param <E> the entity type
 */
public abstract class PlayerFactory<P extends DirtCorePlugin, E, T extends E> {

    private final P plugin;
    private final EntityFactory<P, E> entityFactory;

    public PlayerFactory(final P plugin, final EntityFactory<P, E> entityFactory) {
        this.plugin = plugin;
        this.entityFactory = entityFactory;
    }

    @NonNull
    public abstract Component getDisplayName(@NonNull T entity);

    protected abstract boolean hasDisconnected(@NonNull T player);

    @NonNull
    protected abstract ItemStack getItemInHand(@NonNull T player);

    @NonNull
    protected abstract List<ItemStack> getItems(@NonNull T player);

    protected abstract boolean kick(@NonNull T player, @NonNull Component reason);

    protected abstract void sendMessage(@NonNull T player, @NonNull final Component message);

    protected abstract void sendMessage(@NonNull T player,
            @NonNull final Iterable<Component> messages);

    protected abstract void performCommand(@NonNull T player, @NonNull final String command);

    protected abstract int getFreeInventorySpace(@NonNull T player);

    protected abstract boolean isCreative(@NonNull T player);

    protected abstract void openContainer(@NonNull T player, @NonNull Container container);

    protected abstract void playSound(@NonNull T player, @NonNull Sound sound);

    protected abstract void addItem(@NonNull T player, @NonNull ItemStack itemStack);

    protected abstract boolean teleport(@NonNull T player, @NonNull World world, double x, double y,
            double z, float yRot, float xRot);

    @NonNull
    public final Player wrap(@NonNull final T player) {
        Objects.requireNonNull(player, "player");
        return new AbstractPlayer<>(this.plugin, this.entityFactory, this, player);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
