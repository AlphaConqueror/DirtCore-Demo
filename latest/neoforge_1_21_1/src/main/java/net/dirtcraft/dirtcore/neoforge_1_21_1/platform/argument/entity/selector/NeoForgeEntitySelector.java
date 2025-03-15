/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.selector;

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
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.NeoForgePlatformFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeEntitySelector extends AbstractEntitySelector {

    private static final EntityTypeTest<Entity, ?> ANY_TYPE = new EntityTypeTest<Entity, Entity>() {
        @Override
        public Entity tryCast(@NonNull final Entity entity) {
            return entity;
        }

        @Override
        public @NonNull Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };
    protected final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate;
    protected final Function<Vec3, Vec3> position;
    protected final MinMaxBounds.Doubles range;
    @Nullable
    protected final AABB aabb;
    private final DirtCoreNeoForgePlugin plugin;
    private final BiConsumer<Vec3, List<?
            extends net.dirtcraft.dirtcore.common.model.minecraft.Entity>>
            order;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUniqueId;
    private final EntityTypeTest<Entity, ?> type;

    public NeoForgeEntitySelector(final DirtCoreNeoForgePlugin plugin, final int maxResults,
            final boolean includesEntities, final boolean worldLimited,
            final Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> predicate,
            final MinMaxBounds.Doubles range, final Function<Vec3, Vec3> positions,
            @Nullable final AABB aabb,
            final BiConsumer<Vec3, List<?
                    extends net.dirtcraft.dirtcore.common.model.minecraft.Entity>> order,
            final boolean currentEntity, @Nullable final String playerName,
            @Nullable final UUID entityUniqueId, @Nullable final EntityType<?> type,
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
        final Optional<World> worldOptional = sender.getWorld();

        if (worldOptional.isEmpty()) {
            return Collections.emptyList();
        }

        final NeoForgePlatformFactory platformFactory = this.plugin.getPlatformFactory();
        //noinspection resource
        final Level level = platformFactory.transformWorld(worldOptional.get());
        return this.findEntitiesRaw(sender).stream()
                .filter(entity -> platformFactory.transformEntity(entity).getType()
                        .isEnabled(level.enabledFeatures())).collect(ImmutableCollectors.toList());
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

        if (serverOptional.isEmpty()) {
            return Collections.emptyList();
        }

        final MinecraftServer server = serverOptional.get();
        final NeoForgePlatformFactory platformFactory = this.plugin.getPlatformFactory();

        if (this.playerName != null) {
            final ServerPlayer player = server.getPlayerList().getPlayerByName(this.playerName);
            return player == null ? Collections.emptyList()
                    : Collections.singletonList(platformFactory.wrapPlayer(player));
        }

        if (this.entityUniqueId != null) {
            final ServerPlayer player = server.getPlayerList().getPlayer(this.entityUniqueId);
            return player == null ? Collections.emptyList()
                    : Collections.singletonList(platformFactory.wrapPlayer(player));
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

                if (entity instanceof ServerPlayer) {
                    final Player player = platformFactory.wrapPlayer((ServerPlayer) entity);

                    if (predicate.test(player)) {
                        return Collections.singletonList(player);
                    }
                }
            }

            return Collections.emptyList();
        }

        final int i = this.getResultLimit();
        final List<Player> list;

        if (this.isWorldLimited()) {
            list = sender.getWorld().map(world -> {
                //noinspection resource
                final Level level = this.plugin.getPlatformFactory().transformWorld(world);

                if (level instanceof ServerLevel) {
                    return platformFactory.wrapPlayers(
                            ((ServerLevel) level).getPlayers(this.toPlatformPredicate(predicate),
                                    i));
                }

                return Collections.<Player>emptyList();
            }).orElse(Collections.emptyList());
        } else {
            list = new ArrayList<>();

            for (final ServerPlayer serverPlayer : server.getPlayerList().getPlayers()) {
                final Player player = platformFactory.wrapPlayer(serverPlayer);

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

        if (serverOptional.isEmpty()) {
            return Collections.emptyList();
        }

        final MinecraftServer server = serverOptional.get();

        if (!this.includesEntities) {
            return this.findPlayersRaw(sender);
        }

        final NeoForgePlatformFactory platformFactory = this.plugin.getPlatformFactory();

        if (this.playerName != null) {
            final ServerPlayer player = server.getPlayerList().getPlayerByName(this.playerName);
            return player == null ? Collections.emptyList()
                    : Collections.singletonList(platformFactory.wrapPlayer(player));
        }

        if (this.entityUniqueId != null) {
            for (final ServerLevel serverLevel : server.getAllLevels()) {
                final Entity entity = serverLevel.getEntity(this.entityUniqueId);

                if (entity != null) {
                    return Collections.singletonList(platformFactory.wrapEntity(entity));
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
                final Level level = this.plugin.getPlatformFactory().transformWorld(world);

                if (level instanceof ServerLevel) {
                    this.addEntities(list, (ServerLevel) level, vec3, platformPredicate);
                }
            });
        } else {
            for (final ServerLevel serverLevel : server.getAllLevels()) {
                this.addEntities(list, serverLevel, vec3, platformPredicate);
            }
        }

        return this.sortAndLimit(vec3, platformFactory.wrapEntities(list));
    }

    /**
     * Gets all entities matching this selector, and adds them to the passed list.
     */
    private void addEntities(final List<Entity> result, final ServerLevel level, final Vec3 pos,
            final Predicate<Entity> predicate) {
        final int i = this.getResultLimit();

        if (result.size() < i) {
            if (this.aabb != null) {
                final net.minecraft.world.phys.AABB mcAABB =
                        NeoForgePlatformFactory.transformAABB(this.aabb.move(pos));
                level.getEntities(this.type, mcAABB, predicate, result, i);
            } else {
                level.getEntities(this.type, predicate, result, i);
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
