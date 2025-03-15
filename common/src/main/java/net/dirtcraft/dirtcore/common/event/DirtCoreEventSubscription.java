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

package net.dirtcraft.dirtcore.common.event;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.EventSubscription;
import net.kyori.event.EventSubscriber;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple implementation of {@link EventSubscription}.
 *
 * @param <T> the event type
 */
public class DirtCoreEventSubscription<T extends DirtCoreEvent> implements EventSubscription<T>,
        EventSubscriber<T> {

    /**
     * The event bus which created this handler
     */
    private final AbstractEventBus<?> eventBus;

    /**
     * The event class
     */
    private final Class<T> eventClass;

    /**
     * The delegate "event handler"
     */
    private final Consumer<? super T> consumer;

    /**
     * The post order.
     */
    private final int postOrder;

    /**
     * If cancelled events should be posted to this subscriber.
     */
    private final boolean consumeCancelledEvents;

    /**
     * The plugin which "owns" this handler
     */
    private final @Nullable Object plugin;

    /**
     * If this handler is active
     */
    private final AtomicBoolean active = new AtomicBoolean(true);

    public DirtCoreEventSubscription(final AbstractEventBus<?> eventBus, final Class<T> eventClass,
            final Consumer<? super T> consumer, final int postOrder,
            final boolean consumeCancelledEvents, @Nullable final Object plugin) {
        this.eventBus = eventBus;
        this.eventClass = eventClass;
        this.consumer = consumer;
        this.postOrder = postOrder;
        this.consumeCancelledEvents = consumeCancelledEvents;
        this.plugin = plugin;
    }

    @Override
    public void invoke(@NonNull final T event) {
        try {
            this.consumer.accept(event);
        } catch (final Throwable t) {
            this.eventBus.getPlugin().getLogger().severe(String.format("Unable to pass event %s.",
                    event.getEventType().getSimpleName()), t);
        }
    }

    @Override
    public int postOrder() {
        return this.postOrder;
    }

    @Override
    public boolean consumeCancelledEvents() {
        return this.consumeCancelledEvents;
    }

    @Override
    public @NonNull Class<T> getEventClass() {
        return this.eventClass;
    }

    @Override
    public boolean isActive() {
        return this.active.get();
    }

    @Override
    public void close() {
        // already unregistered
        if (!this.active.getAndSet(false)) {
            return;
        }

        this.eventBus.unregisterHandler(this);
    }

    @Override
    public @NonNull Consumer<? super T> getHandler() {
        return this.consumer;
    }

    public @Nullable Object getPlugin() {
        return this.plugin;
    }
}
