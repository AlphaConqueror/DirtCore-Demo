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

public interface ImmutableStringReader {

    String getString();

    int getRemainingLength();

    int getTotalLength();

    int getCursor();

    String getRead();

    String getRemaining();

    boolean canRead(int length);

    boolean canRead();

    char peek();

    char peek(int offset);
}
