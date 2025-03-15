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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.entity.selector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelector;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.ForgePlatformFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEntitySelector extends AbstractEntitySelector {

    private static final Class<? extends Entity> ANY_TYPE = Entity.class;

    protected final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate;
    protected final Function<Vec3, Vec3> position;
    protected final MinMaxBounds.Doubles range;
    @Nullable
    protected final AABB aabb;
    private final DirtCoreForgePlugin plugin;
    private final BiConsumer<Vec3, List<?
            extends net.dirtcraft.dirtcore.common.model.minecraft.Entity>>
            order;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUniqueId;
    private final Class<? extends Entity> type;

    public ForgeEntitySelector(final DirtCoreForgePlugin plugin, final int maxResults,
            final boolean includesEntities, final boolean worldLimited,
            final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate,
            final MinMaxBounds.Doubles range, final Function<Vec3, Vec3> positions,
            @Nullable final AABB aabb,
            final BiConsumer<Vec3, List<?
                    extends net.dirtcraft.dirtcore.common.model.minecraft.Entity>> order,
            final boolean currentEntity, @Nullable final String playerName,
            @Nullable final UUID entityUniqueId, @Nullable final Class<? extends Entity> type,
            final boolean usesSelector) {
        super(maxResults, includesEntities, worldLimited, currentEntity, usesSelector);
        this.plugin = plugin;
        this.predicate = predicate;
        this.range = range;
        this.position = positions;
        this.aabb = aabb;
        this.order = order;
        this.playerName = playerName;
        this.entityUniqueId = entityUniqueId;
        this.type = type == null ? ANY_TYPE : type;
    }

    @Override
    public net.dirtcraft.dirtcore.common.model.minecraft.@NonNull Entity findSingleEntity(
            @NonNull final Sender sender) throws CommandSyntaxException {
        this.checkPermissions(sender);

        final List<? extends net.dirtcraft.dirtcore.common.model.minecraft.Entity> list =
                this.findEntities(sender);

        if (list.isEmpty()) {
            throw AbstractEntityArgument.NO_ENTITIES_FOUND.create();
        }

        if (list.size() > 1) {
            throw AbstractEntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        }

        return list.get(0);
    }

    @Override
    public @NonNull List<? extends net.dirtcraft.dirtcore.common.model.minecraft.Entity> findEntities(
            @NonNull final Sender sender) throws CommandSyntaxException {
        return this.findEntitiesRaw(sender);
    }

    @Override
    public @NonNull Player findSinglePlayer(
            @NonNull final Sender sender) throws CommandSyntaxException {
        this.checkPermissions(sender);

        final List<Player> list = this.findPlayers(sender);

        if (list.size() != 1) {
            throw AbstractEntityArgument.NO_PLAYERS_FOUND.create();
        }

        return list.get(0);
    }

    @Override
    public @NonNull List<Player> findPlayers(
            @NonNull final Sender sender) throws CommandSyntaxException {
        return this.findPlayersRaw(sender);
    }

    /**
     * Returns a modified version of the predicate on this selector that also checks the AABB and
     * distance.
     */
    protected Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> getPredicate(
            final Vec3 pos) {
        Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate = this.predicate;

        if (this.aabb != null) {
            final AABB aabb = this.aabb.move(pos);
            predicate = predicate.and(entity -> aabb.intersects(entity.getBoundingBox()));
        }

        if (!this.range.isAny()) {
            predicate = predicate.and(entity -> this.range.matchesSqr(entity.distanceToSqr(pos)));
        }

        return predicate;
    }

    @NonNull
    private List<Player> findPlayersRaw(
            @NonNull final Sender sender) throws CommandSyntaxException {
        this.checkPermissions(sender);

        final Optional<MinecraftServer> serverOptional = this.plugin.getBootstrap().getServer();

        if (!serverOptional.isPresent()) {
            return Collections.emptyList();
        }

        final MinecraftServer server = serverOptional.get();
        final ForgePlatformFactory platformFactory = this.plugin.getPlatformFactory();

        if (this.playerName != null) {
            for (final EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player.getName().equalsIgnoreCase(this.playerName)) {
                    return Collections.singletonList(platformFactory.wrapPlayer(player));
                }
            }

            return Collections.emptyList();
        }

        if (this.entityUniqueId != null) {
            for (final EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player.getUniqueID().equals(this.entityUniqueId)) {
                    return Collections.singletonList(platformFactory.wrapPlayer(player));
                }
            }

            return Collections.emptyList();
        }

        final Vec3 vec3 = this.position.apply(sender.getPosition());
        final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate =
                this.getPredicate(vec3);

        if (this.currentEntity) {
            final Optional<net.dirtcraft.dirtcore.common.model.minecraft.Entity> entityOptional =
                    sender.getEntity();

            if (entityOptional.isPresent()) {
                final Entity entity =
                        this.plugin.getPlatformFactory().transformEntity(entityOptional.get());

                if (entity instanceof EntityPlayerMP) {
                    final Player player = platformFactory.wrapPlayer((EntityPlayerMP) entity);

                    if (predicate.test(player)) {
                        return Collections.singletonList(player);
                    }
                }
            }

            return Collections.emptyList();
        }

        final int i = this.getResultLimit();
        final List<Player> list = new ArrayList<>();

        if (this.isWorldLimited()) {
            final Optional<World> worldOptional = sender.getWorld();

            if (worldOptional.isPresent()) {
                final net.minecraft.world.World world =
                        this.plugin.getPlatformFactory().transformWorld(worldOptional.get());
                final Predicate<Entity> platformPredicate = this.toPlatformPredicate(predicate);

                for (final EntityPlayer p : world.playerEntities) {
                    if (p instanceof EntityPlayerMP && platformPredicate.test(p)) {
                        list.add(platformFactory.wrapPlayer((EntityPlayerMP) p));

                        if (list.size() >= i) {
                            return list;
                        }
                    }
                }
            }
        } else {
            for (final EntityPlayerMP p : server.getPlayerList().getPlayers()) {
                final Player player = platformFactory.wrapPlayer(p);

                if (predicate.test(player)) {
                    list.add(player);

                    if (list.size() >= i) {
                        return list;
                    }
                }
            }
        }

        return this.sortAndLimit(vec3, list);
    }

    private int getResultLimit() {
        return this.order == ORDER_ARBITRARY ? this.maxResults : INFINITE;
    }

    private List<? extends net.dirtcraft.dirtcore.common.model.minecraft.Entity> findEntitiesRaw(
            @NonNull final Sender sender) throws CommandSyntaxException {
        this.checkPermissions(sender);

        final Optional<MinecraftServer> serverOptional = this.plugin.getBootstrap().getServer();

        if (!serverOptional.isPresent()) {
            return Collections.emptyList();
        }

        final MinecraftServer server = serverOptional.get();

        if (!this.includesEntities) {
            return this.findPlayersRaw(sender);
        }

        final ForgePlatformFactory platformFactory = this.plugin.getPlatformFactory();

        if (this.playerName != null) {
            for (final EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player.getName().equalsIgnoreCase(this.playerName)) {
                    return Collections.singletonList(platformFactory.wrapPlayer(player));
                }
            }

            return Collections.emptyList();
        }

        if (this.entityUniqueId != null) {
            for (final WorldServer world : server.worlds) {
                for (final Entity entity : world.loadedEntityList) {
                    if (entity.getUniqueID().equals(this.entityUniqueId)) {
                        return Collections.singletonList(platformFactory.wrapEntity(entity));
                    }
                }
            }

            return Collections.emptyList();
        }

        final Vec3 vec3 = this.position.apply(sender.getPosition());
        final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate =
                this.getPredicate(vec3);

        if (this.currentEntity) {
            final Optional<net.dirtcraft.dirtcore.common.model.minecraft.Entity> entityOptional =
                    sender.getEntity();

            if (entityOptional.isPresent()) {
                final net.dirtcraft.dirtcore.common.model.minecraft.Entity entity =
                        entityOptional.get();

                if (predicate.test(entity)) {
                    return Collections.singletonList(entity);
                }
            }

            return Collections.emptyList();
        }

        final List<Entity> list = new ArrayList<>();
        final Predicate<Entity> platformPredicate = this.toPlatformPredicate(predicate);

        if (this.isWorldLimited()) {
            sender.getWorld().ifPresent(world -> {
                final net.minecraft.world.World mcWorld =
                        this.plugin.getPlatformFactory().transformWorld(world);
                this.addEntities(list, mcWorld, vec3, platformPredicate);
            });
        } else {
            for (final net.minecraft.world.World world : server.worlds) {
                this.addEntities(list, world, vec3, platformPredicate);
            }
        }

        return this.sortAndLimit(vec3, platformFactory.wrapEntities(list));
    }

    /**
     * Gets all entities matching this selector, and adds them to the passed list.
     */
    private void addEntities(final List<Entity> result, final net.minecraft.world.World world,
            final Vec3 pos, final Predicate<Entity> predicate) {
        final int limit = this.getResultLimit();

        if (result.size() < limit) {
            if (this.aabb != null) {
                final AxisAlignedBB mcAABB =
                        ForgePlatformFactory.transformAABB(this.aabb.move(pos));
                for (final Entity entity : world.getEntitiesWithinAABB(this.type,
                        mcAABB.offset(pos.x, pos.y, pos.z))) {
                    if (predicate.test(entity)) {
                        result.add(entity);

                        if (result.size() >= limit) {
                            break;
                        }
                    }
                }
            } else {
                for (final Entity entity : world.loadedEntityList) {
                    if (this.type.isAssignableFrom(entity.getClass()) && predicate.test(entity)) {
                        result.add(entity);

                        if (result.size() >= limit) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private <T extends net.dirtcraft.dirtcore.common.model.minecraft.Entity> List<T> sortAndLimit(
            final Vec3 pos, final List<T> entities) {
        if (entities.size() > 1) {
            this.order.accept(pos, entities);
        }

        return entities.subList(0, Math.min(this.maxResults, entities.size()));
    }

    @NonNull
    private Predicate<Entity> toPlatformPredicate(
            @NonNull final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate) {
        return entity -> predicate.test(this.plugin.getPlatformFactory().wrapEntity(entity));
    }
}
