/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.AbstractEntity;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
        entity.discard();
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
        return EntityType.getKey(entity.getType()).toString();
    }

    @Override
    public @NonNull World getWorld(@NonNull final Entity entity) {
        return this.platformFactory.wrapWorld(entity.level());
    }

    @Override
    public @NonNull Vec3 getPosition(@NonNull final Entity entity) {
        final net.minecraft.world.phys.Vec3 position = entity.position();
        return Vec3.from(position.x, position.y, position.z);
    }

    @Override
    public @NonNull Vec2 getRotation(@NonNull final Entity entity) {
        final net.minecraft.world.phys.Vec2 rotation = entity.getRotationVector();
        return Vec2.from(rotation.x, rotation.y);
    }

    @Override
    public @NonNull BlockPos getBlockPos(@NonNull final Entity entity) {
        final net.minecraft.core.BlockPos blockPos = entity.blockPosition();
        return BlockPos.of(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    @Override
    public @NonNull AABB getBoundingBox(@NonNull final Entity entity) {
        final net.minecraft.world.phys.AABB aabb = entity.getBoundingBox();
        return new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    @Override
    protected @NonNull UUID getUniqueId(@NonNull final Entity entity) {
        return entity.getUUID();
    }

    @Override
    protected @NonNull String getName(@NonNull final Entity entity) {
        return entity.getScoreboardName();
    }
}
