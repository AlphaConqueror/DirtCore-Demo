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

package net.dirtcraft.dirtcore.forge_1_12_2;

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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
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
            return sender.getName();
        }

        return Sender.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(@NonNull final ICommandSender sender,
            @NonNull final Component message) {
        sender.sendMessage(this.getPlugin().getPlatformFactory().transformComponent(message));
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
            final Vec3d startVec =
                    new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            final Vec3d lookVector = player.getLookVec().normalize();
            final Vec3d endVec =
                    startVec.add(lookVector.x * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                            lookVector.y * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                            lookVector.z * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final RayTraceResult rayTraceResult = player.world.rayTraceBlocks(startVec, endVec);

            if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
                final BlockPos blockPos = rayTraceResult.getBlockPos();
                return Collections.singleton(
                        SharedSuggestionProvider.TextCoordinates.from(blockPos.getX(),
                                blockPos.getY(), blockPos.getZ()));
            }
        }

        return Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<String> getSelectedEntities(@NonNull final ICommandSender sender) {
        if (sender instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) sender;

            final Vec3d eyePosition =
                    new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
            final Vec3d lookVector = player.getLookVec().normalize();
            Vec3d target = eyePosition.add(
                    lookVector.x * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.y * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE,
                    lookVector.z * SharedSuggestionProvider.DEFAULT_RAY_TRACE_DISTANCE);
            final RayTraceResult rayTraceResult = player.world.rayTraceBlocks(eyePosition, target);

            if (!(rayTraceResult == null || rayTraceResult.typeOfHit == RayTraceResult.Type.MISS)) {
                target = rayTraceResult.hitVec;
            }

            final net.minecraft.world.World world = player.world;
            final AxisAlignedBB aabb =
                    player.getEntityBoundingBox().expand(lookVector.x, lookVector.y, lookVector.z)
                            .grow(1.0D, 1.0D, 1.0D);
            final List<net.minecraft.entity.Entity> entitiesInPath =
                    world.getEntitiesWithinAABBExcludingEntity(player, aabb);
            Double closestDistance = null;
            net.minecraft.entity.Entity closestEntity = null;

            for (final net.minecraft.entity.Entity entity : entitiesInPath) {
                final AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox();
                final RayTraceResult intercept =
                        entityBoundingBox.calculateIntercept(eyePosition, target);

                if (entityBoundingBox.contains(eyePosition)) {
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
