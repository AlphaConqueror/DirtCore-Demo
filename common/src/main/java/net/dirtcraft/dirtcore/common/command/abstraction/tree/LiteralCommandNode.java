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
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.LiteralArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.StringRange;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;

public class LiteralCommandNode<P extends DirtCorePlugin, S extends Sender> extends CommandNode<P
        , S> {

    private final String literal;
    private final String literalLowerCase;

    public LiteralCommandNode(final String literal, final Command<P, S> command,
            final Predicate<S> requirement, final Permission requiredPermission,
            final ConsoleUsage consoleUsage, final CommandNode<P, S> redirect,
            final RedirectModifier<P, S> modifier, final boolean forks) {
        super(command, requirement, requiredPermission, consoleUsage, redirect, modifier, forks);
        this.literal = literal;
        this.literalLowerCase = literal.toLowerCase(Locale.ROOT);
    }

    public String getLiteral() {
        return this.literal;
    }

    @Override
    public String getName() {
        return this.literal;
    }

    @Override
    public String getUsageText() {
        return this.literal;
    }

    @Override
    public void parse(final P plugin, final StringReader reader,
            final CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int end = this.parse(reader);

        if (end > -1) {
            contextBuilder.withNode(this, StringRange.between(start, end));
            return;
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()
                .createWithContext(reader, this.literal);
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context, final SuggestionsBuilder builder) {
        if (this.literalLowerCase.startsWith(builder.getRemainingLowerCase())) {
            return builder.suggest(this.literal).buildFuture();
        }

        return Suggestions.empty();
    }

    @Override
    public LiteralArgumentBuilder<P, S> createBuilder() {
        final LiteralArgumentBuilder<P, S> builder = LiteralArgumentBuilder.literal(this.literal);

        builder.requires(this.getRequirement());
        builder.requiresPermission(this.getRequiredPermission());
        builder.consoleUsage(this.consoleUsage());
        builder.forward(this.getRedirect(), this.getRedirectModifier(), this.isFork());

        if (this.getCommand() != null) {
            builder.executes(this.getCommand());
        }

        return builder;
    }

    @Override
    public Collection<String> getExamples() {
        return Collections.singleton(this.literal);
    }

    @Override
    public boolean isValidInput(final DirtCorePlugin plugin, final String input) {
        return this.parse(new StringReader(input)) > -1;
    }

    @Override
    protected String getSortedKey() {
        return this.literal;
    }

    @Override
    public int hashCode() {
        int result = this.literal.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LiteralCommandNode)) {
            return false;
        }

        final LiteralCommandNode<?, ?> that = (LiteralCommandNode<?, ?>) o;

        if (!this.literal.equals(that.literal)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public String toString() {
        return "<literal " + this.literal + ">";
    }

    private int parse(final StringReader reader) {
        final int start = reader.getCursor();
        if (reader.canRead(this.literal.length())) {
            final int end = start + this.literal.length();
            if (reader.getString().substring(start, end).equals(this.literal)) {
                reader.setCursor(end);
                if (!reader.canRead() || reader.peek() == ' ') {
                    return end;
                } else {
                    reader.setCursor(start);
                }
            }
        }
        return -1;
    }
}
