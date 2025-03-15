/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger;

import net.dirtcraft.dirtcore.api.messenger.message.Message;
import net.dirtcraft.dirtcore.api.messenger.message.OutgoingMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * Represents an object which dispatches {@link OutgoingMessage}s.
 */
@OverrideOnly
public interface Messenger extends AutoCloseable {

    /**
     * Performs the necessary action to dispatch the message using the means
     * of the messenger.
     *
     * <p>The outgoing message instance is guaranteed to be an instance of one
     * of the interfaces extending {@link Message} in the
     * 'api.messenger.message.type' package.</p>
     *
     * <p>3rd party implementations are encouraged to implement this method with consideration
     * that new types may be added in the future.</p>
     *
     * <p>This call is always made async.</p>
     *
     * @param outgoingMessage the outgoing message
     */
    void sendOutgoingMessage(@NonNull OutgoingMessage outgoingMessage);

    /**
     * Performs the necessary action to gracefully shut down the messenger.
     */
    @Override
    default void close() {

    }
}
