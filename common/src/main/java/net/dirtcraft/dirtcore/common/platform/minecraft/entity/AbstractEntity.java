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

package net.dirtcraft.dirtcore.common.platform.minecraft.entity;

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
 * Simple implementation of {@link Entity} using a {@link EntityFactory}.
 *
 * @param <E> the entity type
 */
public class AbstractEntity<E> implements Entity {

    private final DirtCorePlugin plugin;
    private final EntityFactory<?, E> factory;
    private final E entity;

    public AbstractEntity(final DirtCorePlugin plugin, final EntityFactory<?, E> factory,
            final E entity) {
        this.plugin = plugin;
        this.factory = factory;
        this.entity = entity;
    }

    @Override
    public void discard() {
        this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.discard(this.entity));
    }

    @Override
    public @NonNull DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NonNull String getType() {
        return this.factory.getType(this.entity);
    }

    @Override
    public @NonNull World getWorld() {
        return this.factory.getWorld(this.entity);
    }

    @Override
    public @NonNull Vec3 getPosition() {
        return this.factory.getPosition(this.entity);
    }

    @Override
    public @NonNull Vec2 getRotation() {
        return this.factory.getRotation(this.entity);
    }

    @Override
    public @NonNull BlockPos getBlockPos() {
        return this.factory.getBlockPos(this.entity);
    }

    @Override
    public @NonNull AABB getBoundingBox() {
        return this.factory.getBoundingBox(this.entity);
    }

    public E getEntity() {
        return this.entity;
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.factory.getUniqueId(this.entity);
    }

    @Override
    public @NonNull String getName() {
        return this.factory.getName(this.entity);
    }
}
