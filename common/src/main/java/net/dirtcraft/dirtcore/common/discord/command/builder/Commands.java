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

package net.dirtcraft.dirtcore.common.discord.command.builder;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Commands {

    /**
     * Creates a command builder for slash commands.
     *
     * @param name        the name
     * @param description the description
     * @return the command builder
     */
    static RootCommandBuilder slash(@NonNull final String name, @NonNull final String description) {
        return new RootCommandBuilder(name, description);
    }

    /**
     * Creates a subcommand group builder.
     *
     * @param name        the name
     * @param description the description
     * @return the subcommand group builder
     */
    static SubcommandGroupBuilder subGroupCommand(@NonNull final String name,
            @NonNull final String description) {
        return new SubcommandGroupBuilder(name, description);
    }

    /**
     * Creates a subcommand builder.
     *
     * @param name        the name
     * @param description the description
     * @return the subcommand builder
     */
    static SubcommandBuilder subCommand(@NonNull final String name,
            @NonNull final String description) {
        return new SubcommandBuilder(name, description);
    }

    /**
     * Creates an option builder.
     *
     * @param name        the name
     * @param description the description
     * @param type        the type
     * @return the option builder
     */
    static OptionBuilder option(final String name, @NonNull final String description,
            @NonNull final OptionType type) {
        return new OptionBuilder(name, description, type);
    }
}
