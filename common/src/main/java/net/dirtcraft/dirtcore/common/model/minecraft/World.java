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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.model.Animation;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.profile.ChunkEntityProfile;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a world within the minecraft world implementations.
 */
public interface World {

    /**
     * Transforms block coordinates to chunk coordinates.
     * 16 block coordinates are equal to 1 chunk coordinate.
     *
     * @param coordinate the block coordinate
     * @return the chunk coordinate
     */
    static int blockToChunkCoordinate(final int coordinate) {
        return coordinate >> 4;
    }

    /**
     * Transforms chunk coordinates to block coordinates.
     * 1 chunk coordinate is equal to 16 block coordinates.
     *
     * @param coordinate the chunk coordinate
     * @return the block coordinate
     */
    static int chunkToBlockCoordinate(final int coordinate) {
        return coordinate << 4;
    }

    /**
     * Gets the plugin instance the world is from.
     *
     * @return the plugin
     */
    @NonNull DirtCorePlugin getPlugin();

    /**
     * Gets the identifier.
     *
     * @return the identifier
     */
    @NonNull String getIdentifier();

    /**
     * Gets the players in the world.
     *
     * @param predicate  the predicate
     * @param maxResults the max results
     * @return the players
     */
    @NonNull List<Player> getPlayers(@NonNull Predicate<? super Player> predicate,
            final int maxResults);

    /**
     * Gets the block at a position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the block
     */
    @NonNull Block getBlockAt(int x, int y, int z);

    /**
     * Gets the height at a position.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return the height
     */
    int getHeightAt(int x, int z);

    /**
     * Gets the entities in an area. Players are excluded.
     *
     * @param aabb the area
     * @return the entities
     */
    @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(@NonNull AABB aabb);

    /**
     * Gets the entities in a chunk. Players are excluded.
     *
     * @param chunkX the x chunk coordinate
     * @param chunkZ the z chunk coordinate
     * @return the entities
     */
    @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(int chunkX, int chunkZ);

    /**
     * Checks if a chunk exists.
     *
     * @param chunkX the x chunk coordinate
     * @param chunkZ the z chunk coordinate
     * @return true, if the chunk exists
     */
    boolean hasChunk(int chunkX, int chunkZ);

    /**
     * Loads a chunk.
     *
     * @param chunkX the x chunk coordinate
     * @param chunkZ the z chunk coordinate
     * @return true, if the chunk has been loaded
     */
    boolean loadChunk(int chunkX, int chunkZ);

    /**
     * Checks if a position is in world bounds.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return true, if the position is in bounds
     */
    boolean isInWorldBounds(int x, int y, int z);

    /**
     * Checks if entities can exist at a position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return true, if entities can exist at the position
     */
    boolean isInSpawnableBounds(int x, int y, int z);

    /**
     * Gets the day time.
     *
     * @return the day time
     */
    long getDayTime();

    /**
     * Sets the day time.
     *
     * @param time the time
     * @return true, if the time could be set
     */
    boolean setDayTime(long time);

    /**
     * Plays an animation at a position.
     *
     * @param animation the animation
     * @param x         the x coordinate
     * @param y         the y coordinate
     * @param z         the z coordinate
     */
    void playAnimationAt(@NonNull Animation animation, double x, double y, double z);

    /**
     * Gets the entity profiles for this world.
     *
     * @return the entity profiles
     */
    @NonNull Collection<ChunkEntityProfile> getEntityProfiles();

    /**
     * Gets the entity profile for a chunk in this world.
     *
     * @param chunkX the x chunk coordinate
     * @param chunkZ the z chunk coordinate
     * @return the entity profile, if available
     */
    @NonNull Optional<ChunkEntityProfile> getEntityProfile(int chunkX, int chunkZ);

    default @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(@NonNull final Vec2i chunkPos) {
        return this.getEntitiesInChunkNoPlayers(chunkPos.x, chunkPos.y);
    }

    default @NonNull List<Player> getPlayers(@NonNull final Predicate<? super Player> predicate) {
        return this.getPlayers(predicate, Integer.MAX_VALUE);
    }

    default boolean hasChunk(@NonNull final Vec2i chunkPos) {
        return this.hasChunk(chunkPos.x, chunkPos.y);
    }

    default boolean hasChunkAt(final int x, final int z) {
        return this.hasChunk(blockToChunkCoordinate(x), blockToChunkCoordinate(z));
    }

    default boolean hasChunkAt(@NonNull final BlockPos blockPos) {
        return this.hasChunkAt(blockPos.getX(), blockPos.getZ());
    }

    default boolean loadChunk(@NonNull final Vec2i chunkPos) {
        return this.loadChunk(chunkPos.x, chunkPos.y);
    }

    default boolean isInWorldBounds(@NonNull final BlockPos blockPos) {
        return this.isInWorldBounds(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }

    default boolean isInSpawnableBounds(@NonNull final BlockPos blockPos) {
        return this.isInSpawnableBounds(blockPos.getX(), blockPos.getY(), blockPos.getZ());
    }
}
