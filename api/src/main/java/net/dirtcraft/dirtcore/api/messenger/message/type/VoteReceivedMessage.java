/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message.type;

import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.messenger.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a "vote received" message.
 *
 * <p>Used to notify servers of a vote event that just occurred.</p>
 */
public interface VoteReceivedMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "vote_received";

    /**
     * Gets the service name.
     *
     * @return the service name, if present
     */
    @NonNull Optional<String> getServiceName();

    /**
     * Gets the unique id of the voter.
     *
     * @return the unique id of the voter
     */
    @NonNull UUID getVoterUniqueId();
}
