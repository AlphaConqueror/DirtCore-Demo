/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.loader;

import net.dirtcraft.dirtcore.common.loader.event.LoaderEventDispatcher;

/**
 * Minimal bootstrap plugin, called by the loader plugin.
 */
public interface LoaderBootstrap<B, I, W> {

    /**
     * Called on load.
     */
    void onLoad();

    /**
     * Gets the loader event dispatcher.
     *
     * @return the event dispatcher
     */
    LoaderEventDispatcher<B, I, W> getEventDispatcher();
}
