/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
