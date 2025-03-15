/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message.type;

import net.dirtcraft.dirtcore.api.messenger.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an "incident" message.
 *
 * <p>Used to notify other servers of a incident that just occurred.</p>
 */
public interface IncidentMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "incident";

    /**
     * Gets the incident id the message is for.
     *
     * @return the incident id
     */
    @NonNull String getIncidentId();
}
