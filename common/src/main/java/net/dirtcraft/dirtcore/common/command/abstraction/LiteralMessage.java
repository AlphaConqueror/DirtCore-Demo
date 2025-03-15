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

public class LiteralMessage implements Message {

    private final String string;

    public LiteralMessage(final String string, final Object... args) {
        this.string = String.format(string, args);
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Override
    public String toString() {
        return this.string;
    }
}
