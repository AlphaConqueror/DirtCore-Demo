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
