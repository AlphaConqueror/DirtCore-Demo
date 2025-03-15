/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.actionlog;

import java.util.SortedSet;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Represents the internal DirtCore log.
 *
 * <p>The returned instance provides a copy of the data at the time of retrieval.</p>
 *
 * <p>Any changes made to log entries will only apply to this instance of the log.
 * You can add to the log using the {@link ActionLogger}, and then request an updated copy.</p>
 *
 * <p>All methods are thread safe, and return immutable and thread safe collections.</p>
 */
public interface ActionLog {

    /**
     * Gets the {@link Action}s that make up this log.
     *
     * @return the content
     */
    @NonNull @Unmodifiable SortedSet<Action> getContent();

    /**
     * Gets the entries in the log performed by the given actor.
     *
     * @param actor the uuid of the actor to filter by
     * @return the content for the given actor
     */
    @NonNull @Unmodifiable SortedSet<Action> getContent(@NonNull UUID actor);
}
