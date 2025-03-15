/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.model.minecraft;

import net.dirtcraft.dirtcore.common.model.Identifiable;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent an entity within the minecraft entity implementations.
 */
public interface Entity extends Identifiable {

    /**
     * Discards (removes) this entity.
     */
    void discard();

    /**
     * Gets the plugin instance the entity is from.
     *
     * @return the plugin
     */
    @NonNull DirtCorePlugin getPlugin();

    /**
     * Gets the entity type.
     *
     * @return the type
     */
    @NonNull String getType();

    /**
     * Gets the world this entity is in.
     *
     * @return the world
     */
    @NonNull World getWorld();

    /**
     * Gets the position.
     *
     * @return the position
     */
    @NonNull Vec3 getPosition();

    /**
     * Gets the rotation.
     *
     * @return the rotation
     */
    @NonNull Vec2 getRotation();

    /**
     * Gets the block position.
     *
     * @return the block position
     */
    @NonNull BlockPos getBlockPos();

    /**
     * Gets the bounding box.
     *
     * @return the bounding box
     */
    @NonNull AABB getBoundingBox();

    /**
     * Gets the distance to another {@link Vec3}.
     *
     * @param vec3 the other vector
     * @return the distance
     */
    default double distanceToSqr(@NonNull final Vec3 vec3) {
        final Vec3 pos = this.getPosition();
        final double d0 = pos.x - vec3.x;
        final double d1 = pos.y - vec3.y;
        final double d2 = pos.z - vec3.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @NonNull
    default Vec2i getChunkPos() {
        final BlockPos blockPos = this.getBlockPos();
        return Vec2i.from(World.blockToChunkCoordinate(blockPos.getX()),
                World.blockToChunkCoordinate(blockPos.getZ()));
    }

    default int getChunkX() {
        return World.blockToChunkCoordinate(this.getBlockPos().getX());
    }

    default int getChunkZ() {
        return World.blockToChunkCoordinate(this.getBlockPos().getZ());
    }
}
