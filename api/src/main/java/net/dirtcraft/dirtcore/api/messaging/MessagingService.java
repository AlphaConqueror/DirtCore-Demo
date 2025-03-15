/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messaging;

import net.dirtcraft.dirtcore.api.DirtCore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A means to send messages to other servers using the platforms networking
 */
public interface MessagingService {

    /**
     * Gets the name of this messaging service
     *
     * @return the name of this messaging service
     */
    @NonNull String getName();

    /**
     * Uses the messaging service to inform other servers about a general
     * change.
     *
     * <p>The standard response by other servers will be to execute a overall
     * sync of all live data, equivalent to calling
     * {@link DirtCore#runUpdateTask()}.</p>
     *
     * <p>This will push the update asynchronously, and this method will return
     * immediately. Note that this method will not cause an update to be
     * processed on the local server.</p>
     */
    void pushUpdate();
}
