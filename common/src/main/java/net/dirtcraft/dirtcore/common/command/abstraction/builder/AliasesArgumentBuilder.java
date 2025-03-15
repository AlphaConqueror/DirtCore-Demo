/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.builder;

import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.LiteralCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class AliasesArgumentBuilder<P extends DirtCorePlugin, S extends Sender> extends ArgumentBuilder<P, S, AliasesArgumentBuilder<P, S>> {

    private final String[] aliases;

    protected AliasesArgumentBuilder(final String[] aliases) {
        this.aliases = aliases;
    }

    public static <P extends DirtCorePlugin, S extends Sender> AliasesArgumentBuilder<P, S> aliases(
            final String... aliases) {
        return new AliasesArgumentBuilder<>(aliases);
    }

    @Override
    public void onBuild(final CommandNode<P, S> root) {
        for (final String alias : this.aliases) {
            final LiteralCommandNode<P, S> result =
                    new LiteralCommandNode<>(alias, this.getCommand(), this.getRequirement(),
                            this.getRequiredPermission(), this.getConsoleUsage(),
                            this.getRedirect(), this.getRedirectModifier(), this.isFork());

            this.getArguments().forEach(result::addChild);
            // add root options first
            root.getOptions().forEach(result::addOption);
            // add own options second, they might get overwritten
            this.getOptions().forEach(result::addOption);
            root.addChild(result);
        }
    }

    @Override
    protected AliasesArgumentBuilder<P, S> getThis() {
        return this;
    }
}
