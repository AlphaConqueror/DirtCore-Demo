/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeNOPContainer extends InventoryBasic {

    public ForgeNOPContainer(final String inventoryTitle, final boolean hasCustomName,
            final int slotsCount) {
        super(inventoryTitle, hasCustomName, slotsCount);
    }

    @NonNull
    @Override
    public ItemStack getStackInSlot(final int slotIn) {
        return this.getActualStack(slotIn);
    }

    @NonNull
    @Override
    public ItemStack decrStackSize(final int index, final int count) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setInventorySlotContents(final int index, @NonNull final ItemStack stack) {}

    @Override
    public boolean isUsableByPlayer(@NonNull final EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(final int index, @NonNull final ItemStack stack) {
        return false;
    }

    public void updateStack(final int slot, @NonNull final ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
    }

    @NonNull
    public ItemStack getActualStack(final int slot) {
        return super.getStackInSlot(slot);
    }
}
