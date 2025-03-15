/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeNOPSlot extends Slot {

    public ForgeNOPSlot(final IInventory inventory, final int slotIndex, final int pX,
            final int pY) {
        super(inventory, slotIndex, pX, pY);
    }

    @Override
    public boolean canTakeStack(@NonNull final EntityPlayer player) {
        return false;
    }
}
