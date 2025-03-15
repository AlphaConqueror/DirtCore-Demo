/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform;

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
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.checkerframework.checker.nullness.qual.NonNull;

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

        for (final EntityPlayer minecraftPlayer : world.playerEntities) {
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
        final BlockPos blockPos = new BlockPos(x, y, z);
        final IBlockState blockState = world.getBlockState(blockPos);
        final net.minecraft.block.Block block = blockState.getBlock();
        final int metadata = block.getMetaFromState(blockState);
        final TileEntity tileEntity = world.getTileEntity(blockPos);

        return this.platformFactory.wrapBlock(ForgeBlock.of(block, metadata, tileEntity));
    }

    @Override
    protected int getHeightAt(final net.minecraft.world.@NonNull World world, final int x,
            final int z) {
        return world.getHeight(x, z);
    }

    @Override
    protected @NonNull Collection<Entity> getEntitiesInAABBNoPlayers(
            final net.minecraft.world.@NonNull World world, @NonNull final AABB aabb) {
        final Collection<net.minecraft.entity.Entity> entities =
                world.getEntitiesInAABBexcluding(null, ForgePlatformFactory.transformAABB(aabb),
                        entity -> !(entity instanceof EntityPlayer) && entity.isEntityAlive());
        return this.platformFactory.wrapEntities(entities);
    }

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
        final Chunk loadedChunk = worldServer.getChunkProvider().getLoadedChunk(chunkX, chunkZ);

        if (loadedChunk == null) {
            return Collections.emptySet();
        }

        final Set<Entity> entities = new HashSet<>();

        for (final ClassInheritanceMultiMap<net.minecraft.entity.Entity> entityList :
                loadedChunk.getEntityLists()) {
            entityList.stream().filter(entity -> !(entity instanceof EntityPlayer))
                    .map(this.platformFactory::wrapEntity).forEach(entities::add);
        }

        return entities;
    }

    @Override
    protected boolean hasChunk(final net.minecraft.world.@NonNull World world, final int chunkX,
            final int chunkZ) {
        if (!(world instanceof WorldServer)) {
            return false;
        }

        final WorldServer worldServer = (WorldServer) world;
        return worldServer.getChunkProvider().chunkExists(chunkX, chunkZ);
    }

    @Override
    protected boolean loadChunk(final net.minecraft.world.@NonNull World world, final int chunkX,
            final int chunkZ) {
        if (!(world instanceof WorldServer)) {
            return false;
        }

        final WorldServer worldServer = (WorldServer) world;
        return worldServer.getChunkProvider().loadChunk(chunkX, chunkZ) != null;
    }

    @Override
    protected boolean isInWorldBounds(final net.minecraft.world.@NonNull World world, final int x,
            final int y, final int z) {
        final BlockPos blockPos = new BlockPos(x, y, z);
        return world.isOutsideBuildHeight(blockPos) && world.getWorldBorder().contains(blockPos);
    }

    @Override
    protected boolean isInSpawnableBounds(final net.minecraft.world.@NonNull World world,
            final int x, final int y, final int z) {
        return world.isSpawnChunk(x, z);
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
        final EnumParticleTypes particleTypes = this.parseAnimation(animation);

        worldServer.spawnParticle(particleTypes, x, y, z, animation.getParticleCount(),
                animation.getXOffset(), animation.getYOffset(), animation.getZOffset(),
                animation.getSpeed());
    }

    @Override
    protected @NonNull Collection<ChunkEntityProfile> getEntityProfiles(
            final net.minecraft.world.@NonNull World world) {
        if (!(world instanceof WorldServer)) {
            return Collections.emptySet();
        }

        final WorldServer worldServer = (WorldServer) world;
        final Collection<Chunk> loadedChunks = worldServer.getChunkProvider().getLoadedChunks();
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
        final Chunk loadedChunk = worldServer.getChunkProvider().getLoadedChunk(chunkX, chunkZ);

        if (loadedChunk == null) {
            return Optional.empty();
        }

        return Optional.of(this.getEntityProfile(worldServer, loadedChunk));
    }

    @NonNull
    private ChunkEntityProfile getEntityProfile(@NonNull final WorldServer worldServer,
            @NonNull final Chunk chunk) {
        final Map<String, Integer> map = new HashMap<>();
        final AtomicInteger total = new AtomicInteger();

        for (final ClassInheritanceMultiMap<net.minecraft.entity.Entity> entityList :
                chunk.getEntityLists()) {
            entityList.stream().filter(entity -> !(entity instanceof EntityPlayer))
                    .forEach(entity -> {
                        final String type = this.platformFactory.getEntityFactory().getType(entity);

                        map.put(type, map.getOrDefault(type, 0) + 1);
                        total.addAndGet(1);
                    });
        }

        return ChunkEntityProfile.of(map, total.get(), this.getIdentifier(worldServer), chunk.x,
                chunk.z);
    }

    @NonNull
    private EnumParticleTypes parseAnimation(@NonNull final Animation animation) {
        switch (animation) {
            case CRATE_REWARD:
                return EnumParticleTypes.FIREWORKS_SPARK;
            default:
                throw new AssertionError();
        }
    }
}
