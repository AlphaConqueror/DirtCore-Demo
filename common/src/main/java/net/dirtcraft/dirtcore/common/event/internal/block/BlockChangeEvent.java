/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.event.internal.block;

import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.util.Param;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface BlockChangeEvent extends DirtCoreEvent {

    @Param(0)
    @NonNull Block getOldBlock();

    @Param(1)
    @NonNull Block getNewBlock();

    @Param(2)
    int getFlags();

    @Param(3)
    @NonNull World getWorld();

    @Param(4)
    int getX();

    @Param(5)
    int getY();

    @Param(6)
    int getZ();
}
