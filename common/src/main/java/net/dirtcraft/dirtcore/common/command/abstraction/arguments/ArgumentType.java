/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.arguments;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ArgumentType<P extends DirtCorePlugin, T> {

    @NonNull T parse(final P plugin, final StringReader reader) throws CommandSyntaxException;

    @NonNull String getName();

    default <S extends Sender> CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    default Collection<String> getExamples() {
        return Collections.emptyList();
    }
}
