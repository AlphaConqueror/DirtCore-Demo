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

import java.util.Objects;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class ParsedCommandNode<P extends DirtCorePlugin, S extends Sender> {

    private final CommandNode<P, S> node;

    private final StringRange range;

    public ParsedCommandNode(final CommandNode<P, S> node, final StringRange range) {
        this.node = node;
        this.range = range;
    }

    public CommandNode<P, S> getNode() {
        return this.node;
    }

    public StringRange getRange() {
        return this.range;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.node, this.range);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final ParsedCommandNode<?, ?> that = (ParsedCommandNode<?, ?>) o;
        return Objects.equals(this.node, that.node) && Objects.equals(this.range, that.range);
    }

    @Override
    public String toString() {
        return this.node + "@" + this.range;
    }
}
