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
 * Represents a "network chat" message.
 *
 * <p>Used to send chat messages network wide.</p>
 */
public interface NetworkChatMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "network_chat";

    /**
     * Gets the unique id of the sender.
     *
     * @return the unique id
     */
    @NonNull UUID getUniqueId();

    /**
     * Gets the message.
     *
     * @return the message
     */
    @NonNull String getMessage();

    /**
     * If the message is a staff message.
     *
     * @return true, if the message is a staff message
     */
    boolean isStaff();

    /**
     * The server identifier the message was sent from.
     *
     * @return the server identifier
     */
    @NonNull String getServer();
}
