/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.commands.teleport;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import net.dirtcraft.dirtcore.common.command.DefaultArguments;
import net.dirtcraft.dirtcore.common.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.dirtcraft.dirtcore.common.util.TriFunction;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TeleportChunkCommand extends AbstractCommand<DirtCorePlugin, Sender> {

    public static TriFunction<String, Integer, Integer, String> COMMAND_TELEPORT_CHUNK =
            (worldName, chunkX, chunkZ) -> "/teleport-chunk " + chunkX + ' ' + chunkZ + ' '
                    + worldName;

    public TeleportChunkCommand(final DirtCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public ArgumentBuilder<DirtCorePlugin, Sender, ?> build(
            @NonNull final ArgumentFactory<DirtCorePlugin> factory) {
        final DefaultArguments.Argument<Collection<Player>> playersArgument =
                DefaultArguments.PLAYER_TARGETS.fromFactory(factory);
        final DefaultArguments.Argument<World> worldArgument =
                DefaultArguments.WORLD.fromFactory(factory);

        return Commands.aliases("teleport-chunk", "tpc", "tpchunk")
                .requiresPermission(Permission.TELEPORT)
                .then(DefaultArguments.CHUNK_POS.getArgument().consoleUsage(ConsoleUsage.DENIED)
                        .executes(context -> this.teleportChunk(context.getSource(),
                                DefaultArguments.CHUNK_POS.fromContext(context)))
                        .then(worldArgument.getArgument()
                                .executes(context -> this.teleportChunk(context.getSource(),
                                        DefaultArguments.CHUNK_POS.fromContext(context),
                                        worldArgument.fromContext(context)))))
                .then(playersArgument.getArgument().requiresPermission(Permission.TELEPORT_OTHERS)
                        .then(DefaultArguments.CHUNK_POS.getArgument()
                                .executes(context -> this.teleportChunk(context.getSource(),
                                        playersArgument.fromContext(context),
                                        DefaultArguments.CHUNK_POS.fromContext(context)))
                                .then(worldArgument.getArgument()
                                        .executes(context -> this.teleportChunk(context.getSource(),
                                                playersArgument.fromContext(context),
                                                DefaultArguments.CHUNK_POS.fromContext(context),
                                                worldArgument.fromContext(context))))));
    }

    private int teleportChunk(@NonNull final Sender sender, @NonNull final Vec2i chunkPos) {
        final Player player = sender.getPlayerOrException();
        return this.teleportChunk(sender, Collections.singleton(player), chunkPos,
                player.getWorld());
    }

    private int teleportChunk(@NonNull final Sender sender, @NonNull final Vec2i chunkPos,
            @NonNull final World world) {
        final Player player = sender.getPlayerOrException();
        return this.teleportChunk(sender, Collections.singleton(player), chunkPos, world);
    }

    private int teleportChunk(@NonNull final Sender sender,
            @NonNull final Collection<Player> players, @NonNull final Vec2i chunkPos) {
        final Optional<World> worldOptional = sender.getWorld();

        if (!worldOptional.isPresent()) {
            sender.sendMessage(Components.NO_WORLD_PROVIDED.build());
            return Command.SINGLE_SUCCESS;
        }

        return this.teleportChunk(sender, players, chunkPos, worldOptional.get());
    }

    private int teleportChunk(@NonNull final Sender sender,
            @NonNull final Collection<Player> players, @NonNull final Vec2i chunkPos,
            @NonNull final World world) {
        // keeps track of successful teleports
        final AtomicInteger counter = new AtomicInteger();

        this.plugin.getBootstrap().getScheduler().executeSyncBlocking(() -> {
            if (!world.hasChunk(chunkPos.x, chunkPos.y)) {
                // if chunk at x, z is not loaded, load it manually
                if (!world.loadChunk(chunkPos.x, chunkPos.y)) {
                    sender.sendMessage(Components.COULD_NOT_LOAD_CHUNK.build(world, chunkPos));
                    return;
                }
            }

            final int x = World.chunkToBlockCoordinate(chunkPos.x);
            final int z = World.chunkToBlockCoordinate(chunkPos.y);
            final int y = world.getHeightAt(x, z);

            players.forEach(player -> {
                // adjust to block position; look in chunk direction
                if (player.teleport(world, x + 0.5D, y, z + 0.5D, -45F, player.getRotation().x)) {
                    // upon successful teleport
                    player.sendMessage(Components.TELEPORT_CHUNK.build(world, chunkPos));
                    counter.getAndIncrement();
                }
            });
        });

        final int size = players.size();

        if (counter.get() < size) {
            sender.sendMessage(Components.TELEPORT_CHUNK_OTHERS_FAILED.build(world, chunkPos));
        } else if (!(players.size() == 1 && sender.getUniqueId()
                .equals(players.iterator().next().getUniqueId()))) {
            // if the player collection does not only contain the sender player,
            // send a summary message
            sender.sendMessage(Components.TELEPORT_CHUNK_OTHERS.build(world, chunkPos));
        }

        return Command.SINGLE_SUCCESS;
    }
}
