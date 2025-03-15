/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.world;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.model.Animation;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.profile.ChunkEntityProfile;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of {@link World} using a {@link WorldFactory}.
 *
 * @param <W> the world type
 */
public final class AbstractWorld<W> implements World {

    private final DirtCorePlugin plugin;
    private final WorldFactory<?, W> factory;
    private final W world;

    public AbstractWorld(final DirtCorePlugin plugin, final WorldFactory<?, W> factory,
            final W world) {
        this.plugin = plugin;
        this.factory = factory;
        this.world = world;
    }

    @Override
    public @NonNull DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NonNull String getIdentifier() {
        return this.factory.getIdentifier(this.world);
    }

    @Override
    public @NonNull List<Player> getPlayers(@NonNull final Predicate<? super Player> predicate,
            final int maxResults) {
        return this.factory.getPlayers(this.world, predicate, maxResults);
    }

    @Override
    public @NonNull Block getBlockAt(final int x, final int y, final int z) {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.getBlockAt(this.world, x, y, z));
    }

    @Override
    public int getHeightAt(final int x, final int z) {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.getHeightAt(this.world, x, z));
    }

    @Override
    public @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(@NonNull final AABB aabb) {
        return this.plugin.getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.factory.getEntitiesInAABBNoPlayers(this.world, aabb));
    }

    @Override
    public @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(final int chunkX,
            final int chunkZ) {
        return this.plugin.getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.factory.getEntitiesInChunkNoPlayers(this.world, chunkX, chunkZ));
    }

    @Override
    public boolean hasChunk(final int chunkX, final int chunkZ) {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.hasChunk(this.world, chunkX, chunkZ));
    }

    @Override
    public boolean loadChunk(final int chunkX, final int chunkZ) {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.loadChunk(this.world, chunkX, chunkZ));
    }

    @Override
    public boolean isInWorldBounds(final int x, final int y, final int z) {
        return this.factory.isInWorldBounds(this.world, x, y, z);
    }

    @Override
    public boolean isInSpawnableBounds(final int x, final int y, final int z) {
        return this.factory.isInSpawnableBounds(this.world, x, y, z);
    }

    @Override
    public long getDayTime() {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.getDayTime(this.world));
    }

    @Override
    public boolean setDayTime(final long time) {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.setDayTime(this.world, time));
    }

    @Override
    public void playAnimationAt(@NonNull final Animation animation, final double x, final double y,
            final double z) {
        this.factory.playAnimationAt(this.world, animation, x, y, z);
    }

    @Override
    public @NonNull Collection<ChunkEntityProfile> getEntityProfiles() {
        return this.plugin.getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.factory.getEntityProfiles(this.world));
    }

    @Override
    public @NonNull Optional<ChunkEntityProfile> getEntityProfile(final int chunkX,
            final int chunkZ) {
        return this.plugin.getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.factory.getEntityProfile(this.world, chunkX, chunkZ));
    }

    public W getWorld() {
        return this.world;
    }
}
