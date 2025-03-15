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

package net.dirtcraft.dirtcore.forge_1_12_2.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import net.dirtcraft.dirtcore.common.loader.JarInJarClassLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * A utility for registering Forge listeners for methods in a jar-in-jar.
 *
 * <p>This differs from {@link EventBus#register} as reflection is used for invoking the
 * registered listeners
 * instead of ASM, which is incompatible with {@link JarInJarClassLoader}</p>
 */
public class ForgeEventBusFacade {

    private static final Method EVENT_BUS_REGISTER;

    static {
        try {
            EVENT_BUS_REGISTER =
                    EventBus.class.getDeclaredMethod("register", Class.class, Object.class,
                            Method.class, ModContainer.class);
            EVENT_BUS_REGISTER.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<ListenerRegistration> listeners = new ArrayList<>();

    private static Class<?> determineListenerType(final Method method) {
        // Get the parameter types, this includes generic information which is required for
        // GenericEvent
        final Type[] parameterTypes = method.getGenericParameterTypes();

        if (parameterTypes.length != 1) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation. It has "
                            + parameterTypes.length + " arguments, but event handler methods "
                            + "require a single argument only.");
        }

        final Type parameterType = parameterTypes[0];
        final Class<?> eventType;

        if (parameterType instanceof Class) { // Non-generic event
            eventType = (Class<?>) parameterType;
        } else {
            throw new UnsupportedOperationException(
                    "Parameter Type " + parameterType.getClass() + " is not supported");
        }

        // Ensure 'eventType' is a subclass of event
        if (!Event.class.isAssignableFrom(eventType)) {
            throw new IllegalArgumentException(
                    "Method " + method + " has @SubscribeEvent annotation, "
                            + "but takes an argument that is not an Event subtype: " + eventType);
        }

        return eventType;
    }

    /**
     * Handles casting generics for {@link EventBus#register(Class, Object, Method, ModContainer)}.
     */
    private static void addListener(final EventBus eventBus, final Object target,
            final Method method, final Class<?> eventType) {
        try {
            EVENT_BUS_REGISTER.invoke(eventBus, eventType, target, method,
                    Loader.instance().activeModContainer());
        } catch (final IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Register listeners for all methods annotated with {@link SubscribeEvent} on the target
     * object.
     */
    public void register(final Object target) {
        for (final Method method : target.getClass().getMethods()) {
            // ignore static methods
            if (Modifier.isStatic(method.getModifiers())) {
                continue;
            }

            // methods require a SubscribeEvent annotation in order to be registered
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }

            final Class<?> type = determineListenerType(method);
            final EventBus eventBus = MinecraftForge.EVENT_BUS;

            addListener(eventBus, target, method, type);
            this.listeners.add(new ListenerRegistration(eventBus, target));
        }
    }

    /**
     * Unregister previously registered listeners on the target object.
     *
     * @param target the target listener
     */
    public void unregister(final Object target) {
        this.listeners.removeIf(listener -> {
            if (listener.target == target) {
                listener.close();
                return true;
            } else {
                return false;
            }
        });
    }

    /**
     * Unregister all listeners created through this interface.
     */
    public void unregisterAll() {
        for (final ListenerRegistration listener : this.listeners) {
            listener.close();
        }

        this.listeners.clear();
    }

    /**
     * A listener registration.
     */
    private static final class ListenerRegistration implements AutoCloseable {

        /**
         * The event bus that the invoker was registered to
         */
        private final EventBus eventBus;
        /**
         * The target listener class
         */
        private final Object target;

        private ListenerRegistration(final EventBus eventBus, final Object target) {
            this.eventBus = eventBus;
            this.target = target;
        }

        @Override
        public void close() {
            this.eventBus.unregister(this.target);
        }
    }
}
