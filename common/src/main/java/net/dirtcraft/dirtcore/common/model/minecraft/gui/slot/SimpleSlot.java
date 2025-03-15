/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot;

import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.AbstractSlot;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SimpleSlot extends AbstractSlot<SimpleSlot> {

    protected SimpleSlot(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction) {
        super(slotId, itemLikeFunction);
    }

    @Override
    public void onClick(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        final TaskContext taskContext = context.getTaskContext();
        final Runnable runnable =
                () -> this.listeners.forEach(slot -> slot.onListenerUpdate(factory, context, this));

        if (taskContext == null) {
            runnable.run();
        } else {
            taskContext.queue(runnable);
        }
    }

    @Override
    protected SimpleSlot getThis() {
        return this;
    }
}
