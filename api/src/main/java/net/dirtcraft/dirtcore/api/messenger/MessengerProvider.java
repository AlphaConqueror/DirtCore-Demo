/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.messenger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.ApiStatus.OverrideOnly;

/**
 * Represents a provider for {@link Messenger} instances.
 */
@OverrideOnly
public interface MessengerProvider {

    /**
     * Gets the name of this provider.
     *
     * @return the provider name
     */
    @NonNull String getName();

    /**
     * Creates and returns a new {@link Messenger} instance, which passes
     * incoming messages to the provided {@link IncomingMessageConsumer}.
     *
     * <p>As the agent should pass incoming messages to the given consumer,
     * this method should always return a new object.</p>
     *
     * @param incomingMessageConsumer the consumer the new instance should pass
     *                                incoming messages to
     * @return a new messenger agent instance
     */
    @NonNull Messenger obtain(@NonNull IncomingMessageConsumer incomingMessageConsumer);

}
