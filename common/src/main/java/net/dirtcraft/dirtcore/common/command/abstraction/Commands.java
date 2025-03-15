/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction;

import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.AliasesArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.LiteralArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.RequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionRequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Commands {

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the
     * brigadier {@link LiteralArgumentBuilder#literal(String)} method is that it is typed to
     * {@link Sender}.
     */
    static LiteralArgumentBuilder<DirtCorePlugin, Sender> literal(final String pName) {
        return LiteralArgumentBuilder.literal(pName);
    }

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the
     * brigadier {@link RequiredArgumentBuilder#argument} method is that it is typed to
     * {@link Sender}.
     */
    static <T> RequiredArgumentBuilder<DirtCorePlugin, Sender, T> argument(final String pName,
            final ArgumentType<DirtCorePlugin, T> pType) {
        return RequiredArgumentBuilder.argument(pName, pType);
    }

    /**
     * Creates a new option without a type.
     */
    static OptionArgumentBuilder<DirtCorePlugin, Sender, ?> option(final String name) {
        return OptionArgumentBuilder.option(name);
    }

    /**
     * Creates a new option with a type.
     */
    static <T> OptionRequiredArgumentBuilder<DirtCorePlugin, Sender, T> option(final String name,
            final ArgumentType<DirtCorePlugin, T> type) {
        return OptionRequiredArgumentBuilder.option(name, type);
    }

    /**
     * Creates a new argument. Intended to be imported statically. Enables the usage of multiple
     * aliases acting as the same literal.
     */
    static AliasesArgumentBuilder<DirtCorePlugin, Sender> aliases(final String... aliases) {
        return AliasesArgumentBuilder.aliases(aliases);
    }

    static Predicate<String> createValidator(@NonNull final DirtCorePlugin plugin,
            final Commands.@NonNull ParseFunction pParser) {
        return (input) -> {
            try {
                pParser.parse(plugin, new StringReader(input));
                return true;
            } catch (final CommandSyntaxException commandsyntaxexception) {
                return false;
            }
        };
    }

    @FunctionalInterface
    interface ParseFunction {

        void parse(@NonNull DirtCorePlugin plugin,
                @NonNull StringReader pInput) throws CommandSyntaxException;
    }
}
