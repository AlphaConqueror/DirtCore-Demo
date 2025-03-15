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

public class SimpleCommandExceptionType implements CommandExceptionType {

    private final Message message;

    public SimpleCommandExceptionType(final Message message) {
        this.message = message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, this.message);
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, this.message, reader.getString(),
                reader.getCursor());
    }

    @Override
    public String toString() {
        return this.message.getString();
    }
}
