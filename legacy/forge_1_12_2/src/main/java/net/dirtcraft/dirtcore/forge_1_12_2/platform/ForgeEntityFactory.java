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

package net.dirtcraft.dirtcore.forge_1_12_2.platform;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.AbstractEntity;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEntityFactory extends EntityFactory<DirtCoreForgePlugin, Entity> {

    private final ForgePlatformFactory platformFactory;

    public ForgeEntityFactory(final DirtCoreForgePlugin plugin,
            final ForgePlatformFactory platformFactory) {
        super(plugin);
        this.platformFactory = platformFactory;
    }

    @Override
    public void discard(@NonNull final Entity entity) {
        entity.world.removeEntity(entity);
    }

    @Override
    public @NonNull Entity transformEntity(
            final net.dirtcraft.dirtcore.common.model.minecraft.@NonNull Entity entity) {
        if (entity instanceof AbstractEntity) {
            //noinspection unchecked
            return ((AbstractEntity<Entity>) entity).getEntity();
        }

        throw new AssertionError();
    }

    @Override
    public @NonNull String getType(@NonNull final Entity entity) {
        final ResourceLocation resourceLocation = EntityList.getKey(entity);
        return resourceLocation == null ? entity.getClass().getSimpleName()
                : resourceLocation.toString();
    }

    @Override
    public @NonNull World getWorld(@NonNull final Entity entity) {
        return this.platformFactory.wrapWorld(entity.world);
    }

    @Override
    public @NonNull Vec3 getPosition(@NonNull final Entity entity) {
        return Vec3.from(entity.posX, entity.posY, entity.posZ);
    }

    @Override
    public @NonNull Vec2 getRotation(@NonNull final Entity entity) {
        return Vec2.from(entity.rotationYaw, entity.rotationPitch);
    }

    @Override
    public @NonNull BlockPos getBlockPos(@NonNull final Entity entity) {
        return BlockPos.of((int) entity.posX, (int) entity.posY, (int) entity.posZ);
    }

    @Override
    public @NonNull AABB getBoundingBox(@NonNull final Entity entity) {
        final AxisAlignedBB aabb = entity.getEntityBoundingBox();
        return new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    @Override
    protected @NonNull UUID getUniqueId(@NonNull final Entity entity) {
        return entity.getUniqueID();
    }

    @Override
    protected @NonNull String getName(@NonNull final Entity entity) {
        return entity.getName();
    }
}
