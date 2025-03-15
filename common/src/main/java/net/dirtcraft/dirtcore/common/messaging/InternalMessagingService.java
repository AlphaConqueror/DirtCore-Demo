/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.messaging;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.Messenger;
import net.dirtcraft.dirtcore.api.messenger.MessengerProvider;
import net.dirtcraft.dirtcore.common.cache.BufferedRequest;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RestrictiveAction;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface InternalMessagingService {

    /**
     * Gets the name of this messaging service
     *
     * @return the name of this messaging service
     */
    String getName();

    Messenger getMessenger();

    MessengerProvider getMessengerProvider();

    /**
     * Closes the messaging service
     */
    void close();

    /**
     * Gets the buffer for sending updates to other servers
     *
     * @return the update buffer
     */
    BufferedRequest<Void> getUpdateBuffer();

    /**
     * Uses the messaging service to inform other servers about a general
     * change.
     */
    void pushUpdate();

    /**
     * Pushes an update for a specific user.
     *
     * @param user the user
     */
    void pushUserUpdate(@NonNull User user);

    /**
     * Pushes a log entry to connected servers.
     *
     * @param logEntry the log entry
     */
    void pushLog(@NonNull LogEntity logEntry);

    /**
     * Pushes an incident to connected servers.
     *
     * @param restrictiveAction the restrictive action
     */
    void pushIncident(@NonNull RestrictiveAction restrictiveAction);

    /**
     * Pushes a network chat to connected servers.
     *
     * @param uniqueId the unique id of the sender
     * @param message  the message
     * @param staff    is staff chat
     */
    void pushNetworkChat(@NonNull UUID uniqueId, @NonNull String message, boolean staff);
}
