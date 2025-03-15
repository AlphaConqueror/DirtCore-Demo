/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BoolArgumentType implements ArgumentType<DirtCorePlugin, Boolean> {

    private static final Collection<String> EXAMPLES = Arrays.asList("true", "false");

    private BoolArgumentType() {}

    public static BoolArgumentType bool() {
        return new BoolArgumentType();
    }

    public static boolean getBool(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, Boolean.class);
    }

    @Override
    public @NonNull Boolean parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return reader.readBoolean();
    }

    @Override
    public @NonNull String getName() {
        return "bool";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCorePlugin plugin, final CommandContext<DirtCorePlugin, S> context,
            final SuggestionsBuilder builder) {
        if ("true".startsWith(builder.getRemainingLowerCase())) {
            builder.suggest("true");
        }
        if ("false".startsWith(builder.getRemainingLowerCase())) {
            builder.suggest("false");
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
