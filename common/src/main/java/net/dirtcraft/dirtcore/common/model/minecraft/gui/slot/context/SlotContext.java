/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context;

import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserSettingsEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;

/**
 * The context provided for slot updates.
 */
public interface SlotContext {

    /**
     * Provides the slot context builder.
     *
     * @param player the player
     * @return the builder
     */
    @NonNull
    static Builder builder(@NonNull final Player player) {
        return new SlotContextImpl.BuilderImpl(player);
    }

    /**
     * Gets the player.
     *
     * @return the player
     */
    @NonNull Player getPlayer();

    /**
     * Gets the click type.
     *
     * @return the click type
     */
    Slot.@Nullable ClickType getClickType();

    /**
     * Gets the task context.
     *
     * @return the task context
     */
    @Nullable TaskContext getTaskContext();

    /**
     * Sets the task context.
     *
     * @param context the task context
     */
    void setTaskContext(@NonNull TaskContext context);

    /**
     * Gets the task context.
     *
     * @return the task context
     * @throws AssertionError if no task context has been provided
     */
    @NonNull TaskContext getTaskContextOrException() throws AssertionError;

    /**
     * Gets the user settings.
     * Creates user settings if not available.
     *
     * @return the user settings
     */
    @NonNull UserSettingsEntity getOrCreateUserSettings();

    /**
     * Gets the user settings.
     *
     * @return the user settings
     * @throws AssertionError if no user settings have been provided
     */
    @NonNull UserSettingsEntity getUserSettingsOrException() throws AssertionError;

    /**
     * Sets the user settings.
     *
     * @param userSettings the user settings
     */
    void setUserSettings(@NonNull UserSettingsEntity userSettings);

    /**
     * The slot context builder interface.
     */
    interface Builder {

        /**
         * Provides the click type.
         *
         * @param clickType the click type
         * @return the builder
         */
        @NonNull Builder withClickType(final Slot.@NonNull ClickType clickType);

        /**
         * Provides the task context.
         *
         * @param context the task context
         * @return the builder
         */
        @NonNull Builder withTaskContext(final @NonNull TaskContext context);

        /**
         * Provides the user settings.
         *
         * @param userSettings the user settings
         * @return the builder
         */
        @NonNull Builder withUserSettings(final @NonNull UserSettingsEntity userSettings);

        /**
         * Builds the slot context.
         *
         * @return the slot context
         */
        @NonNull
        @CheckReturnValue
        SlotContext build();
    }
}
