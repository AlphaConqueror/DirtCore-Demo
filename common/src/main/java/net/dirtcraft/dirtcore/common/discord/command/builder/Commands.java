/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
