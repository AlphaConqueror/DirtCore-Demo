/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;

public class RootCommandNode<P extends DirtCorePlugin, S extends Sender> extends CommandNode<P, S> {

    public RootCommandNode() {
        super(null, c -> true, Permission.NONE, ConsoleUsage.ALLOWED, null,
                s -> Collections.singleton(s.getSource()), false);
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getUsageText() {
        return "";
    }

    @Override
    public void parse(final P plugin, final StringReader reader,
            final CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException {}

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context, final SuggestionsBuilder builder) {
        return Suggestions.empty();
    }

    @Override
    public ArgumentBuilder<P, S, ?> createBuilder() {
        throw new IllegalStateException("Cannot convert root into a builder");
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.emptyList();
    }

    @Override
    public boolean isValidInput(final DirtCorePlugin plugin, final String input) {
        return false;
    }

    @Override
    protected String getSortedKey() {
        return "";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RootCommandNode)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public String toString() {
        return "<root>";
    }
}
