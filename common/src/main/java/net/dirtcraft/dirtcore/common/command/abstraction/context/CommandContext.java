/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class CommandContext<P extends DirtCorePlugin, S extends Sender> {

    private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = new HashMap<>();

    static {
        PRIMITIVE_TO_WRAPPER.put(boolean.class, Boolean.class);
        PRIMITIVE_TO_WRAPPER.put(byte.class, Byte.class);
        PRIMITIVE_TO_WRAPPER.put(short.class, Short.class);
        PRIMITIVE_TO_WRAPPER.put(char.class, Character.class);
        PRIMITIVE_TO_WRAPPER.put(int.class, Integer.class);
        PRIMITIVE_TO_WRAPPER.put(long.class, Long.class);
        PRIMITIVE_TO_WRAPPER.put(float.class, Float.class);
        PRIMITIVE_TO_WRAPPER.put(double.class, Double.class);
    }

    private final P plugin;
    private final S source;
    private final String input;
    private final Command<P, S> command;
    private final Map<String, ParsedArgument<?>> arguments;
    private final Set<String> options;
    private final CommandNode<P, S> rootNode;
    private final List<ParsedCommandNode<P, S>> nodes;
    private final StringRange range;
    private final CommandContext<P, S> child;
    private final RedirectModifier<P, S> modifier;
    private final boolean forks;
    private final ConsoleUsage consoleUsage;

    public CommandContext(final P plugin, final S source, final String input,
            final Map<String, ParsedArgument<?>> arguments, final Set<String> options,
            final Command<P, S> command, final CommandNode<P, S> rootNode,
            final List<ParsedCommandNode<P, S>> nodes, final StringRange range,
            final CommandContext<P, S> child, final RedirectModifier<P, S> modifier,
            final boolean forks, final ConsoleUsage consoleUsage) {
        this.plugin = plugin;
        this.source = source;
        this.input = input;
        this.arguments = arguments;
        this.options = options;
        this.command = command;
        this.rootNode = rootNode;
        this.nodes = nodes;
        this.range = range;
        this.child = child;
        this.modifier = modifier;
        this.forks = forks;
        this.consoleUsage = consoleUsage;
    }

    public P getPlugin() {
        return this.plugin;
    }

    public CommandContext<P, S> copyFor(final S source) {
        if (this.source == source) {
            return this;
        }

        return new CommandContext<>(this.plugin, source, this.input, this.arguments, this.options,
                this.command, this.rootNode, this.nodes, this.range, this.child, this.modifier,
                this.forks, this.consoleUsage);
    }

    public CommandContext<P, S> getChild() {
        return this.child;
    }

    public CommandContext<P, S> getLastChild() {
        CommandContext<P, S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<P, S> getCommand() {
        return this.command;
    }

    public S getSource() {
        return this.source;
    }

    @SuppressWarnings("unchecked")
    public <V> V getArgument(final String name, final Class<V> clazz) {
        final ParsedArgument<?> argument = this.arguments.get(name);

        if (argument == null) {
            throw new IllegalArgumentException(
                    "No such argument '" + name + "' exists on this command");
        }

        final Object result = argument.getResult();

        if (PRIMITIVE_TO_WRAPPER.getOrDefault(clazz, clazz).isAssignableFrom(result.getClass())) {
            return (V) result;
        }

        throw new IllegalArgumentException(
                "Argument '" + name + "' is defined as " + result.getClass().getSimpleName()
                        + ", not " + clazz);
    }

    public Set<String> getOptions() {
        return this.options;
    }

    public boolean hasOption(final String name) {
        return this.options.contains(name);
    }

    @Override
    public int hashCode() {
        int result = this.source.hashCode();
        result = 31 * result + this.arguments.hashCode();
        result = 31 * result + this.options.hashCode();
        result = 31 * result + (this.command != null ? this.command.hashCode() : 0);
        result = 31 * result + this.rootNode.hashCode();
        result = 31 * result + this.nodes.hashCode();
        result = 31 * result + (this.child != null ? this.child.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CommandContext)) {
            return false;
        }

        final CommandContext<?, ?> that = (CommandContext<?, ?>) o;

        if (!this.arguments.equals(that.arguments)) {
            return false;
        }

        if (!this.options.equals(that.options)) {
            return false;
        }

        if (!this.rootNode.equals(that.rootNode)) {
            return false;
        }

        if (this.nodes.size() != that.nodes.size() || !this.nodes.equals(that.nodes)) {
            return false;
        }

        if (!Objects.equals(this.command, that.command)) {
            return false;
        }

        if (!this.source.equals(that.source)) {
            return false;
        }

        return Objects.equals(this.child, that.child);
    }

    public RedirectModifier<P, S> getRedirectModifier() {
        return this.modifier;
    }

    public StringRange getRange() {
        return this.range;
    }

    public String getInput() {
        return this.input;
    }

    public CommandNode<P, S> getRootNode() {
        return this.rootNode;
    }

    public List<ParsedCommandNode<P, S>> getNodes() {
        return this.nodes;
    }

    public boolean hasNodes() {
        return !this.nodes.isEmpty();
    }

    public boolean isForked() {
        return this.forks;
    }

    public boolean canConsoleUse() {
        return this.consoleUsage == ConsoleUsage.ALLOWED;
    }
}
