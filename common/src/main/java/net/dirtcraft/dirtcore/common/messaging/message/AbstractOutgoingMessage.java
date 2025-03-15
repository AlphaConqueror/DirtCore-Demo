/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.messaging.message;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.message.OutgoingMessage;

public abstract class AbstractOutgoingMessage extends AbstractMessage implements OutgoingMessage {

    protected AbstractOutgoingMessage(final UUID id) {
        super(id);
    }
}
