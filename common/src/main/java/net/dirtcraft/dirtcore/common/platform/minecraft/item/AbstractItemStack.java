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
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple implementation of {@link ItemStack} using a {@link ItemStackFactory}.
 *
 * @param <I> the item stack type
 */
public final class AbstractItemStack<I> implements ItemStack {

    private final ItemStackFactory<?, I> factory;
    private final I itemStack;

    public AbstractItemStack(final ItemStackFactory<?, I> factory, final I itemStack) {
        this.factory = factory;
        this.itemStack = itemStack;
    }

    @Override
    public void setLore(@NonNull final List<Component> lore) {
        this.factory.setLore(this.itemStack, lore);
    }

    @Override
    public void appendLore(@NonNull final Collection<Component> loreCollection) {
        this.factory.appendLore(this.itemStack, loreCollection);
    }

    @Override
    public @NonNull Component getDisplayName() {
        return this.factory.getDisplayName(this.itemStack);
    }

    @Override
    public int getStackSize() {
        return this.factory.getStackSize(this.itemStack);
    }

    @Override
    public void setStackSize(final int stackSize) {
        this.factory.setStackSize(this.itemStack, stackSize);
    }

    @Override
    public boolean hasPersistentData() {
        return this.factory.hasPersistentData(this.itemStack);
    }

    @Override
    public boolean isSameItemSamePersistentData(@NonNull final ItemStack other) {
        return this.factory.isSameItemSamePersistentData(this.itemStack, other);
    }

    @Override
    public boolean isBlock() {
        return this.factory.isBlock(this.itemStack);
    }

    @Override
    public @NonNull Optional<Block> getAsBlock() {
        return this.factory.getAsBlock(this.itemStack);
    }

    @Override
    public @NonNull ItemStack copy() {
        return new AbstractItemStack<>(this.factory, this.factory.copy(this.itemStack));
    }

    @Override
    public boolean isEmpty() {
        return this.factory.isEmpty(this.itemStack);
    }

    @Override
    public @NonNull String getIdentifier() {
        return this.factory.getIdentifier(this.itemStack);
    }

    @Override
    public @NonNull String getMod() {
        return this.factory.getMod(this.itemStack);
    }

    @Override
    public @Nullable String getPersistentDataAsString() {
        return this.factory.getPersistentDataAsString(this.itemStack);
    }

    @Override
    public boolean persistentDataMatches(@Nullable final String s) {
        return this.factory.persistentDataMatches(this.itemStack, s);
    }

    @Override
    public boolean persistentDataPartiallyMatches(@NonNull final String s) {
        return this.factory.persistentDataPartiallyMatches(this.itemStack, s);
    }

    public I getItemStack() {
        return this.itemStack;
    }
}
