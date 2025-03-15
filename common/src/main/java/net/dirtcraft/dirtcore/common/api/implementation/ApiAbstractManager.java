/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.api.implementation;

import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public abstract class ApiAbstractManager<I, E, H> {

    protected final DirtCorePlugin plugin;
    protected final H handle;

    protected ApiAbstractManager(final DirtCorePlugin plugin, final H handle) {
        this.plugin = plugin;
        this.handle = handle;
    }

    protected abstract E proxy(I internal);

}
