/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform;

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
import net.dirtcraft.dirtcore.common.model.Animation;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.profile.ChunkEntityProfile;
import net.dirtcraft.dirtcore.common.platform.minecraft.world.AbstractWorld;
import net.dirtcraft.dirtcore.common.platform.minecraft.world.WorldFactory;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeWorldFactory extends WorldFactory<DirtCoreForgePlugin,
        net.minecraft.world.World> {

    private final ForgePlatformFactory platformFactory;

    public ForgeWorldFactory(final DirtCoreForgePlugin plugin,
            final ForgePlatformFactory platformFactory) {
        super(plugin);
        this.platformFactory = platformFactory;
    }

    @Override
    protected net.minecraft.world.@NonNull World transformWorld(@NonNull final World world) {
        if (world instanceof AbstractWorld) {
            //noinspection unchecked
            return ((AbstractWorld<net.minecraft.world.World>) world).getWorld();
        }

        throw new AssertionError();
    }

    @Override
    protected @NonNull String getIdentifier(final net.minecraft.world.@NonNull World world) {
        return ForgeUtils.getWorldName(world);
    }

    @Override
    protected @NonNull List<Player> getPlayers(final net.minecraft.world.@NonNull World world,
            @NonNull final Predicate<? super Player> predicate, final int maxResults) {
        final List<Player> list = new ArrayList<>();

        //noinspection unchecked
        for (final EntityPlayer minecraftPlayer : (List<EntityPlayer>) world.playerEntities) {
            if (!(minecraftPlayer instanceof EntityPlayerMP)) {
                continue;
            }

            final Player player = this.platformFactory.wrapPlayer((EntityPlayerMP) minecraftPlayer);

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
    protected @NonNull Block getBlockAt(final net.minecraft.world.@NonNull World world, final int x,
            final int y, final int z) {
        final net.minecraft.block.Block block = world.getBlock(x, y, z);
        final int metadata = world.getBlockMetadata(x, y, z);
        final TileEntity tileEntity = world.getTileEntity(x, y, z);

        return this.platformFactory.wrapBlock(ForgeBlock.of(block, metadata, tileEntity));
    }

    @Override
    protected int getHeightAt(final net.minecraft.world.@NonNull World world, final int x,
            final int z) {
        return world.getHeightValue(x, z);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(
            final net.minecraft.world.@NonNull World world, @NonNull final AABB aabb) {
        final Collection<net.minecraft.entity.Entity> entities =
                (List<net.minecraft.entity.Entity>) world.getEntitiesWithinAABBExcludingEntity(null,
                        ForgePlatformFactory.transformAABB(aabb),
                        entity -> !(entity instanceof EntityPlayer) && entity.isEntityAlive());
        return this.platformFactory.wrapEntities(entities);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NonNull Collection<Entity> getEntitiesInChunkNoPlayers(
            final net.minecraft.world.@NonNull World world, final int chunkX, final int chunkZ) {
        if (!(world instanceof WorldServer)) {
            return Collections.emptySet();
        }

        if (!this.hasChunk(world, chunkX, chunkZ)) {
            return Collections.emptySet();
        }

        final WorldServer worldServer = (WorldServer) world;
        final ChunkProviderServer chunkProvider =
                (ChunkProviderServer) worldServer.getChunkProvider();
        final Chunk loadedChunk = this.getLoadedChunk(chunkProvider, chunkX, chunkZ);

        if (loadedChunk == null) {
            return Collections.emptySet();
        }

        final Set<Entity> entities = new HashSet<>();

        for (final List<net.minecraft.entity.Entity> entityList : loadedChunk.entityLists) {
            entityList.stream().filter(entity -> !(entity instanceof EntityPlayer))
                    .map(this.platformFactory::wrapEntity).forEach(entities::add);
        }

        return entities;
    }

    @Override
    protected boolean hasChunk(final net.minecraft.world.@NonNull World world, final int chunkX,
            final int chunkZ) {
        return world.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    @Override
    protected boolean loadChunk(final net.minecraft.world.@NonNull World world, final int chunkX,
            final int chunkZ) {
        return world.getChunkProvider().loadChunk(chunkX, chunkZ) != null;
    }

    @Override
    protected boolean isInWorldBounds(final net.minecraft.world.@NonNull World world, final int x,
            final int y, final int z) {
        return world.blockExists(x, y, z);
    }

    @Override
    protected boolean isInSpawnableBounds(final net.minecraft.world.@NonNull World world,
            final int x, final int y, final int z) {
        return world.blockExists(x, y, z);
    }

    @Override
    protected long getDayTime(final net.minecraft.world.@NonNull World world) {
        return world.getWorldTime();
    }

    @Override
    protected boolean setDayTime(final net.minecraft.world.@NonNull World world, final long time) {
        world.setWorldTime(time);
        return true;
    }

    @Override
    protected void playAnimationAt(final net.minecraft.world.@NonNull World world,
            @NonNull final Animation animation, final double x, final double y, final double z) {
        if (!(world instanceof WorldServer)) {
            return;
        }

        final WorldServer worldServer = (WorldServer) world;
        final String particleName = this.parseAnimation(animation);

        worldServer.func_147487_a(particleName, x, y, z, animation.getParticleCount(),
                animation.getXOffset(), animation.getYOffset(), animation.getZOffset(),
                animation.getSpeed());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected @NonNull Collection<ChunkEntityProfile> getEntityProfiles(
            final net.minecraft.world.@NonNull World world) {
        if (!(world instanceof WorldServer)) {
            return Collections.emptySet();
        }

        final WorldServer worldServer = (WorldServer) world;
        final ChunkProviderServer chunkProvider =
                (ChunkProviderServer) worldServer.getChunkProvider();
        final List<Chunk> loadedChunks = (List<Chunk>) chunkProvider.loadedChunks;
        final List<ChunkEntityProfile> list = new ArrayList<>(loadedChunks.size());

        for (final Chunk chunk : loadedChunks) {
            list.add(this.getEntityProfile(worldServer, chunk));
        }

        return list;
    }

    @Override
    protected @NonNull Optional<ChunkEntityProfile> getEntityProfile(
            final net.minecraft.world.@NonNull World world, final int chunkX, final int chunkZ) {
        if (!(world instanceof WorldServer)) {
            return Optional.empty();
        }

        final WorldServer worldServer = (WorldServer) world;
        final ChunkProviderServer chunkProvider =
                (ChunkProviderServer) worldServer.getChunkProvider();
        final Chunk loadedChunk = this.getLoadedChunk(chunkProvider, chunkX, chunkZ);

        if (loadedChunk == null) {
            return Optional.empty();
        }

        return Optional.of(this.getEntityProfile(worldServer, loadedChunk));
    }

    @Nullable
    private Chunk getLoadedChunk(@NonNull final ChunkProviderServer chunkProviderServer,
            final int x, final int z) {
        final long i = ChunkCoordIntPair.chunkXZ2Int(x, z);
        return (Chunk) chunkProviderServer.id2ChunkMap.getValueByKey(i);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private ChunkEntityProfile getEntityProfile(@NonNull final WorldServer worldServer,
            @NonNull final Chunk chunk) {
        final Map<String, Integer> map = new HashMap<>();
        final AtomicInteger total = new AtomicInteger();

        for (final List<net.minecraft.entity.Entity> entityList : chunk.entityLists) {
            entityList.stream().filter(entity -> !(entity instanceof EntityPlayer))
                    .forEach(entity -> {
                        final String type = this.platformFactory.getEntityFactory().getType(entity);

                        map.put(type, map.getOrDefault(type, 0) + 1);
                        total.addAndGet(1);
                    });
        }

        return ChunkEntityProfile.of(map, total.get(), this.getIdentifier(worldServer),
                chunk.xPosition, chunk.zPosition);
    }

    @NonNull
    private String parseAnimation(@NonNull final Animation animation) {
        switch (animation) {
            case CRATE_REWARD:
                return "fireworksSpark";
            default:
                throw new AssertionError();
        }
    }
}
