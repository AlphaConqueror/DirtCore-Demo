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

import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class SuggestionContext<P extends DirtCorePlugin, S extends Sender> {

    public final CommandNode<P, S> parent;
    public final int startPos;

    public SuggestionContext(final CommandNode<P, S> parent, final int startPos) {
        this.parent = parent;
        this.startPos = startPos;
    }
}
