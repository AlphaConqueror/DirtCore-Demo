/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction;

import java.util.Collection;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

@FunctionalInterface
public interface AmbiguityConsumer<P extends DirtCorePlugin, S extends Sender> {

    void ambiguous(final CommandNode<P, S> parent, final CommandNode<P, S> child,
            final CommandNode<P, S> sibling, final Collection<String> inputs);
}
