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
import java.util.Objects;
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
 * Factory class to make a thread-safe world instance.
 *
 * @param <P> the plugin type
 * @param <W> the world type
 */
public abstract class WorldFactory<P extends DirtCorePlugin, W> {

    private final P plugin;

    public WorldFactory(final P plugin) {this.plugin = plugin;}

    protected abstract @NonNull W transformWorld(@NonNull World world);

    protected abstract @NonNull String getIdentifier(@NonNull W world);

    protected abstract @NonNull List<Player> getPlayers(@NonNull W world,
            @NonNull Predicate<? super Player> predicate, int maxResults);

    protected abstract @NonNull Block getBlockAt(@NonNull W world, int x, int y, int z);

    protected abstract int getHeightAt(@NonNull W world, int x, int z);

    protected abstract @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(@NonNull W world,
            @NonNull AABB aabb);

    protected abstract @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(@NonNull W world,
            int chunkX, int chunkZ);

    protected abstract boolean hasChunk(@NonNull W world, int chunkX, int chunkZ);

    protected abstract boolean loadChunk(@NonNull W world, int chunkX, int chunkZ);

    protected abstract boolean isInWorldBounds(@NonNull W world, int x, int y, int z);

    protected abstract boolean isInSpawnableBounds(@NonNull W world, int x, int y, int z);

    protected abstract long getDayTime(@NonNull W world);

    protected abstract boolean setDayTime(@NonNull W world, long time);

    protected abstract void playAnimationAt(@NonNull W world, @NonNull Animation animation,
            final double x, final double y, final double z);

    protected abstract @NonNull Collection<ChunkEntityProfile> getEntityProfiles(@NonNull W world);

    protected abstract @NonNull Optional<ChunkEntityProfile> getEntityProfile(@NonNull W world,
            int chunkX, int chunkZ);

    public final World wrap(final W world) {
        Objects.requireNonNull(world, "world");
        return new AbstractWorld<>(this.plugin, this, world);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
