/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of {@link Slot} using a {@link SlotFactory}.
 *
 * @param <T> the slot type
 */
public abstract class AbstractSlot<T extends Slot<T>> implements Slot<T> {

    protected final int slotId;
    @NonNull
    protected final List<Slot<?>> listeners = new ArrayList<>();
    @NonNull
    protected Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction;
    @NonNull
    protected Function<@NonNull SlotContext, @NonNull Boolean> requirement = context -> true;
    protected boolean taskContextRequirement = false;

    protected AbstractSlot(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction) {
        this.slotId = slotId;
        this.itemLikeFunction = itemLikeFunction;
    }

    /**
     * Called upon click.
     *
     * @param factory the factory
     * @param context the context
     */
    protected abstract void onClick(@NonNull final SlotFactory factory,
            @NonNull final SlotContext context);

    /**
     * Gets this slot instance.
     *
     * @return this slot
     */
    protected abstract T getThis();

    @Override
    public int getSlotId() {
        return this.slotId;
    }

    @Override
    public @NonNull ItemStack getItemStack(@NonNull final DirtCorePlugin plugin,
            @NonNull final SlotContext context) {
        return this.itemLikeFunction.apply(context).build(plugin);
    }

    @Override
    public @NonNull T requires(
            @NonNull final Function<@NonNull SlotContext, @NonNull Boolean> requirement) {
        this.requirement = requirement;
        return this.getThis();
    }

    @Override
    public boolean getTaskContextRequirement() {
        return this.taskContextRequirement;
    }

    @Override
    public @NonNull T requiresTaskContext() {
        this.taskContextRequirement = true;
        return this.getThis();
    }

    @Override
    public @NonNull T withListener(@NonNull final Slot<?> slot) {
        this.listeners.add(slot);
        return this.getThis();
    }

    @Override
    public void onListenerUpdate(@NonNull final SlotFactory factory,
            @NonNull final SlotContext context, @NonNull final Slot<?> slot) {
        factory.updateSlot(context, this);
    }

    @Override
    public void click(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        if (this.requirement.apply(context)) {
            this.onClick(factory, context);
            return;
        }

        // requirement not met
        context.getPlayer().playSound(Sound.GUI_FAILURE);
    }

    @Override
    public void update(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        final TaskContext taskContext = context.getTaskContext();
        final Runnable runnable = () -> {
            factory.updateSlot(context, this);
            this.listeners.forEach(slot -> slot.onListenerUpdate(factory, context, this));
        };

        if (taskContext == null) {
            runnable.run();
        } else {
            taskContext.queue(runnable);
        }
    }
}
