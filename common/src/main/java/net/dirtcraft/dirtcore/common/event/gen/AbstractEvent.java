/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.event.gen;

import java.lang.invoke.MethodHandles;
import net.dirtcraft.dirtcore.api.DirtCore;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Abstract implementation of {@link DirtCoreEvent}.
 */
public abstract class AbstractEvent implements DirtCoreEvent {

    private final DirtCore api;

    protected AbstractEvent(final DirtCore api) {
        this.api = api;
    }

    @Override
    public @NonNull DirtCore getDirtCore() {
        return this.api;
    }

    // Overridden by the subclass. Used by GeneratedEventClass to get setter method handles.
    public MethodHandles.Lookup mhl() {
        throw new UnsupportedOperationException();
    }
}
