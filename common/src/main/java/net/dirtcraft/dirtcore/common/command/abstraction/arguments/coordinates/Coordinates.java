/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface Coordinates {

    @NonNull Vec3 getPosition(@NonNull Sender sender);

    @NonNull Vec2 getRotation(@NonNull Sender sender);

    @NonNull Vec2i getChunkPos(@NonNull final Sender sender);

    boolean isXRelative();

    boolean isYRelative();

    boolean isZRelative();

    default BlockPos getBlockPos(@NonNull final Sender sender) {
        return BlockPos.containing(this.getPosition(sender));
    }
}
