/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot;

import java.util.function.Consumer;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.AbstractSlot;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangingSlot extends AbstractSlot<ChangingSlot> {

    @NonNull
    protected final Consumer<@NonNull SlotContext> contextConsumer;

    protected ChangingSlot(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction,
            @NonNull final Consumer<@NonNull SlotContext> contextConsumer) {
        super(slotId, itemLikeFunction);
        this.contextConsumer = contextConsumer;
    }

    @Override
    public void onClick(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        this.contextConsumer.accept(context);
        this.update(factory, context);

        final TaskContext taskContext = context.getTaskContext();
        final Runnable runnable = () -> context.getPlayer().playSound(Sound.GUI_SUCCESS);

        if (taskContext == null) {
            runnable.run();
        } else {
            taskContext.queue(runnable);
        }
    }

    @Override
    protected ChangingSlot getThis() {
        return this;
    }
}
