/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.Messenger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.NonExtendable;

/**
 * Represents a message sent received via a {@link Messenger}.
 */
@NonExtendable
public interface Message {

    /**
     * Gets the unique id associated with this message.
     *
     * <p>This ID is used to ensure a single server instance doesn't process
     * the same message twice.</p>
     *
     * @return the id of the message
     */
    @NonNull UUID getId();
}
