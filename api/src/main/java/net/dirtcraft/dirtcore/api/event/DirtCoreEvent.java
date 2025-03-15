/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.event;

import net.dirtcraft.dirtcore.api.DirtCore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A superinterface for all DirtCore events.
 */
public interface DirtCoreEvent {

    /**
     * Get the API instance this event was dispatched from
     *
     * @return the api instance
     */
    @NonNull DirtCore getDirtCore();

    /**
     * Gets the type of the event.
     *
     * @return the type of the event
     */
    @NonNull Class<? extends DirtCoreEvent> getEventType();
}
