/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class NeoForgeNOPSlot extends Slot {

    public NeoForgeNOPSlot(final Container pContainer, final int pSlot, final int pX,
            final int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean allowModification(final @NotNull Player pPlayer) {
        return false;
    }
}
