/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
