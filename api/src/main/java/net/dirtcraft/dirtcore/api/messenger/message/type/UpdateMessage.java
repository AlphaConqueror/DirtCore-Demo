/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message.type;

import net.dirtcraft.dirtcore.api.messenger.message.Message;

/**
 * Represents an "update" message.
 *
 * <p>Used to notify other servers of general changes.</p>
 */
public interface UpdateMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "update";
}
