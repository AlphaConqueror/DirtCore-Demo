/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.platform.sender.AbstractSender;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.platform.sender.SenderFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeSenderFactory extends SenderFactory<DirtCoreForgePlugin, ICommandSender> {

    public ForgeSenderFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    public UUID getUniqueId(@NonNull final ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return ((EntityPlayer) sender).getUniqueID();
        }

        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(@NonNull final ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            return sender.getCommandSenderName();
        }

        return Sender.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(@NonNull final ICommandSender sender,
            @NonNull final Component message) {
        sender.addChatMessage(this.getPlugin().getPlatformFactory().transformComponent(message));
    }

    @Override
    public void sendMessage(@NonNull final ICommandSender sender,
            @NonNull final Iterable<Component> messages) {
        messages.forEach(message -> this.sendMessage(sender, message));
    }

    @Override
    public boolean isConsole(@NonNull final ICommandSender sender) {
        return !(sender instanceof EntityPlayer);
    }

    @Override
    public boolean isEntity(@NonNull final ICommandSender sender) {
        return false;
    }

    @Override
    public @NonNull Optional<Entity> getEntity(@NonNull final ICommandSender sender) {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<World> getWorld(@NonNull final ICommandSender sender) {
        return Optional.of(
                this.getPlugin().getPlatformFactory().wrapWorld(sender.getEntityWorld()));
    }

    @Override
    public @NonNull Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates(
            @NonNull final ICommandSender sender) {
        return this.getAbsoluteCoordinates(sender);
    }

    @Override
    public @NonNull Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates(
            @NonNull final ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) sender;
            final Vec3 startVec =
                    Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(),
                            player.posZ);
            final Vec3 lookVector = player.getLookVec().normalize();
            final Vec3 endVec = startVec.addVector(
                    lookVector.xCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.yCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.zCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final MovingObjectPosition movingObjectPosition =
                    player.worldObj.rayTraceBlocks(startVec, endVec);

            if (movingObjectPosition != null && movingObjectPosition.typeOfHit
                    == MovingObjectPosition.MovingObjectType.BLOCK) {
                return Collections.singleton(
                        SharedSuggestionProvider.TextCoordinates.from(movingObjectPosition.blockX,
                                movingObjectPosition.blockY, movingObjectPosition.blockZ));
            }
        }

        return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<String> getSelectedEntities(@NonNull final ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) sender;

            final Vec3 eyePosition =
                    Vec3.createVectorHelper(player.posX, player.posY + player.getEyeHeight(),
                            player.posZ);
            final Vec3 lookVector = player.getLookVec().normalize();
            Vec3 target = eyePosition.addVector(
                    lookVector.xCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.yCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.zCoord * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final MovingObjectPosition movingObjectPosition =
                    player.worldObj.rayTraceBlocks(eyePosition, target);

            if (!(movingObjectPosition == null || movingObjectPosition.typeOfHit
                    == MovingObjectPosition.MovingObjectType.MISS)) {
                target = movingObjectPosition.hitVec;
            }

            final net.minecraft.world.World world = player.worldObj;
            final AxisAlignedBB aabb =
                    player.boundingBox.addCoord(lookVector.xCoord, lookVector.yCoord,
                            lookVector.zCoord).expand(1.0D, 1.0D, 1.0D);
            //noinspection unchecked
            final List<net.minecraft.entity.Entity> entitiesInPath =
                    (List<net.minecraft.entity.Entity>) world.getEntitiesWithinAABBExcludingEntity(
                            player, aabb);
            Double closestDistance = null;
            net.minecraft.entity.Entity closestEntity = null;

            for (final net.minecraft.entity.Entity entity : entitiesInPath) {
                final AxisAlignedBB entityBoundingBox = entity.boundingBox;
                final MovingObjectPosition intercept =
                        entityBoundingBox.calculateIntercept(eyePosition, target);

                if (entityBoundingBox.isVecInside(eyePosition)) {
                    // player head inside entity bounding box
                    closestEntity = entity;
                    break;
                }

                if (intercept != null) {
                    final double distance = eyePosition.distanceTo(intercept.hitVec);

                    if (closestDistance == null || distance < closestDistance) {
                        closestDistance = distance;
                        closestEntity = entity;
                    }
                }
            }

            if (closestEntity != null) {
                return Collections.singleton(closestEntity.getUniqueID().toString());
            }
        }

        return Collections.emptySet();
    }

    @Override
    public Sender wrap(@NonNull final ICommandSender sender) {
        Objects.requireNonNull(sender, "sender");
        return new LegacyAbstractSender(this.getPlugin(), this, sender);
    }

    private static class LegacyAbstractSender extends AbstractSender<ICommandSender> {

        protected LegacyAbstractSender(final DirtCorePlugin plugin,
                final SenderFactory<?, ICommandSender> factory, final ICommandSender sender) {
            super(plugin, factory, sender);
        }

        @Override
        public void sendMessage(final Component message) {
            for (final Component line : splitNewlines(message)) {
                this.factory.sendMessage(this.sender, line);
            }
        }
    }
}
