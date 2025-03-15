/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform.gui.slot;

import net.minecraft.item.ItemStack;

public class ForgeSlotDelegate extends ForgeNOPSlot {

    public ForgeSlotDelegate(final ForgeNOPContainer inventory, final int slotIndex, final int pX,
            final int pY) {
        super(inventory, slotIndex, pX, pY);
    }

    @Override
    public ItemStack getStack() {
        return ((ForgeNOPContainer) this.inventory).getActualStack(this.getSlotIndex());
    }

    @Override
    public void putStack(final ItemStack stack) {
        ((ForgeNOPContainer) this.inventory).updateStack(this.getSlotIndex(), stack);
    }
}
