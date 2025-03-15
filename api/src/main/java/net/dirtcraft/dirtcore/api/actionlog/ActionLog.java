/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
