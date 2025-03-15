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

package net.dirtcraft.dirtcore.forge_1_20_1.platform;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.model.Animation;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.profile.ChunkEntityProfile;
import net.dirtcraft.dirtcore.common.platform.minecraft.world.AbstractWorld;
import net.dirtcraft.dirtcore.common.platform.minecraft.world.WorldFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.EntitySectionStorage;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.levelgen.Heightmap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeWorldFactory extends WorldFactory<DirtCoreForgePlugin, Level> {

    private static final Field PERSISTENT_ENTITY_SELECTION_MANAGER_SELECTION_STORAGE;
    private static final Field SERVER_LEVEL_ENTITY_MANAGER;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            PERSISTENT_ENTITY_SELECTION_MANAGER_SELECTION_STORAGE =
                    PersistentEntitySectionManager.class.getDeclaredField("f_157495_");
            PERSISTENT_ENTITY_SELECTION_MANAGER_SELECTION_STORAGE.setAccessible(true);
            //noinspection JavaReflectionMemberAccess
            SERVER_LEVEL_ENTITY_MANAGER = ServerLevel.class.getDeclaredField("f_143244_");
            SERVER_LEVEL_ENTITY_MANAGER.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final ForgePlatformFactory platformFactory;

    public ForgeWorldFactory(final DirtCoreForgePlugin plugin,
            final ForgePlatformFactory platformFactory) {
        super(plugin);
        this.platformFactory = platformFactory;
    }

    @Override
    protected @NonNull Level transformWorld(@NonNull final World world) {
        if (world instanceof AbstractWorld) {
            //noinspection unchecked
            return ((AbstractWorld<Level>) world).getWorld();
        }

        throw new AssertionError();
    }

    @Override
    protected @NonNull String getIdentifier(@NonNull final Level world) {
        return world.dimension().location().toString();
    }

    @Override
    protected @NonNull List<Player> getPlayers(@NonNull final Level world,
            @NonNull final Predicate<? super Player> predicate, final int maxResults) {
        final List<Player> list = new ArrayList<>();

        for (final net.minecraft.world.entity.player.Player minecraftPlayer : world.players()) {
            if (!(minecraftPlayer instanceof ServerPlayer)) {
                continue;
            }

            final Player player = this.platformFactory.wrapPlayer((ServerPlayer) minecraftPlayer);

            if (predicate.test(player)) {
                list.add(player);

                if (list.size() >= maxResults) {
                    return list;
                }
            }
        }

        return list;
    }

    @Override
    protected @NonNull Block getBlockAt(@NonNull final Level world, final int x, final int y,
            final int z) {
        final BlockPos blockPos = new BlockPos(x, y, z);
        final BlockState blockState = world.getBlockState(blockPos);
        final BlockEntity blockEntity = world.getBlockEntity(blockPos);

        return this.platformFactory.wrapBlock(ForgeBlock.of(blockState, blockEntity));
    }

    @Override
    protected int getHeightAt(@NonNull final Level world, final int x, final int z) {
        return world.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
    }

    @Override
    protected @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(@NonNull final Level world,
            @NonNull final AABB aabb) {
        final Collection<net.minecraft.world.entity.Entity> entities =
                world.getEntities((net.minecraft.world.entity.Entity) null,
                        ForgePlatformFactory.transformAABB(aabb),
                        entity -> !(entity instanceof net.minecraft.world.entity.player.Player)
                                && entity.isAlive());
        return this.platformFactory.wrapEntities(entities);
    }

    @Override
    protected @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(@NonNull final Level world,
            final int chunkX, final int chunkZ) {
        if (!(world instanceof final ServerLevel serverLevel)) {
            return Collections.emptySet();
        }

        if (!this.hasChunk(serverLevel, chunkX, chunkZ)) {
            return Collections.emptySet();
        }

        final long chunkPos = ChunkPos.asLong(chunkX, chunkZ);
        final Stream<EntitySection<net.minecraft.world.entity.Entity>> sections =
                this.getEntitySelectionStorage(serverLevel).getExistingSectionsInChunk(chunkPos);
        final Set<Entity> entities = new HashSet<>();

        sections.forEach(section -> {
            if (section.getStatus().isAccessible()) {
                section.getEntities()
                        .filter(entity -> !(entity instanceof net.minecraft.world.entity.player.Player))
                        .map(this.platformFactory::wrapEntity).forEach(entities::add);
            }
        });

        return entities;
    }

    @Override
    protected boolean hasChunk(@NonNull final Level world, final int chunkX, final int chunkZ) {
        return world.hasChunk(chunkX, chunkZ);
    }

    @Override
    protected boolean loadChunk(@NonNull final Level world, final int chunkX, final int chunkZ) {
        //noinspection ConstantValue
        return world.getChunk(chunkX, chunkZ) != null;
    }

    @Override
    protected boolean isInWorldBounds(@NonNull final Level world, final int x, final int y,
            final int z) {
        return world.isInWorldBounds(new BlockPos(x, y, z));
    }

    @Override
    protected boolean isInSpawnableBounds(@NonNull final Level world, final int x, final int y,
            final int z) {
        return Level.isInSpawnableBounds(new BlockPos(x, y, z));
    }

    @Override
    protected long getDayTime(@NonNull final Level world) {
        return world.getDayTime();
    }

    @Override
    protected boolean setDayTime(@NonNull final Level world, final long time) {
        if (world instanceof ServerLevel) {
            ((ServerLevel) world).setDayTime(time);
            return true;
        }

        return false;
    }

    @Override
    protected void playAnimationAt(@NonNull final Level level, @NonNull final Animation animation,
            final double x, final double y, final double z) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return;
        }

        final ParticleOptions particleOptions = this.parseAnimation(animation);
        serverLevel.sendParticles(particleOptions, x, y, z, animation.getParticleCount(),
                animation.getXOffset(), animation.getYOffset(), animation.getZOffset(),
                animation.getSpeed());
    }

    @Override
    protected @NonNull Collection<ChunkEntityProfile> getEntityProfiles(
            @NonNull final Level world) {
        if (!(world instanceof final ServerLevel serverLevel)) {
            return Collections.emptySet();
        }

        final EntitySectionStorage<net.minecraft.world.entity.Entity> entitySectionStorage =
                this.getEntitySelectionStorage(serverLevel);
        final LongSet loadedChunks = entitySectionStorage.getAllChunksWithExistingSections();
        final List<ChunkEntityProfile> list = new ArrayList<>(loadedChunks.size());

        for (final LongIterator iterator = loadedChunks.iterator(); iterator.hasNext(); ) {
            final long chunkPos = iterator.nextLong();
            this.getEntityProfile(serverLevel, entitySectionStorage, chunkPos).ifPresent(list::add);
        }

        return list;
    }

    @Override
    protected @NonNull Optional<ChunkEntityProfile> getEntityProfile(@NonNull final Level world,
            final int chunkX, final int chunkZ) {
        if (!(world instanceof final ServerLevel serverLevel)) {
            return Optional.empty();
        }

        if (!this.hasChunk(serverLevel, chunkX, chunkZ)) {
            return Optional.empty();
        }

        return this.getEntityProfile(serverLevel, this.getEntitySelectionStorage(serverLevel),
                chunkX, chunkZ);
    }

    @SuppressWarnings("unchecked")
    private EntitySectionStorage<net.minecraft.world.entity.Entity> getEntitySelectionStorage(
            @NonNull final ServerLevel serverLevel) {
        try {
            final PersistentEntitySectionManager<net.minecraft.world.entity.Entity> entityManager =
                    (PersistentEntitySectionManager<net.minecraft.world.entity.Entity>) SERVER_LEVEL_ENTITY_MANAGER.get(
                            serverLevel);
            return (EntitySectionStorage<net.minecraft.world.entity.Entity>) PERSISTENT_ENTITY_SELECTION_MANAGER_SELECTION_STORAGE.get(
                    entityManager);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private Optional<ChunkEntityProfile> getEntityProfile(@NonNull final ServerLevel serverLevel,
            @NonNull final EntitySectionStorage<net.minecraft.world.entity.Entity> entitySectionStorage,
            final long chunkPos) {
        return this.getEntityProfile(serverLevel, entitySectionStorage, chunkPos,
                ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos));
    }

    @NonNull
    private Optional<ChunkEntityProfile> getEntityProfile(@NonNull final ServerLevel serverLevel,
            @NonNull final EntitySectionStorage<net.minecraft.world.entity.Entity> entitySectionStorage,
            final int chunkX, final int chunkZ) {
        return this.getEntityProfile(serverLevel, entitySectionStorage,
                ChunkPos.asLong(chunkX, chunkZ), chunkX, chunkZ);
    }

    @NonNull
    private Optional<ChunkEntityProfile> getEntityProfile(@NonNull final ServerLevel serverLevel,
            @NonNull final EntitySectionStorage<net.minecraft.world.entity.Entity> entitySectionStorage,
            final long chunkPos, final int chunkX, final int chunkZ) {
        final Stream<EntitySection<net.minecraft.world.entity.Entity>> sections =
                entitySectionStorage.getExistingSectionsInChunk(chunkPos);
        final AtomicInteger sectionCount = new AtomicInteger();
        final Map<String, Integer> map = new HashMap<>();
        final AtomicInteger total = new AtomicInteger();

        sections.forEach(section -> {
            if (section.getStatus().isAccessible()) {
                sectionCount.getAndIncrement();
                section.getEntities()
                        .filter(entity -> !(entity instanceof net.minecraft.world.entity.player.Player))
                        .forEach(entity -> {
                            final String type = EntityType.getKey(entity.getType()).toString();

                            map.put(type, map.getOrDefault(type, 0) + 1);
                            total.addAndGet(1);
                        });
            }
        });

        if (sectionCount.get() == 0) {
            return Optional.empty();
        }

        return Optional.of(
                ChunkEntityProfile.of(map, total.get(), this.getIdentifier(serverLevel), chunkX,
                        chunkZ));
    }

    @NonNull
    private ParticleOptions parseAnimation(@NonNull final Animation animation) {
        switch (animation) {
            case CRATE_REWARD:
                return ParticleTypes.FIREWORK;
            default:
                throw new AssertionError();
        }
    }
}
