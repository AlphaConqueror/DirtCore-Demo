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
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a slot within the common slot implementations.
 *
 * @param <T> the slot type
 */
public interface Slot<T extends Slot<T>> {

    /**
     * Creates a simple slot.
     *
     * @param slotId           the slot id
     * @param itemLikeFunction the function providing the item stack in this slot
     * @return a simple slot
     */
    @NonNull
    static SimpleSlot of(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction) {
        return new SimpleSlot(slotId, itemLikeFunction);
    }

    /**
     * Creates a changing slot.
     *
     * @param slotId           the slot id
     * @param itemLikeFunction the function providing the item stack in this slot
     * @param contextConsumer  the consumer accepted upon change (on click)
     * @return a changing slot
     */
    @NonNull
    static ChangingSlot ofChanging(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction,
            @NonNull final Consumer<@NonNull SlotContext> contextConsumer) {
        return new ChangingSlot(slotId, itemLikeFunction, contextConsumer);
    }

    /**
     * Creates a connected slot.
     *
     * @param slotId               the slot id
     * @param itemLikeFunction     the function providing the item stack in this slot
     * @param contextConsumer      the consumer accepted upon change (on click)
     * @param connectedSlotHandler the slot handler listening to slots interactions
     * @return a connected slot
     */
    @NonNull
    static ConnectedSlot ofConnected(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction,
            @NonNull final Consumer<@NonNull SlotContext> contextConsumer,
            @NonNull final ConnectedSlotHandler connectedSlotHandler) {
        return new ConnectedSlot(slotId, itemLikeFunction, contextConsumer, connectedSlotHandler);
    }

    /**
     * Gets the slot id.
     *
     * @return the slot id
     */
    int getSlotId();

    /**
     * Gets the item stack.
     *
     * @param plugin  the plugin
     * @param context the context
     * @return the item stack
     */
    @NonNull ItemStack getItemStack(@NonNull DirtCorePlugin plugin, @NonNull SlotContext context);

    /**
     * Sets the requirement to interact with this slot.
     *
     * @param requirement the requirement
     * @return this slot
     */
    @NonNull T requires(@NonNull Function<@NonNull SlotContext, @NonNull Boolean> requirement);

    /**
     * Gets task context requirement.
     *
     * @return if a task context is required
     */
    boolean getTaskContextRequirement();

    /**
     * Sets the task context requirement.
     *
     * @return this slot
     */
    @NonNull T requiresTaskContext();

    /**
     * Adds a slot listening to the slots interactions.
     *
     * @param slot the slot
     * @return this slot
     */
    @NonNull T withListener(@NonNull Slot<?> slot);

    /**
     * Called upon listener update.
     * Called in separate method to prevent stack overflow upon bidirectional listening.
     *
     * @param factory the factory
     * @param context the context
     * @param slot    the slot
     */
    void onListenerUpdate(@NonNull SlotFactory factory, @NonNull SlotContext context,
            @NonNull Slot<?> slot);

    /**
     * Clicks this slot.
     *
     * @param factory the factory
     * @param context the context
     */
    void click(@NonNull SlotFactory factory, @NonNull SlotContext context);

    /**
     * Updates this slot.
     *
     * @param factory the factory
     * @param context the context
     */
    void update(@NonNull SlotFactory factory, @NonNull SlotContext context);

    /**
     * The click type.
     */
    enum ClickType {
        /**
         * Left click.
         */
        LEFT,
        /**
         * Right click.
         */
        RIGHT,
        /**
         * Click type unknown.
         */
        UNKNOWN
    }
}
