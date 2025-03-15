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

package net.dirtcraft.dirtcore.common.command.abstraction.builder;

import java.util.Collection;
import java.util.Collections;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.RedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.SingleRedirectModifier;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.RootCommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public abstract class ArgumentBuilder<P extends DirtCorePlugin, S extends Sender,
        T extends ArgumentBuilder<P, S, T>> extends AbstractArgumentBuilderLike<S, T> {

    private final RootCommandNode<P, S> rootCommandNode = new RootCommandNode<>();
    private Command<P, S> command;
    private ConsoleUsage consoleUsage = ConsoleUsage.INHERIT;
    private CommandNode<P, S> target;
    private RedirectModifier<P, S> modifier = null;
    private boolean forks;

    public abstract void onBuild(CommandNode<P, S> root);

    public T then(final ArgumentBuilder<P, S, ?> argument) {
        if (this.target != null) {
            throw new IllegalStateException("Cannot add children to a redirected node");
        }

        argument.onBuild(this.rootCommandNode);
        return this.getThis();
    }

    public Collection<CommandNode<P, S>> getArguments() {
        return this.rootCommandNode.getChildren();
    }

    public T executes(final Command<P, S> command) {
        this.command = command;
        return this.getThis();
    }

    public Command<P, S> getCommand() {
        return this.command;
    }

    public T consoleUsage(final ConsoleUsage consoleUsage) {
        this.consoleUsage = consoleUsage;
        return this.getThis();
    }

    public ConsoleUsage getConsoleUsage() {
        return this.consoleUsage;
    }

    public T withOption(final OptionArgumentBuilder<P, S, ?> option) {
        this.rootCommandNode.addOption(option.build());
        return this.getThis();
    }

    public Collection<OptionCommandNode<P, S>> getOptions() {
        return this.rootCommandNode.getOptions();
    }

    public T redirect(final CommandNode<P, S> target) {
        return this.forward(target, null, false);
    }

    public T redirect(final CommandNode<P, S> target, final SingleRedirectModifier<P, S> modifier) {
        return this.forward(target,
                modifier == null ? null : o -> Collections.singleton(modifier.apply(o)), false);
    }

    public T fork(final CommandNode<P, S> target, final RedirectModifier<P, S> modifier) {
        return this.forward(target, modifier, true);
    }

    public T forward(final CommandNode<P, S> target, final RedirectModifier<P, S> modifier,
            final boolean fork) {
        if (!this.rootCommandNode.getChildren().isEmpty()) {
            throw new IllegalStateException("Cannot forward a node with children");
        }
        this.target = target;
        this.modifier = modifier;
        this.forks = fork;
        return this.getThis();
    }

    public CommandNode<P, S> getRedirect() {
        return this.target;
    }

    public RedirectModifier<P, S> getRedirectModifier() {
        return this.modifier;
    }

    public boolean isFork() {
        return this.forks;
    }
}
