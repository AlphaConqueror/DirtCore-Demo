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

package net.dirtcraft.dirtcore.common.command.abstraction.context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class CommandContextBuilder<P extends DirtCorePlugin, S extends Sender> {

    private final P plugin;
    private final Map<String, ParsedArgument<?>> arguments = new LinkedHashMap<>();
    private final Set<String> options = new HashSet<>();
    private final CommandNode<P, S> rootNode;
    private final List<ParsedCommandNode<P, S>> nodes = new ArrayList<>();
    private final CommandDispatcher<P, S> dispatcher;
    private S source;
    private Command<P, S> command;
    private CommandContextBuilder<P, S> child;
    private StringRange range;
    private RedirectModifier<P, S> modifier = null;
    private boolean forks;
    private ConsoleUsage consoleUsage;

    public CommandContextBuilder(final P plugin, final CommandDispatcher<P, S> dispatcher,
            final S source, final CommandNode<P, S> rootNode, final int start) {
        this.plugin = plugin;
        this.rootNode = rootNode;
        this.dispatcher = dispatcher;
        this.source = source;
        this.range = StringRange.at(start);
        this.consoleUsage = rootNode.consoleUsage();
    }

    public P getPlugin() {
        return this.plugin;
    }

    public CommandContextBuilder<P, S> withSource(final S source) {
        this.source = source;
        return this;
    }

    public S getSource() {
        return this.source;
    }

    public CommandNode<P, S> getRootNode() {
        return this.rootNode;
    }

    public CommandContextBuilder<P, S> withArgument(final String name,
            final ParsedArgument<?> argument) {
        this.arguments.put(name, argument);
        return this;
    }

    public Map<String, ParsedArgument<?>> getArguments() {
        return this.arguments;
    }

    public CommandContextBuilder<P, S> withOption(final String name,
            final boolean overridesConsoleUsage, final ParsedArgument<?> argument) {
        this.options.add(name);
        this.arguments.put(name, argument);

        if (overridesConsoleUsage) {
            this.consoleUsage = ConsoleUsage.ALLOWED;
        }

        return this;
    }

    public Set<String> getOptions() {
        return this.options;
    }

    public CommandContextBuilder<P, S> withCommand(final Command<P, S> command) {
        this.command = command;
        return this;
    }

    public CommandContextBuilder<P, S> withNode(final CommandNode<P, S> node,
            final StringRange range) {
        this.nodes.add(new ParsedCommandNode<>(node, range));
        this.range = StringRange.encompassing(this.range, range);
        this.modifier = node.getRedirectModifier();
        this.forks = node.isFork();

        final ConsoleUsage nodeConsoleUsage = node.consoleUsage();

        if (nodeConsoleUsage != ConsoleUsage.INHERIT) {
            this.consoleUsage = nodeConsoleUsage;
        }

        return this;
    }

    public CommandContextBuilder<P, S> copy() {
        final CommandContextBuilder<P, S> copy =
                new CommandContextBuilder<>(this.plugin, this.dispatcher, this.source,
                        this.rootNode, this.range.getStart());
        copy.command = this.command;
        copy.arguments.putAll(this.arguments);
        copy.options.addAll(this.options);
        copy.nodes.addAll(this.nodes);
        copy.child = this.child;
        copy.range = this.range;
        copy.forks = this.forks;
        copy.consoleUsage = this.consoleUsage;
        return copy;
    }

    public CommandContextBuilder<P, S> withChild(final CommandContextBuilder<P, S> child) {
        this.child = child;
        return this;
    }

    public ConsoleUsage getConsoleUsage() {
        return this.consoleUsage;
    }

    public CommandContextBuilder<P, S> getChild() {
        return this.child;
    }

    public CommandContextBuilder<P, S> getLastChild() {
        CommandContextBuilder<P, S> result = this;
        while (result.getChild() != null) {
            result = result.getChild();
        }
        return result;
    }

    public Command<P, S> getCommand() {
        return this.command;
    }

    public List<ParsedCommandNode<P, S>> getNodes() {
        return this.nodes;
    }

    public CommandContext<P, S> build(final String input) {
        return new CommandContext<>(this.plugin, this.source, input, this.arguments, this.options,
                this.command, this.rootNode, this.nodes, this.range,
                this.child == null ? null : this.child.build(input), this.modifier, this.forks,
                this.consoleUsage);
    }

    public CommandDispatcher<P, S> getDispatcher() {
        return this.dispatcher;
    }

    public StringRange getRange() {
        return this.range;
    }

    public SuggestionContext<P, S> findSuggestionContext(final int cursor) {
        if (this.range.getStart() <= cursor) {
            if (this.range.getEnd() < cursor) {
                if (this.child != null) {
                    return this.child.findSuggestionContext(cursor);
                } else if (!this.nodes.isEmpty()) {
                    final ParsedCommandNode<P, S> last = this.nodes.get(this.nodes.size() - 1);
                    return new SuggestionContext<>(last.getNode(), last.getRange().getEnd() + 1);
                } else {
                    return new SuggestionContext<>(this.rootNode, this.range.getStart());
                }
            } else {
                CommandNode<P, S> prev = this.rootNode;
                for (final ParsedCommandNode<P, S> node : this.nodes) {
                    final StringRange nodeRange = node.getRange();
                    if (nodeRange.getStart() <= cursor && cursor <= nodeRange.getEnd()) {
                        return new SuggestionContext<>(prev, nodeRange.getStart());
                    }
                    prev = node.getNode();
                }
                if (prev == null) {
                    throw new IllegalStateException("Can't find node before cursor");
                }
                return new SuggestionContext<>(prev, this.range.getStart());
            }
        }
        throw new IllegalStateException("Can't find node before cursor");
    }
}
