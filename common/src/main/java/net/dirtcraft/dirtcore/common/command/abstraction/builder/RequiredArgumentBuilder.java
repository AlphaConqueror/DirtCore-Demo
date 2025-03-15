/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.builder;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.ArgumentCommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class RequiredArgumentBuilder<P extends DirtCorePlugin, S extends Sender, T> extends ArgumentBuilder<P, S, RequiredArgumentBuilder<P, S, T>> {

    private final String name;
    private final ArgumentType<P, T> type;
    private SuggestionProvider<P, S> suggestionsProvider = null;

    private RequiredArgumentBuilder(final String name, final ArgumentType<P, T> type) {
        this.name = name;
        this.type = type;
    }

    public static <P extends DirtCorePlugin, S extends Sender, T> RequiredArgumentBuilder<P, S,
            T> argument(
            final String name, final ArgumentType<P, T> type) {
        return new RequiredArgumentBuilder<>(name, type);
    }

    public RequiredArgumentBuilder<P, S, T> suggests(final SuggestionProvider<P, S> provider) {
        this.suggestionsProvider = provider;
        return this.getThis();
    }

    public SuggestionProvider<P, S> getSuggestionsProvider() {
        return this.suggestionsProvider;
    }

    public ArgumentType<P, T> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void onBuild(final CommandNode<P, S> root) {
        final ArgumentCommandNode<P, S, T> result =
                new ArgumentCommandNode<>(this.getName(), this.getType(), this.getCommand(),
                        this.getRequirement(), this.getRequiredPermission(), this.getConsoleUsage(),
                        this.getRedirect(), this.getRedirectModifier(), this.isFork(),
                        this.getSuggestionsProvider());

        this.getArguments().forEach(result::addChild);
        // add root options first
        root.getOptions().forEach(result::addOption);
        // add own options second, they might get overwritten
        this.getOptions().forEach(result::addOption);
        root.addChild(result);
    }

    @Override
    protected RequiredArgumentBuilder<P, S, T> getThis() {
        return this;
    }
}
