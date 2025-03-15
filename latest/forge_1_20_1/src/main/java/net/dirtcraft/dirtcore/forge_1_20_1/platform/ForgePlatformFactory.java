/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.brigadier.ParseResults;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.dirtcraft.dirtcore.common.platform.sender.DummyConsoleSender;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ForgePlatformFactory implements PlatformFactory<Entity, ServerPlayer,
        net.minecraft.network.chat.Component, ItemStack, Level, ForgeBlock> {

    @NonNull
    private final DirtCoreForgePlugin plugin;
    private final ForgeBlockFactory blockFactory;
    private final ForgeEntityFactory entityFactory;
    private final ForgeItemStackFactory itemStackFactory;
    private final ForgePlayerFactory playerFactory;
    private final ForgeWorldFactory worldFactory;

    public ForgePlatformFactory(@NotNull final DirtCoreForgePlugin plugin) {
        this.plugin = plugin;
        this.blockFactory = new ForgeBlockFactory(plugin);
        this.entityFactory = new ForgeEntityFactory(plugin, this);
        this.itemStackFactory = new ForgeItemStackFactory(plugin);
        this.playerFactory = new ForgePlayerFactory(plugin, this, this.entityFactory);
        this.worldFactory = new ForgeWorldFactory(plugin, this);
    }

    @NonNull
    public static AABB transformAABB(
            final net.dirtcraft.dirtcore.common.model.minecraft.phys.@NonNull AABB aabb) {
        return new AABB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public static net.dirtcraft.dirtcore.common.model.minecraft.phys.@NonNull BlockPos transformBlockPos(
            final @NonNull BlockPos blockPos) {
        return net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos.of(blockPos.getX(),
                blockPos.getY(), blockPos.getZ());
    }

    @Override
    public void broadcast(@NonNull final Component message) {
        this.plugin.getBootstrap().getServer().ifPresent(server -> server.getPlayerList()
                .broadcastSystemMessage(this.transformComponent(message), false));
    }

    @Override
    public void performCommand(@NonNull final String command) {
        final Optional<MinecraftServer> serverOptional = this.plugin.getBootstrap().getServer();

        if (serverOptional.isPresent()) {
            final MinecraftServer server = serverOptional.get();
            final ParseResults<CommandSourceStack> results = server.getCommands().getDispatcher()
                    .parse(command, server.createCommandSourceStack());

            server.getCommands().performCommand(results, command);
            return;
        }

        this.plugin.getLogger()
                .warn("Command '{}' could not be executed. Server not found.", command);
    }

    @Override
    public net.dirtcraft.dirtcore.common.model.minecraft.@NonNull Entity wrapEntity(
            @NonNull final Entity entity) {
        return this.entityFactory.wrap(entity);
    }

    @Override
    public @NonNull List<net.dirtcraft.dirtcore.common.model.minecraft.Entity> wrapEntities(
            @NonNull final Collection<Entity> entities) {
        return entities.stream().map(this::wrapEntity).collect(ImmutableCollectors.toList());
    }

    @Override
    public @NonNull Entity transformEntity(
            final net.dirtcraft.dirtcore.common.model.minecraft.@NonNull Entity entity) {
        return this.entityFactory.transformEntity(entity);
    }

    @Override
    public boolean isValidEntityType(@NonNull final String type) {
        return EntityType.byString(type).isPresent();
    }

    @Override
    public @NonNull Collection<String> getEntityTypes() {
        //noinspection deprecation
        return BuiltInRegistries.ENTITY_TYPE.stream().map(EntityType::getKey)
                .map(ResourceLocation::toString).collect(ImmutableCollectors.toSet());
    }

    @Override
    public @NonNull Collection<String> getEntityTypesNoPlayer() {
        //noinspection deprecation
        return BuiltInRegistries.ENTITY_TYPE.stream().filter(type -> type != EntityType.PLAYER)
                .map(EntityType::getKey).map(ResourceLocation::toString)
                .collect(ImmutableCollectors.toSet());
    }

    @Override
    public boolean isPlayerOnline(@NonNull final UUID uuid) {
        return this.plugin.getBootstrap().getServer()
                .map(server -> server.getPlayerList().getPlayer(uuid) != null).orElse(false);
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final String username) {
        return this.plugin.getBootstrap().getServer().map(server -> {
            final ServerPlayer player = server.getPlayerList().getPlayerByName(username);
            return player == null ? null : this.playerFactory.wrap(player);
        });
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final UUID uniqueId) {
        return this.plugin.getBootstrap().getServer().map(server -> {
            final ServerPlayer player = server.getPlayerList().getPlayer(uniqueId);
            return player == null ? null : this.playerFactory.wrap(player);
        });
    }

    @Override
    public @NonNull Player wrapPlayer(@NonNull final ServerPlayer player) {
        return this.playerFactory.wrap(player);
    }

    @Override
    public @NonNull List<Player> wrapPlayers(@NonNull final Collection<ServerPlayer> players) {
        return players.stream().map(this::wrapPlayer).collect(ImmutableCollectors.toList());
    }

    @Override
    public @NotNull Collection<String> getPlayerNames() {
        return this.plugin.getBootstrap().getServer()
                .map(server -> ImmutableSet.copyOf(server.getPlayerNames()))
                .orElse(ImmutableSet.of());
    }

    @Override
    public @NonNull Collection<UUID> getPlayerUUIDs() {
        return this.plugin.getBootstrap().getServer().map(MinecraftServer::getPlayerList)
                .map(PlayerList::getPlayers).map(players -> {
                    final Set<UUID> set = new HashSet<>(players.size());

                    for (final ServerPlayer player : players) {
                        set.add(player.getGameProfile().getId());
                    }

                    return set;
                }).orElse(Collections.emptySet());
    }

    @Override
    public @NonNull Stream<Player> getOnlinePlayers() {
        return this.plugin.getBootstrap().getServer().map(MinecraftServer::getPlayerList)
                .map(PlayerList::getPlayers)
                .map(players -> players.stream().filter(Objects::nonNull)
                        .map(this.playerFactory::wrap)).orElseGet(Stream::empty);
    }

    @Override
    public @NonNull Stream<Sender> getOnlineSenders() {
        return Stream.concat(Stream.of(this.getConsoleSender()),
                this.plugin.getBootstrap().getServer().map(MinecraftServer::getPlayerList)
                        .map(PlayerList::getPlayers)
                        .map(players -> players.stream().filter(Objects::nonNull)
                                .map(player -> this.plugin.getSenderFactory()
                                        .wrap(player.createCommandSourceStack())))
                        .orElseGet(Stream::empty));
    }

    @Override
    public @NonNull Sender getConsoleSender() {
        return this.plugin.getBootstrap().getServer().map(server -> this.plugin.getSenderFactory()
                        .wrap(server.createCommandSourceStack()))
                .orElseGet(() -> new DummyConsoleSender(this.plugin) {
                    @Override
                    public void sendMessage(final Component message) {
                        ForgePlatformFactory.this.plugin.getLogger()
                                .info(PlainTextComponentSerializer.plainText().serialize(message));
                    }

                    @Override
                    public void sendMessage(final Iterable<Component> messages) {
                        for (final Component c : messages) {
                            this.sendMessage(c);
                        }
                    }
                });
    }

    @Override
    public @NonNull Optional<Block> getBlock(@NonNull final String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Collection<String> getBlockNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Block wrapBlock(@NonNull final ForgeBlock block) {
        return this.blockFactory.wrap(block);
    }

    @Override
    public @NonNull Stream<String> getModNames() {
        return ModList.get().getMods().stream().map(IModInfo::getModId);
    }

    @Override
    public @NonNull MutableComponent transformComponent(@NonNull final Component component) {
        try {
            final MutableComponent mutableComponent =
                    net.minecraft.network.chat.Component.Serializer.fromJson(
                            GSON_COMPONENT_SERIALIZER.serialize(this.extractURLs(component)));

            if (mutableComponent != null) {
                return mutableComponent;
            }
        } catch (final Exception e) {
            this.plugin.getLogger()
                    .severe("There was an exception during the transformation of components.", e);
        }

        return net.minecraft.network.chat.Component.empty();
    }

    @Override
    public @NonNull Component transformComponent(
            final net.minecraft.network.chat.@NonNull Component mutableComponent) {
        try {
            return GSON_COMPONENT_SERIALIZER.deserializeOr(
                    net.minecraft.network.chat.Component.Serializer.toJson(mutableComponent),
                    Component.empty());
        } catch (final Exception e) {
            this.plugin.getLogger()
                    .severe("There was an exception during the transformation of components.", e);
        }

        return Component.empty();
    }

    @Override
    public net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack wrapItemStack(
            @NonNull final ItemStack itemStack) {
        return this.itemStackFactory.wrap(itemStack);
    }

    @Override
    public @NonNull ItemStack transformItemStack(
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack itemStack) {
        return this.itemStackFactory.transform(itemStack);
    }

    @Override
    public net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack transformItemStack(
            @NonNull final SimpleItemStack simpleItemStack) {
        return this.itemStackFactory.transform(simpleItemStack);
    }

    @Override
    public @NonNull Optional<net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack> createItemStack(
            @NonNull final String identifier, final int count,
            @Nullable final String persistentData) {
        return this.itemStackFactory.createItemStack(identifier, count, persistentData)
                .map(this::wrapItemStack);
    }

    @Override
    public @NonNull World wrapWorld(@NonNull final Level world) {
        return this.worldFactory.wrap(world);
    }

    @Override
    public @NonNull Level transformWorld(@NonNull final World world) {
        return this.worldFactory.transformWorld(world);
    }

    @Override
    public @NonNull Collection<World> getWorlds() {
        return this.plugin.getBootstrap().getServer().map(server -> {
            final ImmutableList.Builder<World> worldsBuilder = ImmutableList.builder();
            server.getAllLevels().forEach(level -> worldsBuilder.add(this.wrapWorld(level)));
            return worldsBuilder.build();
        }).orElse(ImmutableList.of());
    }

    @Override
    public @NonNull String componentToUnformattedString(@NonNull final Component component) {
        return this.stripFormatting(this.transformComponent(component).getString());
    }

    @NonNull
    private Component extractURLs(@NonNull final Component component) {
        return component.replaceText(URL_TEXT_REPLACEMENT_CONFIG);
    }
}
