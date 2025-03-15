/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.platform.minecraft.player.PlayerFactory;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.gui.container.ForgeContainerAdapter;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
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
        return Component.text(player.getDisplayName());
    }

    @Override
    protected boolean hasDisconnected(@NonNull final EntityPlayerMP player) {
        final NetHandlerPlayServer netHandlerPlayServer = player.playerNetServerHandler;

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
        final ItemStack itemStack = player.getHeldItem();
        return itemStack == null
                ? net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack.EMPTY
                : this.platformFactory.wrapItemStack(itemStack);
    }

    @Override
    protected @NonNull List<net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack> getItems(
            @NonNull final EntityPlayerMP player) {
        return Arrays.stream(player.inventory.mainInventory).map(itemStack -> itemStack == null
                        ? net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack.EMPTY
                        : this.platformFactory.wrapItemStack(itemStack))
                .collect(ImmutableCollectors.toList());
    }

    @Override
    protected boolean kick(@NonNull final EntityPlayerMP player, @NonNull final Component reason) {
        final NetworkManager networkManager = player.playerNetServerHandler.getNetworkManager();
        final IChatComponent reasonComponent = this.platformFactory.transformComponent(reason);

        networkManager.scheduleOutboundPacket(new S40PacketDisconnect(reasonComponent),
                future -> networkManager.closeChannel(reasonComponent));
        networkManager.disableAutoRead();
        return true;
    }

    @Override
    protected void sendMessage(@NonNull final EntityPlayerMP player,
            @NonNull final Component message) {
        player.addChatMessage(this.platformFactory.transformComponent(message));
    }

    @Override
    protected void sendMessage(@NonNull final EntityPlayerMP player,
            @NonNull final Iterable<Component> messages) {
        for (final Component c : messages) {
            player.addChatMessage(this.platformFactory.transformComponent(c));
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
        return player.theItemInWorldManager.isCreative();
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
        player.playerNetServerHandler.sendPacket(
                new S2DPacketOpenWindow(player.currentWindowId, 0, inventory.getInventoryName(),
                        inventory.getSizeInventory(), inventory.isCustomInventoryName()));
        player.openContainer = containerAdapter;
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.onCraftGuiOpened(player);
    }

    @Override
    protected void playSound(@NonNull final EntityPlayerMP player, @NonNull final Sound sound) {
        final SoundContainer soundContainer = this.parseSound(sound);
        final S29PacketSoundEffect packet =
                new S29PacketSoundEffect(soundContainer.getResourceLocation(), player.posX,
                        player.posY, player.posZ, soundContainer.getVolume(),
                        soundContainer.getPitch());
        player.playerNetServerHandler.sendPacket(packet);
    }

    @Override
    protected void addItem(@NonNull final EntityPlayerMP player,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack itemStack) {
        final ItemStack stack = this.platformFactory.transformItemStack(itemStack);
        final boolean result = player.isEntityAlive() && !this.hasDisconnected(player)
                && player.inventory.addItemStackToInventory(stack);

        // drop the remaining item stack, if it could not be fully added
        if (!result && stack.stackSize > 0) {
            player.entityDropItem(stack, 0);
        }
    }

    // TODO: Check
    @Override
    protected boolean teleport(@NonNull final EntityPlayerMP player, @NonNull final World world,
            final double x, final double y, final double z, final float yRot, final float xRot) {
        final Optional<MinecraftServer> serverOptional =
                this.getPlugin().getBootstrap().getServer();
        final net.minecraft.world.World mcWorld = this.platformFactory.transformWorld(world);

        if (serverOptional.isPresent() && mcWorld instanceof WorldServer) {
            final int dimensionId = mcWorld.provider.dimensionId;

            if (dimensionId == player.worldObj.provider.dimensionId) {
                player.setLocationAndAngles(x, y, z, yRot, xRot);
            } else {
                final WorldServer worldServer = (WorldServer) mcWorld;

                player.playerNetServerHandler.setPlayerLocation(x, y, z, yRot, xRot);
                serverOptional.get().getConfigurationManager()
                        .transferPlayerToDimension(player, dimensionId,
                                ForgeTeleporter.of(worldServer, yRot, xRot));
            }

            // TODO: Check if player.setExperienceLevels(xpLevel); is needed

            // prevent fall damage upon teleport
            player.fallDistance = 0.0F;
            return true;
        }

        return false;
    }

    @NonNull
    private SoundContainer parseSound(@NonNull final Sound sound) {
        switch (sound) {
            case CRATE_CLOSE:
                return SoundContainer.of("minecraft:random.chestclosed");
            case CRATE_OPEN:
                return SoundContainer.of("minecraft:random.chestopen");
            case CRATE_REWARD:
                return SoundContainer.of("minecraft:random.levelup");
            case CRATE_SPIN:
                return SoundContainer.of("minecraft:note.pling");
            case GUI_FAILURE:
                return SoundContainer.of("minecraft:mob.villager.no");
            case GUI_SUCCESS:
                return SoundContainer.of("minecraft:random.orb", 1F, 2F);
            default:
                throw new AssertionError();
        }
    }

    private static class SoundContainer {

        @NonNull
        private final String resourceLocation;
        private final float volume;
        private final float pitch;

        private SoundContainer(@NonNull final String resourceLocation, final float volume,
                final float pitch) {
            this.resourceLocation = resourceLocation;
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
        public String getResourceLocation() {
            return this.resourceLocation;
        }

        public float getVolume() {
            return this.volume;
        }

        public float getPitch() {
            return this.pitch;
        }
    }

    private static class ForgeTeleporter extends Teleporter {

        private final float yRot;
        private final float xRot;

        private ForgeTeleporter(@NonNull final WorldServer worldServer, final float yRot,
                final float xRot) {
            super(worldServer);
            this.yRot = yRot;
            this.xRot = xRot;
        }

        @NonNull
        private static ForgeTeleporter of(@NonNull final WorldServer worldServer, final float yRot,
                final float xRot) {
            return new ForgeTeleporter(worldServer, yRot, xRot);
        }

        @Override
        public void placeInPortal(final Entity entity, final double x, final double y,
                final double z, final float yaw) {
            entity.setLocationAndAngles(x, y, z, this.yRot, this.xRot);
        }
    }
}
