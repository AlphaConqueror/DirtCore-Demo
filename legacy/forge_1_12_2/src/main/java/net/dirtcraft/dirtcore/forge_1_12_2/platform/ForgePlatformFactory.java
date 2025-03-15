/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.platform.sender.DummyConsoleSender;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge.legacy.platform.ForgeLegacyPlatformFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ForgePlatformFactory implements ForgeLegacyPlatformFactory<Entity, EntityPlayerMP,
        ITextComponent, ItemStack, net.minecraft.world.World, ForgeBlock> {

    private static final ResourceLocation PLAYER = new ResourceLocation("player");

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
    public static AxisAlignedBB transformAABB(
            final net.dirtcraft.dirtcore.common.model.minecraft.phys.@NonNull AABB aabb) {
        return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
    }

    public static net.dirtcraft.dirtcore.common.model.minecraft.phys.@NonNull BlockPos transformBlockPos(
            @NonNull final BlockPos blockPos) {
        return net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos.of(blockPos.getX(),
                blockPos.getY(), blockPos.getZ());
    }

    @Override
    public void broadcast(@NonNull final Component message) {
        this.getOnlineSenders().forEach(sender -> sender.sendMessage(message));
    }

    @Override
    public void performCommand(@NonNull final String command) {
        final Optional<MinecraftServer> serverOptional = this.plugin.getBootstrap().getServer();

        if (serverOptional.isPresent()) {
            final MinecraftServer server = serverOptional.get();
            server.getCommandManager().executeCommand(server, command);
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
        return ForgeRegistries.ENTITIES.containsKey(ForgeUtils.tryParse(type));
    }

    @Override
    public @NonNull Collection<String> getEntityTypes() {
        return ForgeRegistries.ENTITIES.getKeys().stream().map(ResourceLocation::toString)
                .collect(ImmutableCollectors.toSet());
    }

    @Override
    public @NonNull Collection<String> getEntityTypesNoPlayer() {
        return ForgeRegistries.ENTITIES.getKeys().stream()
                .filter(resourceLocation -> !resourceLocation.equals(PLAYER))
                .map(ResourceLocation::toString).collect(ImmutableCollectors.toSet());
    }

    @Override
    public boolean isPlayerOnline(@NonNull final UUID uniqueId) {
        return this.plugin.getBootstrap().getServer()
                .map(server -> server.getPlayerList().getPlayers().stream().filter(Objects::nonNull)
                        .anyMatch(entityPlayerMP -> entityPlayerMP.getUniqueID().equals(uniqueId)))
                .orElse(false);
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final String username) {
        return this.plugin.getBootstrap().getServer().map(server -> {
            for (final EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player != null && player.getName().equalsIgnoreCase(username)) {
                    return this.playerFactory.wrap(player);
                }
            }

            return null;
        });
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final UUID uniqueId) {
        return this.plugin.getBootstrap().getServer().map(server -> {
            for (final EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player != null && player.getUniqueID().equals(uniqueId)) {
                    return this.playerFactory.wrap(player);
                }
            }

            return null;
        });
    }

    @Override
    public @NonNull Player wrapPlayer(@NonNull final EntityPlayerMP player) {
        return this.playerFactory.wrap(player);
    }

    @Override
    public @NonNull List<Player> wrapPlayers(@NonNull final Collection<EntityPlayerMP> players) {
        return players.stream().map(this::wrapPlayer).collect(ImmutableCollectors.toList());
    }

    @Override
    public @NotNull Collection<String> getPlayerNames() {
        return this.plugin.getBootstrap().getServer()
                .map(server -> ImmutableSet.copyOf(server.getPlayerList().getOnlinePlayerNames()))
                .orElse(ImmutableSet.of());
    }

    @Override
    public @NonNull Collection<UUID> getPlayerUUIDs() {
        return this.plugin.getBootstrap().getServer()
                .map(server -> server.getPlayerList().getPlayers().stream().filter(Objects::nonNull)
                        .map(Entity::getUniqueID).collect(ImmutableCollectors.toSet()))
                .orElse(ImmutableSet.of());
    }

    @Override
    public @NonNull Stream<Player> getOnlinePlayers() {
        return this.plugin.getBootstrap().getServer()
                .map(server -> server.getPlayerList().getPlayers().stream().filter(Objects::nonNull)
                        .map(this.playerFactory::wrap)).orElseGet(Stream::empty);
    }

    @Override
    public @NonNull Stream<Sender> getOnlineSenders() {
        return Stream.concat(Stream.of(this.getConsoleSender()),
                this.plugin.getBootstrap().getServer()
                        .map(server -> server.getPlayerList().getPlayers().stream()
                                .filter(Objects::nonNull)
                                .map(player -> this.plugin.getSenderFactory().wrap(player)))
                        .orElseGet(Stream::empty));
    }

    @Override
    public @NonNull Sender getConsoleSender() {
        return this.plugin.getBootstrap().getServer()
                .map(server -> this.plugin.getSenderFactory().wrap(server))
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
        return Loader.instance().getModList().stream().map(ModContainer::getModId);
    }

    @Override
    public @NonNull ITextComponent transformComponent(@NonNull final Component component) {
        try {
            final ITextComponent textComponent = ITextComponent.Serializer.jsonToComponent(
                    GSON_COMPONENT_SERIALIZER_LEGACY.serialize(this.extractURLs(component)));

            if (textComponent != null) {
                return textComponent;
            }
        } catch (final Exception e) {
            this.plugin.getLogger()
                    .severe("There was an exception during the transformation of components.", e);
        }

        return new TextComponentString("");
    }

    @Override
    public @NonNull Component transformComponent(@NonNull final ITextComponent textComponent) {
        try {
            return GSON_COMPONENT_SERIALIZER_LEGACY.deserializeOr(
                    ITextComponent.Serializer.componentToJson(textComponent), Component.empty());
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
    public @NonNull World wrapWorld(final net.minecraft.world.@NonNull World world) {
        return this.worldFactory.wrap(world);
    }

    @Override
    public net.minecraft.world.@NonNull World transformWorld(@NonNull final World world) {
        return this.worldFactory.transformWorld(world);
    }

    @Override
    public @NonNull Collection<World> getWorlds() {
        return this.plugin.getBootstrap().getServer().map(server -> {
            final ImmutableList.Builder<World> worldsBuilder = ImmutableList.builder();
            Arrays.stream(server.worlds).forEach(level -> worldsBuilder.add(this.wrapWorld(level)));
            return worldsBuilder.build();
        }).orElse(ImmutableList.of());
    }

    @Override
    public @NonNull String componentToUnformattedString(@NonNull final Component component) {
        return this.stripFormatting(this.transformComponent(component).getUnformattedText());
    }

    @Override
    @NonNull
    public String componentToJson(@NonNull final Component component) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(component);
    }

    @Override
    @NonNull
    public Component jsonToComponent(@NonNull final String json) {
        return LEGACY_COMPONENT_SERIALIZER.deserialize(json);
    }

    @NonNull
    protected ForgeEntityFactory getEntityFactory() {
        return this.entityFactory;
    }

    @NonNull
    private Component extractURLs(@NonNull final Component component) {
        return component.replaceText(URL_TEXT_REPLACEMENT_CONFIG);
    }
}
