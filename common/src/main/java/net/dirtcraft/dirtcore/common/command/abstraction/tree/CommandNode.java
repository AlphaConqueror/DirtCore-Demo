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

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.tree;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.AmbiguityConsumer;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.jetbrains.annotations.NotNull;

public abstract class CommandNode<P extends DirtCorePlugin, S extends Sender> extends AbstractCommandNodeLike<S> implements Comparable<CommandNode<P, S>> {

    private final Map<String, CommandNode<P, S>> children = new LinkedHashMap<>();
    private final Map<String, LiteralCommandNode<P, S>> literals = new LinkedHashMap<>();
    private final Map<String, ArgumentCommandNode<P, S, ?>> arguments = new LinkedHashMap<>();
    private final Map<String, OptionCommandNode<P, S>> options = new LinkedHashMap<>();
    private final ConsoleUsage consoleUsage;
    private final CommandNode<P, S> redirect;
    private final RedirectModifier<P, S> modifier;
    private final boolean forks;
    private Command<P, S> command;

    protected CommandNode(final Command<P, S> command, final Predicate<S> requirement,
            final Permission requiredPermission, final ConsoleUsage consoleUsage,
            final CommandNode<P, S> redirect, final RedirectModifier<P, S> modifier,
            final boolean forks) {
        super(requirement, requiredPermission);
        this.command = command;
        this.consoleUsage = consoleUsage;
        this.redirect = redirect;
        this.modifier = modifier;
        this.forks = forks;
    }

    public abstract String getName();

    public abstract String getUsageText();

    public abstract void parse(P plugin, StringReader reader,
            CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException;

    public abstract CompletableFuture<Suggestions> listSuggestions(P plugin,
            CommandContext<P, S> context, SuggestionsBuilder builder) throws CommandSyntaxException;

    public abstract ArgumentBuilder<P, S, ?> createBuilder();

    public abstract Collection<String> getExamples();

    protected abstract boolean isValidInput(final P plugin, final String input);

    protected abstract String getSortedKey();

    public Command<P, S> getCommand() {
        return this.command;
    }

    public Collection<CommandNode<P, S>> getChildren() {
        return this.children.values();
    }

    public CommandNode<P, S> getChild(final String name) {
        return this.children.get(name);
    }

    public CommandNode<P, S> getRedirect() {
        return this.redirect;
    }

    public RedirectModifier<P, S> getRedirectModifier() {
        return this.modifier;
    }

    public void addChild(final CommandNode<P, S> node) {
        if (node instanceof RootCommandNode) {
            throw new UnsupportedOperationException(
                    "Cannot add a RootCommandNode as a child to any other CommandNode");
        }

        final CommandNode<P, S> child = this.children.get(node.getName());

        if (child != null) {
            // We've found something to merge onto
            if (node.getCommand() != null) {
                child.command = node.getCommand();
            }
            for (final CommandNode<P, S> grandchild : node.getChildren()) {
                child.addChild(grandchild);
            }
        } else {
            this.children.put(node.getName(), node);
            if (node instanceof LiteralCommandNode) {
                this.literals.put(node.getName(), (LiteralCommandNode<P, S>) node);
            } else if (node instanceof ArgumentCommandNode) {
                this.arguments.put(node.getName(), (ArgumentCommandNode<P, S, ?>) node);
            }
        }
    }

    public void addOption(final OptionCommandNode<P, S> option) {
        this.options.put(option.getName(), option);
    }

    public Collection<OptionCommandNode<P, S>> getOptions() {
        return this.options.values();
    }

    public void findAmbiguities(final P plugin, final AmbiguityConsumer<P, S> consumer) {
        Set<String> matches = new HashSet<>();

        for (final CommandNode<P, S> child : this.children.values()) {
            for (final CommandNode<P, S> sibling : this.children.values()) {
                if (child == sibling) {
                    continue;
                }

                for (final String input : child.getExamples()) {
                    if (sibling.isValidInput(plugin, input)) {
                        matches.add(input);
                    }
                }

                if (!matches.isEmpty()) {
                    consumer.ambiguous(this, child, sibling, matches);
                    matches = new HashSet<>();
                }
            }

            child.findAmbiguities(plugin, consumer);
        }
    }

    @Override
    public int hashCode() {
        return 31 * this.children.hashCode() + (this.command != null ? this.command.hashCode() : 0);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommandNode)) {
            return false;
        }

        final CommandNode<?, ?> that = (CommandNode<?, ?>) o;

        if (!this.children.equals(that.children)) {
            return false;
        }

        return Objects.equals(this.command, that.command);
    }

    public Predicate<S> getRequirement() {
        return this.requirement;
    }

    public Permission getRequiredPermission() {
        return this.requiredPermission;
    }

    public ConsoleUsage consoleUsage() {
        return this.consoleUsage;
    }

    public Collection<? extends CommandNode<P, S>> getRelevantNodes(final StringReader input) {
        if (!this.literals.isEmpty()) {
            final int cursor = input.getCursor();

            while (input.canRead() && input.peek() != ' ') {
                input.skip();
            }

            final String text = input.getString().substring(cursor, input.getCursor());

            input.setCursor(cursor);

            final LiteralCommandNode<P, S> literal = this.literals.get(text);

            if (literal != null) {
                return Collections.singleton(literal);
            }
        }

        return this.arguments.values();
    }

    @Override
    public int compareTo(final @NotNull CommandNode<P, S> o) {
        if (this instanceof LiteralCommandNode == o instanceof LiteralCommandNode) {
            return this.getSortedKey().compareTo(o.getSortedKey());
        }

        return (o instanceof LiteralCommandNode) ? 1 : -1;
    }

    public boolean isFork() {
        return this.forks;
    }
}
