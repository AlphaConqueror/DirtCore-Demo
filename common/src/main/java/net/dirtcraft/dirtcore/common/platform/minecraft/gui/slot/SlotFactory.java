/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe slot instance.
 */
public interface SlotFactory {

    void updateSlot(@NonNull SlotContext context, @NonNull Slot<?> slot);
}
