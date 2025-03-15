/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform;

import java.util.List;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.platform.minecraft.player.PlayerFactory;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.container.NeoForgeContainerAdapter;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class NeoForgePlayerFactory extends PlayerFactory<DirtCoreNeoForgePlugin, Entity,
        ServerPlayer> {

    private final NeoForgePlatformFactory platformFactory;

    public NeoForgePlayerFactory(final DirtCoreNeoForgePlugin plugin,
            final NeoForgePlatformFactory platformFactory,
            final NeoForgeEntityFactory entityFactory) {
        super(plugin, entityFactory);
        this.platformFactory = platformFactory;
    }

    @Override
    public @NonNull Component getDisplayName(@NonNull final ServerPlayer player) {
        final net.minecraft.network.chat.Component displayName = player.getDisplayName();
        return this.platformFactory.transformComponent(
                displayName == null ? player.getName() : displayName, player.registryAccess());
    }

    @Override
    protected boolean hasDisconnected(@NonNull final ServerPlayer player) {
        return player.hasDisconnected();
    }

    @Override
    protected net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack getItemInHand(
            @NonNull final ServerPlayer player) {
        return this.platformFactory.wrapItemStack(player.getMainHandItem());
    }

    @Override
    protected @NonNull List<net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack> getItems(
            @NonNull final ServerPlayer player) {
        return player.getInventory().items.stream().map(this.platformFactory::wrapItemStack)
                .collect(ImmutableCollectors.toList());
    }

    @Override
    protected boolean kick(@NonNull final ServerPlayer player, @NonNull final Component reason) {
        player.connection.disconnect(this.platformFactory.transformComponent(reason));
        return true;
    }

    @Override
    protected void sendMessage(@NonNull final ServerPlayer player,
            @NonNull final Component message) {
        player.sendSystemMessage(this.platformFactory.transformComponent(message));
    }

    @Override
    protected void sendMessage(@NonNull final ServerPlayer player,
            @NonNull final Iterable<Component> messages) {
        for (final Component c : messages) {
            player.sendSystemMessage(this.platformFactory.transformComponent(c));
        }
    }

    @Override
    protected void performCommand(@NonNull final ServerPlayer player,
            @NonNull final String command) {
        this.getPlugin().getBootstrap().getServer().ifPresent(server -> server.getCommands()
                .performPrefixedCommand(player.createCommandSourceStack(), command));
    }

    @Override
    protected int getFreeInventorySpace(@NonNull final ServerPlayer player) {
        final List<ItemStack> items = player.getInventory().items;
        int freeSpace = 0;

        for (final ItemStack item : items) {
            if (item.isEmpty()) {
                freeSpace++;
            }
        }

        return freeSpace;
    }

    @Override
    protected boolean isCreative(@NonNull final ServerPlayer player) {
        return player.isCreative();
    }

    @Override
    protected void openContainer(@NonNull final ServerPlayer player,
            @NonNull final Container container) {
        final MenuProvider menuProvider = new MenuProvider() {
            @Override
            public AbstractContainerMenu createMenu(final int pContainerId,
                    @NotNull final Inventory pPlayerInventory, @NotNull final Player pPlayer) {
                final NeoForgeContainerAdapter containerAdapter =
                        new NeoForgeContainerAdapter(NeoForgePlayerFactory.this.getPlugin(),
                                container, pContainerId, pPlayerInventory);

                containerAdapter.init();
                return containerAdapter;
            }

            @Override
            public @NotNull net.minecraft.network.chat.Component getDisplayName() {
                return NeoForgePlayerFactory.this.platformFactory.transformComponent(
                        container.getTitle());
            }
        };

        player.openMenu(menuProvider);
    }

    @Override
    protected void playSound(@NonNull final ServerPlayer player, @NonNull final Sound sound) {
        final SoundContainer soundContainer = this.parseSound(sound);
        final Holder<SoundEvent> holder = Holder.direct(
                SoundEvent.createVariableRangeEvent(soundContainer.getResourceLocation()));
        final ClientboundSoundPacket soundPacket =
                new ClientboundSoundPacket(holder, SoundSource.MASTER, player.getX(), player.getY(),
                        player.getZ(), soundContainer.getVolume(), soundContainer.getPitch(),
                        player.level().getRandom().nextLong());
        player.connection.send(soundPacket);
    }

    @Override
    protected void addItem(@NonNull final ServerPlayer player,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack itemStack) {
        final ItemStack stack = this.platformFactory.transformItemStack(itemStack);
        final boolean result =
                player.isAlive() && !player.hasDisconnected() && player.addItem(stack);

        // drop the remaining item stack, if it could not be fully added
        if (!(result || stack.isEmpty())) {
            player.drop(stack, false);
        }
    }

    @Override
    protected boolean teleport(@NonNull final ServerPlayer player, @NonNull final World world,
            final double x, final double y, final double z, final float yRot, final float xRot) {
        final Level level = this.platformFactory.transformWorld(world);

        if (level instanceof final ServerLevel serverLevel) {
            final EntityTeleportEvent.TeleportCommand event =
                    EventHooks.onEntityTeleportCommand(player, x, y, z);

            if (!event.isCanceled()) {
                player.teleportTo(serverLevel, x, y, z, yRot, xRot);

                if (!player.isFallFlying()) {
                    player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0, 1.0));
                    player.setOnGround(true);
                }

                return true;
            }
        }

        return false;
    }

    @NonNull
    private SoundContainer parseSound(@NonNull final Sound sound) {
        return switch (sound) {
            case CRATE_CLOSE -> SoundContainer.of("block.ender_chest.close");
            case CRATE_OPEN -> SoundContainer.of("block.ender_chest.open");
            case CRATE_REWARD -> SoundContainer.of("minecraft:entity.player.levelup");
            case CRATE_SPIN -> SoundContainer.of("minecraft:entity.ender_eye.death");
            case GUI_FAILURE -> SoundContainer.of("minecraft:entity.villager.no");
            case GUI_SUCCESS -> SoundContainer.of("minecraft:entity.experience_orb.pickup", 1F, 2F);
        };
    }

    private static class SoundContainer {

        @NonNull
        private final ResourceLocation resourceLocation;
        private final float volume;
        private final float pitch;

        private SoundContainer(@NonNull final String path, final float volume, final float pitch) {
            this.resourceLocation = ResourceLocation.parse(path);
            this.volume = volume;
            this.pitch = pitch;
        }

        private static SoundContainer of(@NonNull final String path) {
            return new SoundContainer(path, 1F, 1F);
        }

        private static SoundContainer of(@NonNull final String path, final float volume,
                final float pitch) {
            return new SoundContainer(path, volume, pitch);
        }

        @NonNull
        public ResourceLocation getResourceLocation() {
            return this.resourceLocation;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }
    }
}
