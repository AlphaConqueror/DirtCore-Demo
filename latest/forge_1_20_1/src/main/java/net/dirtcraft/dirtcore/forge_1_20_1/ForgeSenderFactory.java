/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.platform.sender.SenderFactory;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeSenderFactory extends SenderFactory<DirtCoreForgePlugin, CommandSourceStack> {

    public ForgeSenderFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    public UUID getUniqueId(@NonNull final CommandSourceStack commandSourceStack) {
        if (commandSourceStack.source instanceof Player) {
            return ((Player) commandSourceStack.source).getUUID();
        }

        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(@NonNull final CommandSourceStack commandSourceStack) {
        if (commandSourceStack.getEntity() instanceof Player) {
            return commandSourceStack.getTextName();
        }

        return Sender.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(@NonNull final CommandSourceStack commandSourceStack,
            @NonNull final Component message) {
        commandSourceStack.sendSystemMessage(
                this.getPlugin().getPlatformFactory().transformComponent(message));
    }

    @Override
    public void sendMessage(@NonNull final CommandSourceStack commandSourceStack,
            @NonNull final Iterable<Component> messages) {
        messages.forEach(message -> this.sendMessage(commandSourceStack, message));
    }

    @Override
    public boolean isConsole(@NonNull final CommandSourceStack commandSourceStack) {
        return !(commandSourceStack.source instanceof Player);
    }

    @Override
    public boolean isEntity(@NonNull final CommandSourceStack commandSourceStack) {
        return false;
    }

    @Override
    public @NonNull Optional<net.dirtcraft.dirtcore.common.model.minecraft.Entity> getEntity(
            @NonNull final CommandSourceStack commandSourceStack) {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<World> getWorld(@NonNull final CommandSourceStack commandSourceStack) {
        final ServerLevel serverLevel = commandSourceStack.getLevel();
        return Optional.of(this.getPlugin().getPlatformFactory().wrapWorld(serverLevel));
    }

    @Override
    public @NonNull Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates(
            @NonNull final CommandSourceStack commandSourceStack) {
        final CommandSource commandSource = commandSourceStack.source;

        if (commandSource instanceof Player) {
            final Player player = (Player) commandSource;
            final Level world = player.level();
            final Vec3 eyePosition = player.getEyePosition(1.0F);
            final Vec3 lookVector = player.getViewVector(1.0F)
                    .scale(SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final Vec3 target = eyePosition.add(lookVector);  // Calculate the target point

            // Perform the raytrace using the world.clip method
            final BlockHitResult blockHitResult = world.clip(
                    new ClipContext(eyePosition, target, ClipContext.Block.OUTLINE,
                            ClipContext.Fluid.NONE, player));

            if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                final BlockPos blockpos = blockHitResult.getBlockPos();
                return Collections.singleton(
                        SharedSuggestionProvider.TextCoordinates.from(blockpos.getX(),
                                blockpos.getY(), blockpos.getZ()));
            }
        }

        return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates(
            @NonNull final CommandSourceStack commandSourceStack) {
        final CommandSource commandSource = commandSourceStack.source;

        if (commandSource instanceof Player) {
            final Player player = (Player) commandSource;
            final Level world = player.level();
            final Vec3 eyePosition = player.getEyePosition(1.0F);
            final Vec3 lookVector = player.getViewVector(1.0F)
                    .scale(SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final Vec3 target = eyePosition.add(lookVector);  // Calculate the target point

            // Perform the raytrace using the world.clip method
            final HitResult hitResult = world.clip(
                    new ClipContext(eyePosition, target, ClipContext.Block.OUTLINE,
                            ClipContext.Fluid.NONE, player));

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                final Vec3 vec3 = hitResult.getLocation();
                return Collections.singleton(
                        SharedSuggestionProvider.TextCoordinates.from(vec3.x, vec3.y, vec3.z));
            }
        }

        return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<String> getSelectedEntities(
            @NonNull final CommandSourceStack commandSourceStack) {
        final CommandSource commandSource = commandSourceStack.source;

        if (commandSource instanceof Player) {
            final Player player = (Player) commandSource;
            final Vec3 eyePosition = player.getEyePosition(1.0F);
            final Vec3 lookVector = player.getViewVector(1.0F)
                    .scale(SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            Vec3 target = eyePosition.add(lookVector);
            final Level world = player.level();
            final HitResult result = world.clip(
                    new ClipContext(eyePosition, target, ClipContext.Block.COLLIDER,
                            ClipContext.Fluid.NONE, player));

            if (result.getType() != HitResult.Type.MISS) {
                target = result.getLocation();
            }

            final AABB aabb =
                    player.getBoundingBox().expandTowards(lookVector).inflate(1.0D, 1.0D, 1.0D);
            final List<Entity> entitiesInPath = world.getEntities(player, aabb);
            Double closestDistance = null;
            Entity closestEntity = null;

            for (final Entity entity : entitiesInPath) {
                final AABB entityBoundingBox =
                        entity.getBoundingBox().inflate(entity.getPickRadius());
                final Optional<Vec3> positionOptional = entityBoundingBox.clip(eyePosition, target);

                if (entityBoundingBox.contains(eyePosition)) {
                    // player head inside entity bounding box
                    closestEntity = entity;
                    break;
                }

                if (positionOptional.isPresent()) {
                    final double distance = eyePosition.distanceTo(positionOptional.get());

                    if (closestDistance == null || distance < closestDistance) {
                        closestDistance = distance;
                        closestEntity = entity;
                    }
                }
            }

            if (closestEntity != null) {
                return Collections.singleton(closestEntity.getStringUUID());
            }
        }

        return Collections.emptySet();
    }
}
