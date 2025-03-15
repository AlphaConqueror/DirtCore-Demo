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

import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.LiteralCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class LiteralArgumentBuilder<P extends DirtCorePlugin, S extends Sender> extends ArgumentBuilder<P, S, LiteralArgumentBuilder<P, S>> {

    private final String literal;

    protected LiteralArgumentBuilder(final String literal) {
        this.literal = literal;
    }

    public static <P extends DirtCorePlugin, S extends Sender> LiteralArgumentBuilder<P, S> literal(
            final String name) {
        return new LiteralArgumentBuilder<>(name);
    }

    public String getLiteral() {
        return this.literal;
    }

    @Override
    public void onBuild(final CommandNode<P, S> root) {
        final LiteralCommandNode<P, S> result =
                new LiteralCommandNode<>(this.getLiteral(), this.getCommand(),
                        this.getRequirement(), this.getRequiredPermission(), this.getConsoleUsage(),
                        this.getRedirect(), this.getRedirectModifier(), this.isFork());

        this.getArguments().forEach(result::addChild);
        // add root options first
        root.getOptions().forEach(result::addOption);
        // add own options second, they might get overwritten
        this.getOptions().forEach(result::addOption);
        root.addChild(result);
    }

    @Override
    protected LiteralArgumentBuilder<P, S> getThis() {
        return this;
    }
}
