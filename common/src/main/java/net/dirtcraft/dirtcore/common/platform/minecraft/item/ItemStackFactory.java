/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.item;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory class to make a thread-safe item stack instance.
 *
 * @param <P> the plugin type
 * @param <I> the item stack type
 */
public abstract class ItemStackFactory<P extends DirtCorePlugin, I> {

    private final P plugin;

    public ItemStackFactory(final P plugin) {this.plugin = plugin;}

    protected abstract @NonNull Optional<I> createItemStack(@NonNull final String identifier,
            final int count, @Nullable final String persistentData);

    protected abstract @NonNull Component getDisplayName(@NonNull I itemStack);

    protected abstract @NonNull I transform(@NonNull ItemStack itemStack);

    protected abstract @NonNull ItemStack transform(@NonNull SimpleItemStack itemStack);

    protected abstract void setDisplayName(@NonNull I itemStack, @NonNull Component displayName);

    protected abstract int getStackSize(@NonNull I itemStack);

    protected abstract void setStackSize(@NonNull I itemStack, int stackSize);

    protected abstract @NonNull String getIdentifier(@NonNull I itemStack);

    protected abstract @NonNull String getMod(@NonNull I itemStack);

    protected abstract void appendLore(@NonNull I itemStack,
            @NonNull Collection<Component> loreCollection);

    protected abstract void setLore(@NonNull I itemStack, @NonNull List<Component> loreList);

    protected abstract boolean hasPersistentData(@NonNull I itemStack);

    protected abstract @Nullable String getPersistentDataAsString(@NonNull I itemStack);

    protected abstract boolean isSameItemSamePersistentData(@NonNull I itemStack,
            @NonNull ItemStack other);

    protected abstract boolean persistentDataPartiallyMatches(@NonNull I itemStack,
            @NonNull final String s);

    protected abstract boolean persistentDataMatches(@NonNull I itemStack,
            @Nullable final String s);

    protected abstract boolean isBlock(@NonNull I itemStack);

    protected abstract boolean isEmpty(@NonNull I itemStack);

    protected abstract Optional<Block> getAsBlock(@NonNull I itemStack);

    @NonNull
    protected abstract I copy(@NonNull I itemStack);

    public final ItemStack wrap(final I itemStack) {
        Objects.requireNonNull(itemStack, "itemStack");
        return new AbstractItemStack<>(this, itemStack);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
