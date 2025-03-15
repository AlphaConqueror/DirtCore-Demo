/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger.message.type;

import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.api.messenger.message.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an "action log" message.
 *
 * <p>Used to dispatch live action log updates to other servers.</p>
 */
public interface ActionLogMessage extends Message {

    /**
     * The type as string.
     */
    String TYPE = "log";

    /**
     * Gets the action being sent
     *
     * @return the action
     */
    @NonNull Action getAction();
}
