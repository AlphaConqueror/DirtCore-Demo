/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.event.type;

import java.util.concurrent.atomic.AtomicReference;
import net.dirtcraft.dirtcore.api.event.util.Param;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that has a result.
 *
 * @param <T> the type of the result
 * @since 5.3
 */
public interface ResultEvent<T> {

    /**
     * Gets an {@link AtomicReference} containing the result.
     *
     * @return the result
     */
    @Param(-1)
    @NonNull AtomicReference<T> result();

    /**
     * Gets if a result has been set for the event.
     *
     * @return if there is a result
     */
    default boolean hasResult() {
        return this.result().get() != null;
    }

}
