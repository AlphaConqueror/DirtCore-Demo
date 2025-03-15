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
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.RequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.ParsedArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;

public class ArgumentCommandNode<P extends DirtCorePlugin, S extends Sender, T> extends CommandNode<P, S> {

    private static final String USAGE_ARGUMENT_CLOSE = ">";
    private static final String USAGE_ARGUMENT_OPEN = "<";
    private final String name;
    private final ArgumentType<P, T> type;
    private final SuggestionProvider<P, S> customSuggestions;

    public ArgumentCommandNode(final String name, final ArgumentType<P, T> type,
            final Command<P, S> command, final Predicate<S> requirement,
            final Permission requiredPermission, final ConsoleUsage consoleUsage,
            final CommandNode<P, S> redirect, final RedirectModifier<P, S> modifier,
            final boolean forks, final SuggestionProvider<P, S> customSuggestions) {
        super(command, requirement, requiredPermission, consoleUsage, redirect, modifier, forks);
        this.name = name;
        this.type = type;
        this.customSuggestions = customSuggestions;
    }

    public ArgumentType<?, T> getType() {
        return this.type;
    }

    public SuggestionProvider<P, S> getCustomSuggestions() {
        return this.customSuggestions;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getUsageText() {
        return USAGE_ARGUMENT_OPEN + this.name + USAGE_ARGUMENT_CLOSE;
    }

    @Override
    public void parse(final P plugin, final StringReader reader,
            final CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final T result = this.type.parse(plugin, reader);
        final ParsedArgument<T> parsed = new ParsedArgument<>(start, reader.getCursor(), result);

        contextBuilder.withArgument(this.name, parsed);
        contextBuilder.withNode(this, parsed.getRange());
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        if (this.customSuggestions == null) {
            return this.type.listSuggestions(plugin, context, builder);
        }

        return this.customSuggestions.getSuggestions(context, builder);
    }

    @Override
    public RequiredArgumentBuilder<P, S, T> createBuilder() {
        final RequiredArgumentBuilder<P, S, T> builder =
                RequiredArgumentBuilder.argument(this.name, this.type);
        builder.requires(this.getRequirement());
        builder.requiresPermission(this.getRequiredPermission());
        builder.consoleUsage(this.consoleUsage());
        builder.forward(this.getRedirect(), this.getRedirectModifier(), this.isFork());
        builder.suggests(this.customSuggestions);

        if (this.getCommand() != null) {
            builder.executes(this.getCommand());
        }

        return builder;
    }

    @Override
    public Collection<String> getExamples() {
        return this.type.getExamples();
    }

    @Override
    public boolean isValidInput(final P plugin, final String input) {
        try {
            final StringReader reader = new StringReader(input);
            this.type.parse(plugin, reader);
            return !reader.canRead() || reader.peek() == ' ';
        } catch (final CommandSyntaxException ignored) {
            return false;
        }
    }

    @Override
    protected String getSortedKey() {
        return this.name;
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ArgumentCommandNode)) {
            return false;
        }

        final ArgumentCommandNode<?, ?, ?> that = (ArgumentCommandNode<?, ?, ?>) o;

        if (!this.name.equals(that.name)) {
            return false;
        }

        if (!this.type.equals(that.type)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public String toString() {
        return "<argument " + this.name + ":" + this.type + ">";
    }
}
