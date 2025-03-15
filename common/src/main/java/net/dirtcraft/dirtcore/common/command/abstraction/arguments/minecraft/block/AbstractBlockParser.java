/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;

public abstract class AbstractBlockParser {

    protected static final SimpleCommandExceptionType ERROR_UNKNOWN_BLOCK_NO_CONTEXT =
            new SimpleCommandExceptionType(new LiteralMessage("Unknown block type"));

    protected AbstractBlockParser() {}
}
