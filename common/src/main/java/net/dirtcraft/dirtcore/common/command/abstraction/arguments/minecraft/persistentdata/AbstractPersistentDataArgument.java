/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public abstract class AbstractPersistentDataArgument<P extends DirtCorePlugin> implements ArgumentType<P, String> {}
