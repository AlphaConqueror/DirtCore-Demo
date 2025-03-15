/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;

public abstract class AbstractItemParser {

    protected static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM =
            new DynamicCommandExceptionType(o -> new LiteralMessage("Unknown item '%s'", o));
    protected static final SimpleCommandExceptionType ERROR_UNKNOWN_ITEM_NO_CONTEXT =
            new SimpleCommandExceptionType(new LiteralMessage("Unknown item"));

    protected AbstractItemParser() {}
}