/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeSlotFactory implements SlotFactory {

    @NonNull
    private final DirtCoreNeoForgePlugin plugin;
    @NonNull
    private final NeoForgeNOPContainer inventory;

    public NeoForgeSlotFactory(@NonNull final DirtCoreNeoForgePlugin plugin,
            @NonNull final NeoForgeNOPContainer inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
    }

    @Override
    public void updateSlot(@NonNull final SlotContext context, @NonNull final Slot<?> slot) {
        this.inventory.updateStack(slot.getSlotId(), this.plugin.getPlatformFactory()
                .transformItemStack(slot.getItemStack(this.plugin, context)));
    }
}
