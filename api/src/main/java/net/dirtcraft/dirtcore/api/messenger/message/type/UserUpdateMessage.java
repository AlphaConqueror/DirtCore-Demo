/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message.type;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a "user update" message.
 *
 * <p>Used to notify other servers of a change to a specific user.</p>
 */
public interface UserUpdateMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "user_update";

    /**
     * Gets the user the message is for.
     *
     * @return the user
     */
    @NonNull UUID getUserUniqueId();

}
