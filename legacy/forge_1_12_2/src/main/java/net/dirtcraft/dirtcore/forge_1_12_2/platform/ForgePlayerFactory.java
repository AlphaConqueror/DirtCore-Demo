/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform;

import java.util.List;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.platform.minecraft.player.PlayerFactory;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.container.ForgeContainerAdapter;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ITeleporter;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgePlayerFactory extends PlayerFactory<DirtCoreForgePlugin, Entity, EntityPlayerMP> {

    private final ForgePlatformFactory platformFactory;

    public ForgePlayerFactory(final DirtCoreForgePlugin plugin,
            final ForgePlatformFactory platformFactory, final ForgeEntityFactory entityFactory) {
        super(plugin, entityFactory);
        this.platformFactory = platformFactory;
    }

    @Override
    public @NonNull Component getDisplayName(@NonNull final EntityPlayerMP player) {
        return this.platformFactory.transformComponent(player.getDisplayName());
    }

    @Override
    protected boolean hasDisconnected(@NonNull final EntityPlayerMP player) {
        final NetHandlerPlayServer netHandlerPlayServer = player.connection;

        if (netHandlerPlayServer == null) {
            return true;
        }

        final NetworkManager networkManager = netHandlerPlayServer.netManager;

        if (networkManager == null) {
            return true;
        }

        return !networkManager.isChannelOpen();
    }

    @Override
    protected net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack getItemInHand(
            @NonNull final EntityPlayerMP player) {
        final ItemStack itemStack = player.getHeldItemMainhand();
        return this.platformFactory.wrapItemStack(itemStack);
    }

    @Override
    protected @NonNull List<net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack> getItems(
            @NonNull final EntityPlayerMP player) {
        return player.inventory.mainInventory.stream().map(this.platformFactory::wrapItemStack)
                .collect(ImmutableCollectors.toList());
    }

    @Override
    protected boolean kick(@NonNull final EntityPlayerMP player, @NonNull final Component reason) {
        player.connection.disconnect(this.platformFactory.transformComponent(reason));
        return true;
    }

    @Override
    protected void sendMessage(@NonNull final EntityPlayerMP player,
            @NonNull final Component message) {
        player.sendMessage(this.platformFactory.transformComponent(message));
    }

    @Override
    protected void sendMessage(@NonNull final EntityPlayerMP player,
            @NonNull final Iterable<Component> messages) {
        for (final Component c : messages) {
            player.sendMessage(this.platformFactory.transformComponent(c));
        }
    }

    @Override
    protected void performCommand(@NonNull final EntityPlayerMP player,
            @NonNull final String command) {
        this.getPlugin().getBootstrap().getServer()
                .ifPresent(server -> server.getCommandManager().executeCommand(player, command));
    }

    @Override
    protected int getFreeInventorySpace(@NonNull final EntityPlayerMP player) {
        int freeSpace = 0;

        for (final ItemStack item : player.inventory.mainInventory) {
            if (item == null) {
                freeSpace++;
            }
        }

        return freeSpace;
    }

    @Override
    protected boolean isCreative(@NonNull final EntityPlayerMP player) {
        return player.isCreative();
    }

    @Override
    protected void openContainer(@NonNull final EntityPlayerMP player,
            @NonNull final Container container) {
        final ForgeContainerAdapter containerAdapter =
                new ForgeContainerAdapter(ForgePlayerFactory.this.getPlugin(), container,
                        player.inventory);
        final IInventory inventory = containerAdapter.getContainer();

        containerAdapter.init();

        if (player.openContainer != player.inventoryContainer) {
            player.closeScreen();
        }

        player.getNextWindowId();
        player.connection.sendPacket(
                new SPacketOpenWindow(player.currentWindowId, "minecraft:container",
                        inventory.getDisplayName(), inventory.getSizeInventory()));
        player.openContainer = containerAdapter;
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addListener(player);
        MinecraftForge.EVENT_BUS.post(new PlayerContainerEvent.Open(player, player.openContainer));
    }

    @Override
    protected void playSound(@NonNull final EntityPlayerMP player, @NonNull final Sound sound) {
        final SoundContainer soundContainer = this.parseSound(sound);
        final SPacketSoundEffect packet =
                new SPacketSoundEffect(soundContainer.getSoundEvent(), SoundCategory.MASTER,
                        player.posX, player.posY, player.posZ, soundContainer.getVolume(),
                        soundContainer.getPitch());
        player.connection.sendPacket(packet);
    }

    @Override
    protected void addItem(@NonNull final EntityPlayerMP player,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack itemStack) {
        final ItemStack stack = this.platformFactory.transformItemStack(itemStack);
        final boolean result = player.isEntityAlive() && !this.hasDisconnected(player)
                && player.inventory.addItemStackToInventory(stack);

        // drop the remaining item stack, if it could not be fully added
        if (!result && stack.getCount() > 0) {
            player.entityDropItem(stack, 0);
        }
    }

    // TODO: Check
    @Override
    protected boolean teleport(@NonNull final EntityPlayerMP player, @NonNull final World world,
            final double x, final double y, final double z, final float yRot, final float xRot) {
        final net.minecraft.world.World level = this.platformFactory.transformWorld(world);
        final int dimensionId = level.provider.getDimension();

        if (dimensionId == player.world.provider.getDimension()) {
            player.setLocationAndAngles(x, y, z, yRot, xRot);
        } else {
            player.changeDimension(dimensionId, ForgeTeleporter.of(x, y, z, yRot, xRot));
        }

        // TODO: Check if player.setExperienceLevels(xpLevel); is needed

        // prevent fall damage upon teleport
        player.fallDistance = 0.0F;
        return true;
    }

    @NonNull
    private SoundContainer parseSound(@NonNull final Sound sound) {
        switch (sound) {
            case CRATE_CLOSE:
                return SoundContainer.of(SoundEvents.BLOCK_ENDERCHEST_CLOSE);
            case CRATE_OPEN:
                return SoundContainer.of(SoundEvents.BLOCK_ENDERCHEST_OPEN);
            case CRATE_REWARD:
                return SoundContainer.of(SoundEvents.ENTITY_PLAYER_LEVELUP);
            case CRATE_SPIN:
                return SoundContainer.of(SoundEvents.ENTITY_ENDEREYE_DEATH);
            case GUI_FAILURE:
                return SoundContainer.of(SoundEvents.ENTITY_VILLAGER_NO);
            case GUI_SUCCESS:
                return SoundContainer.of(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1F, 2F);
            default:
                throw new AssertionError();
        }
    }

    private static class SoundContainer {

        @NonNull
        private final SoundEvent soundEvent;
        private final float volume;
        private final float pitch;

        private SoundContainer(@NonNull final SoundEvent soundEvent, final float volume,
                final float pitch) {
            this.soundEvent = soundEvent;
            this.volume = volume;
            this.pitch = pitch;
        }

        private static SoundContainer of(@NonNull final SoundEvent soundEvent) {
            return new SoundContainer(soundEvent, 1F, 1F);
        }

        private static SoundContainer of(@NonNull final SoundEvent soundEvent, final float volume,
                final float pitch) {
            return new SoundContainer(soundEvent, volume, pitch);
        }

        @NonNull
        public SoundEvent getSoundEvent() {
            return this.soundEvent;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }
    }

    private static class ForgeTeleporter implements ITeleporter {

        private final double x;
        private final double y;
        private final double z;
        private final float yRot;
        private final float xRot;

        private ForgeTeleporter(final double x, final double y, final double z, final float yRot,
                final float xRot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yRot = yRot;
            this.xRot = xRot;
        }

        @NonNull
        private static ForgeTeleporter of(final double x, final double y, final double z,
                final float yRot, final float xRot) {
            return new ForgeTeleporter(x, y, z, yRot, xRot);
        }

        @Override
        public void placeEntity(final net.minecraft.world.World world, final Entity entity,
                final float yaw) {
            entity.setLocationAndAngles(this.x, this.y, this.z, this.yRot, this.xRot);
        }
    }
}
