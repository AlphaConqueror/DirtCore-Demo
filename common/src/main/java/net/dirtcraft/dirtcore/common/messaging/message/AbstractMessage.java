/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.messaging.message;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractMessage implements Message {

    private final UUID id;

    protected AbstractMessage(final UUID id) {
        this.id = id;
    }

    @Override
    public @NonNull UUID getId() {
        return this.id;
    }

}
