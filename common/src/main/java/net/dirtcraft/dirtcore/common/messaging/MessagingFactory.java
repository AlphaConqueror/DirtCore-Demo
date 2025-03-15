/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.messaging;

import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class MessagingFactory<P extends DirtCorePlugin> {

    private final P plugin;

    public MessagingFactory(final P plugin) {
        this.plugin = plugin;
    }

    public final InternalMessagingService getInstance() {
        throw new UnsupportedOperationException();
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
