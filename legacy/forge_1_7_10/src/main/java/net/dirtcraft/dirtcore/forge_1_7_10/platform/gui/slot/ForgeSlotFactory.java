/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform.gui.slot;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeSlotFactory implements SlotFactory {

    @NonNull
    private final DirtCoreForgePlugin plugin;
    @NonNull
    private final ForgeNOPContainer inventory;

    public ForgeSlotFactory(@NonNull final DirtCoreForgePlugin plugin,
            @NonNull final ForgeNOPContainer inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
    }

    @Override
    public void updateSlot(@NonNull final SlotContext context, @NonNull final Slot<?> slot) {
        this.inventory.updateStack(slot.getSlotId(), this.plugin.getPlatformFactory()
                .transformItemStack(slot.getItemStack(this.plugin, context)));
    }
}
