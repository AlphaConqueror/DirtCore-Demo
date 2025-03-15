/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.AbstractEntity;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.AxisAlignedBB;
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
        entity.worldObj.removeEntity(entity);
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
        final String s = EntityList.getEntityString(entity);
        return s == null ? entity.getClass().getSimpleName() : s;
    }

    @Override
    public @NonNull World getWorld(@NonNull final Entity entity) {
        return this.platformFactory.wrapWorld(entity.worldObj);
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
        final AxisAlignedBB aabb = entity.getBoundingBox();
        return new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    @Override
    protected @NonNull UUID getUniqueId(@NonNull final Entity entity) {
        return entity.getUniqueID();
    }

    @Override
    protected @NonNull String getName(@NonNull final Entity entity) {
        return entity.getCommandSenderName();
    }
}
