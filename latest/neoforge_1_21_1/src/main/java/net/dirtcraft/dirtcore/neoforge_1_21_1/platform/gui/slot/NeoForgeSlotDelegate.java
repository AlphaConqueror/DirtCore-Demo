/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NeoForgeSlotDelegate extends NeoForgeNOPSlot {

    public NeoForgeSlotDelegate(final Container pContainer, final int pSlot, final int pX,
            final int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public @NotNull ItemStack getItem() {
        return ((NeoForgeNOPContainer) this.container).getActualStack(this.index);
    }

    @Override
    public void set(@NotNull final ItemStack stack) {
        ((NeoForgeNOPContainer) this.container).updateStack(this.index, stack);
    }
}
