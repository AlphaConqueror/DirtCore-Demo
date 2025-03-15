/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ForgeSlotDelegate extends ForgeNOPSlot {

    public ForgeSlotDelegate(final Container pContainer, final int pSlot, final int pX,
            final int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return ((ForgeNOPContainer) this.container).getActualStack(this.index);
    }

    @Override
    public void set(@NotNull final ItemStack stack) {
        ((ForgeNOPContainer) this.container).updateStack(this.index, stack);
    }
}
