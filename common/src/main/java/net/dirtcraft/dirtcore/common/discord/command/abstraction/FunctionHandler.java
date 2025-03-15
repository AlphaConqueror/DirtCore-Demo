/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.abstraction;

import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Contains a function to be handled.
 */
public interface FunctionHandler {

    /**
     * If a function has been assigned.
     *
     * @return true, if assigned
     */
    boolean hasFunction();

    /**
     * The function to be executed.
     * Is an alternative function, if {@link #hasFunction()} is false.
     *
     * @return the function
     */
    @NonNull CommandFunction getFunction();

    /**
     * The runnable to be executed after the main function.
     *
     * @return the runnable
     */
    @Nullable Runnable getExecuteAfter();

    @NonNull
    default CommandFunction getAlternativeFunction() {
        return interactionContext -> interactionContext.getEvent().getHook()
                .sendMessageEmbeds(DiscordEmbeds.THIS_SHOULDNT_HAVE_HAPPENED.build())
                .setEphemeral(true);
    }
}
