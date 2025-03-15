/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.event;

/**
 * A utility for registering Minecraft listeners for methods.
 */
public interface EventBusFacade {

    /**
     * Register listeners for all methods.
     *
     * @param target the target listener
     */
    void register(Object target);

    /**
     * Unregister previously registered listeners on the target object.
     *
     * @param target the target listener
     */
    void unregister(final Object target);

    /**
     * Unregister all listeners created through this interface.
     */
    void unregisterAll();
}
