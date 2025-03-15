/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeNOPContainer extends InventoryBasic {

    public ForgeNOPContainer(final String inventoryTitle, final boolean hasCustomName,
            final int slotsCount) {
        super(inventoryTitle, hasCustomName, slotsCount);
    }

    @Override
    public ItemStack getStackInSlot(final int slotIn) {
        return this.getActualStack(slotIn);
    }

    @Override
    public ItemStack decrStackSize(final int index, final int count) {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(final int index) {
        return null;
    }

    @Override
    public void setInventorySlotContents(final int index, final ItemStack stack) {}

    @Override
    public boolean isUseableByPlayer(final EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValidForSlot(final int index, final ItemStack stack) {
        return false;
    }

    public void updateStack(final int slot, @Nullable final ItemStack stack) {
        super.setInventorySlotContents(slot, stack);
    }
 
    @Nullable
    public ItemStack getActualStack(final int slot) {
        return super.getStackInSlot(slot);
    }
}
