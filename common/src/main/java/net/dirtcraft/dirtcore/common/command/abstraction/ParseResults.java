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
import java.util.Collections;
import java.util.Map;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNodeLike;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class ParseResults<P extends DirtCorePlugin, S extends Sender> {

    private final CommandContextBuilder<P, S> context;
    private final Map<CommandNodeLike<S>, CommandSyntaxException> exceptions;
    private final ImmutableStringReader reader;

    public ParseResults(final CommandContextBuilder<P, S> context,
            final ImmutableStringReader reader,
            final Map<CommandNodeLike<S>, CommandSyntaxException> exceptions) {
        this.context = context;
        this.reader = reader;
        this.exceptions = exceptions;
    }

    public ParseResults(final CommandContextBuilder<P, S> context) {
        this(context, new StringReader(""), Collections.emptyMap());
    }

    public CommandContextBuilder<P, S> getContext() {
        return this.context;
    }

    public ImmutableStringReader getReader() {
        return this.reader;
    }

    public Map<CommandNodeLike<S>, CommandSyntaxException> getExceptionMap() {
        return this.exceptions;
    }

    public Collection<CommandSyntaxException> getExceptions() {
        return this.exceptions.values();
    }
}
