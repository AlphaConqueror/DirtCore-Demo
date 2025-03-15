/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.exceptions;

import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.ImmutableStringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;

public class DynamicCommandExceptionType implements CommandExceptionType {

    private final Function<Object, Message> function;

    public DynamicCommandExceptionType(final Function<Object, Message> function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object arg) {
        return new CommandSyntaxException(this, this.function.apply(arg));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader,
            final Object arg) {
        return new CommandSyntaxException(this, this.function.apply(arg), reader.getString(),
                reader.getCursor());
    }
}
