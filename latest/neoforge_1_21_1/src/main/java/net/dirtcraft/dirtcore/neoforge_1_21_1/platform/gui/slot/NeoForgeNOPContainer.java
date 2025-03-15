/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NeoForgeNOPContainer extends SimpleContainer {

    public NeoForgeNOPContainer(final int pSize) {
        super(pSize);
    }

    @Override
    public @NotNull ItemStack getItem(final int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull List<ItemStack> removeAllItems() {
        return new ArrayList<>();
    }

    @Override
    public @NotNull ItemStack removeItem(final int slot, final int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemType(@NotNull final Item item, final int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack addItem(@NotNull final ItemStack stack) {
        return stack;
    }

    @Override
    public boolean canAddItem(@NotNull final ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(final int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(final int slot, @NotNull final ItemStack stack) {}

    @Override
    public boolean stillValid(@NotNull final Player player) {
        return false;
    }

    public void updateStack(final int slot, final ItemStack stack) {
        super.setItem(slot, stack);
    }

    public ItemStack getActualStack(final int slot) {
        return super.getItem(slot);
    }
}
