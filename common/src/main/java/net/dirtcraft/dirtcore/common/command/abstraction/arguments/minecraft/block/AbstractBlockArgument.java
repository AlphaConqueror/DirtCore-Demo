/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractBlockArgument<P extends DirtCorePlugin> implements ArgumentType<P,
        BlockResult> {

    @Override
    public @NonNull String getName() {
        return "block";
    }
}
