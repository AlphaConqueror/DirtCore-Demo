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

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.EventBus;
import net.dirtcraft.dirtcore.api.event.EventSubscription;
import net.dirtcraft.dirtcore.api.event.type.Cancellable;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.event.EventSubscriber;
import net.kyori.event.SimpleEventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractEventBus<P> implements EventBus, AutoCloseable {

    /**
     * The plugin instance
     */
    private final DirtCorePlugin plugin;

    /**
     * The api provider instance
     */
    private final DirtCoreApiProvider apiProvider;

    /**
     * The delegate event bus
     */
    private final Bus bus = new Bus();

    protected AbstractEventBus(final DirtCorePlugin plugin, final DirtCoreApiProvider apiProvider) {
        this.plugin = plugin;
        this.apiProvider = apiProvider;
    }

    /**
     * Checks that the given plugin object is a valid plugin instance for the platform
     *
     * @param plugin the object
     * @return a plugin
     * @throws IllegalArgumentException if the plugin is invalid
     */
    protected abstract P checkPlugin(@NonNull Object plugin) throws IllegalArgumentException;

    public DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    public DirtCoreApiProvider getApiProvider() {
        return this.apiProvider;
    }

    public void post(final DirtCoreEvent event) {
        this.bus.post(event);
    }

    public boolean shouldPost(@NonNull final Class<? extends DirtCoreEvent> eventClass) {
        return this.bus.hasSubscribers(eventClass);
    }

    public void subscribe(@NonNull final DirtCoreEventListener listener) {
        listener.bind(this);
    }

    @Override
    public <T extends DirtCoreEvent> @NonNull EventSubscription<T> subscribe(
            @NonNull final Class<T> eventClass, @NonNull final Consumer<? super T> handler,
            final int postOrder, final boolean consumeCancelledEvents) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(handler, "handler");
        return this.registerSubscription(eventClass, handler, postOrder, consumeCancelledEvents,
                null);
    }

    @Override
    public <T extends DirtCoreEvent> @NonNull EventSubscription<T> subscribe(
            @NonNull final Object plugin, @NonNull final Class<T> eventClass,
            @NonNull final Consumer<? super T> handler, final int postOrder,
            final boolean consumeCancelledEvents) {
        return this.registerSubscription(eventClass, handler, postOrder, consumeCancelledEvents,
                this.checkPlugin(plugin));
    }

    @Override
    public <T extends DirtCoreEvent> @NonNull Set<EventSubscription<T>> getSubscriptions(
            @NonNull final Class<T> eventClass) {
        return this.bus.getHandlers(eventClass);
    }

    /**
     * Removes a specific handler from the bus
     *
     * @param handler the handler to remove
     */
    public void unregisterHandler(@NonNull final DirtCoreEventSubscription<?> handler) {
        this.bus.unregister(handler);
    }

    @Override
    public void close() {
        this.bus.unregisterAll();
    }

    /**
     * Removes all handlers for a specific plugin
     *
     * @param plugin the plugin
     */
    protected void unregisterHandlers(@NonNull final P plugin) {
        this.bus.unregister(sub -> ((DirtCoreEventSubscription<?>) sub).getPlugin() == plugin);
    }

    private <T extends DirtCoreEvent> EventSubscription<T> registerSubscription(
            final Class<T> eventClass, final Consumer<? super T> handler, final int postOrder,
            final boolean consumeCancelledEvents, final Object plugin) {
        if (!eventClass.isInterface()) {
            throw new IllegalArgumentException("class " + eventClass + " is not an interface");
        }
        if (!DirtCoreEvent.class.isAssignableFrom(eventClass)) {
            throw new IllegalArgumentException(
                    "class " + eventClass.getName() + " does not implement DirtCoreEvent");
        }

        final DirtCoreEventSubscription<T> eventHandler =
                new DirtCoreEventSubscription<>(this, eventClass, handler, postOrder,
                        consumeCancelledEvents, plugin);
        this.bus.register(eventClass, eventHandler);

        return eventHandler;
    }

    private static final class Bus extends SimpleEventBus<DirtCoreEvent> {

        Bus() {
            super(DirtCoreEvent.class);
        }

        @SuppressWarnings("unchecked")
        public <T extends DirtCoreEvent> Set<EventSubscription<T>> getHandlers(
                final Class<T> eventClass) {
            return super.subscribers().values().stream().filter(s -> s instanceof EventSubscription
                            && ((EventSubscription<?>) s).getEventClass().isAssignableFrom(eventClass))
                    .map(s -> (EventSubscription<T>) s).collect(Collectors.toSet());
        }

        @Override
        protected boolean eventCancelled(@NonNull final DirtCoreEvent event) {
            return event instanceof Cancellable && ((Cancellable) event).isCancelled();
        }

        @Override
        protected boolean shouldPost(@NonNull final DirtCoreEvent event,
                @NonNull final EventSubscriber<?> subscriber) {
            return subscriber.consumeCancelledEvents() || !this.eventCancelled(event);
        }
    }
}
