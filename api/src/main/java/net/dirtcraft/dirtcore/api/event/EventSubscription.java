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
