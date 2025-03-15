/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.entity;

import java.util.Objects;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe entity instance.
 *
 * @param <P> the plugin type
 * @param <E> the entity type
 */
public abstract class EntityFactory<P extends DirtCorePlugin, E> {

    private final P plugin;

    public EntityFactory(final P plugin) {
        this.plugin = plugin;
    }

    public abstract void discard(@NonNull E entity);

    @NonNull
    public abstract E transformEntity(
            net.dirtcraft.dirtcore.common.model.minecraft.@NonNull Entity entity);

    @NonNull
    public abstract String getType(@NonNull E entity);

    @NonNull
    public abstract World getWorld(@NonNull E entity);

    @NonNull
    public abstract Vec3 getPosition(@NonNull E entity);

    @NonNull
    public abstract Vec2 getRotation(@NonNull E entity);

    @NonNull
    public abstract BlockPos getBlockPos(@NonNull E entity);

    @NonNull
    public abstract AABB getBoundingBox(@NonNull E entity);

    @NonNull
    protected abstract UUID getUniqueId(@NonNull E entity);

    @NonNull
    protected abstract String getName(@NonNull E entity);

    @NonNull
    public Entity wrap(@NonNull final E entity) {
        Objects.requireNonNull(entity, "entity");
        return new AbstractEntity<>(this.plugin, this, entity);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
