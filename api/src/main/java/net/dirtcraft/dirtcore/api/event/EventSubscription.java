/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.event;

import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a subscription to a {@link DirtCoreEvent}.
 *
 * @param <T> the event class
 */
public interface EventSubscription<T extends DirtCoreEvent> extends AutoCloseable {

    /**
     * Gets the class this handler is listening to
     *
     * @return the event class
     */
    @NonNull Class<T> getEventClass();

    /**
     * Returns true if this handler is active
     *
     * @return true if this handler is still active
     */
    boolean isActive();

    /**
     * Unregisters this handler from the event bus.
     */
    @Override
    void close();

    /**
     * Gets the event consumer responsible for handling the event
     *
     * @return the event consumer
     */
    @NonNull Consumer<? super T> getHandler();
}
