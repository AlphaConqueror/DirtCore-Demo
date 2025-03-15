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

import net.dirtcraft.dirtcore.common.command.abstraction.ImmutableStringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;

public class DynamicNCommandExceptionType implements CommandExceptionType {

    private final Function function;

    public DynamicNCommandExceptionType(final Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object a, final Object... args) {
        return new CommandSyntaxException(this, this.function.apply(args));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader,
            final Object... args) {
        return new CommandSyntaxException(this, this.function.apply(args), reader.getString(),
                reader.getCursor());
    }

    public interface Function {

        Message apply(Object[] args);
    }
}
