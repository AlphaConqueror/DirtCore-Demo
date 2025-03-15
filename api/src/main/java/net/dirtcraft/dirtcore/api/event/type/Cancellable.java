/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.event.type;

import java.util.concurrent.atomic.AtomicBoolean;
import net.dirtcraft.dirtcore.api.event.util.Param;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that can be cancelled
 */
public interface Cancellable {

    /**
     * Gets an {@link AtomicBoolean} holding the cancellation state of the event
     *
     * @return the cancellation
     */
    @Param(-1)
    @NonNull AtomicBoolean cancellationState();

    /**
     * Returns true if the event is currently cancelled.
     *
     * @return if the event is cancelled
     */
    default boolean isCancelled() {
        return this.cancellationState().get();
    }

    /**
     * Returns true if the event is not currently cancelled.
     *
     * @return if the event is not cancelled
     */
    default boolean isNotCancelled() {
        return !this.isCancelled();
    }

    /**
     * Sets the cancellation state of the event.
     *
     * @param cancelled the new state
     * @return the previous state
     */
    default boolean setCancelled(final boolean cancelled) {
        return this.cancellationState().getAndSet(cancelled);
    }

}
