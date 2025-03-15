/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.NeoForgePlatformFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeBlock;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgePlatformListener {

    /**
     * {@link BlockItem#getPlacementState(BlockPlaceContext)}
     */
    private static final Method GET_PLACEMENT_STATE_METHOD;

    static {
        try {
            GET_PLACEMENT_STATE_METHOD =
                    BlockItem.class.getDeclaredMethod("getPlacementState", BlockPlaceContext.class);
            GET_PLACEMENT_STATE_METHOD.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private final DirtCoreNeoForgePlugin plugin;

    public NeoForgePlatformListener(@NonNull final DirtCoreNeoForgePlugin plugin) {
        this.plugin = plugin;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(final AdvancementEvent.AdvancementEarnEvent event) {
        final Advancement advancement = event.getAdvancement().value();
        final Optional<DisplayInfo> displayOptional = advancement.display();

        if (displayOptional.isEmpty()) {
            return;
        }

        final DisplayInfo display = displayOptional.get();

        if (!display.shouldAnnounceChat() || advancement.parent().isEmpty()) {
            return;
        }

        final Player player = event.getEntity();
        final String advancementTitle =
                this.plugin.getPlatformFactory().stripFormatting(display.getTitle().getString());
        final String description = this.plugin.getPlatformFactory()
                .stripFormatting(display.getDescription().getString());

        this.plugin.getEventDispatcher()
                .dispatchPlayerAchievement(player.getUUID(), player.getName().getString(),
                        advancementTitle, description);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCommand(final CommandEvent event) {
        final CommandSourceStack source = event.getParseResults().getContext().getSource();
        final UUID uniqueId;
        final String username;

        if (source.source instanceof final Player player) {
            uniqueId = player.getUUID();
            username = player.getGameProfile().getName();
        } else {
            uniqueId = Sender.CONSOLE_UUID;
            username = Sender.CONSOLE_NAME;
        }

        final String commandLine = event.getParseResults().getReader().getString();
        final boolean cancel = this.plugin.getEventDispatcher()
                .dispatchServerCommand(false, uniqueId, username, commandLine);

        event.setCanceled(cancel);
    }

    @SubscribeEvent
    public void onPlayerAttack(final AttackEntityEvent event) {
        final Player player = event.getEntity();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final ItemStack mcItemStack = player.getMainHandItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerAttack(false, uniqueId, username, dcItemStack));
    }

    @SubscribeEvent
    public void onPlayerBlockBreak(final BlockEvent.BreakEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final BlockPos blockPos = event.getPos();
        final BlockState blockState = event.getLevel().getBlockState(blockPos);
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(NeoForgeBlock.of(blockState, blockEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld((Level) event.getLevel());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockBreak(false, uniqueId, username, block, world,
                        NeoForgePlatformFactory.transformBlockPos(blockPos),
                        player.isFakePlayer()));
    }

    @SubscribeEvent
    public void onPlayerBlockPlace(final BlockEvent.EntityPlaceEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof final Player player)) {
            return;
        }

        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final BlockPos blockPos = event.getPos();
        final BlockState blockState = event.getLevel().getBlockState(blockPos);
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(NeoForgeBlock.of(blockState, blockEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld((Level) event.getLevel());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockPlace(false, uniqueId, username, block, world, blockPos.getX(),
                        blockPos.getY(), blockPos.getZ(), player.isFakePlayer()));
    }

    @SubscribeEvent
    public void onPlayerDeath(final LivingDeathEvent event) {
        final LivingEntity entity = event.getEntity();

        if (entity instanceof Player) {
            final String deathMessage = entity.getCombatTracker().getDeathMessage().getString();

            this.plugin.getEventDispatcher()
                    .dispatchPlayerDeath(entity.getUUID(), entity.getName().getString(),
                            deathMessage);
        }
    }

    @SubscribeEvent
    public void onPlayerInteractEntityInteractSpecific(
            final PlayerInteractEvent.EntityInteractSpecific event) {
        this.onPlayerInteract(event,
                net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.ENTITY_INTERACT_SPECIFIC);
    }

    @SubscribeEvent
    public void onPlayerInteractEntityInteract(final PlayerInteractEvent.EntityInteract event) {
        this.onPlayerInteract(event,
                net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.ENTITY_INTERACT);
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        this.onPlayerInteract(event, this.getRightClickBlockType(event));
    }

    @SubscribeEvent
    public void onPlayerInteractRightClickItem(final PlayerInteractEvent.RightClickItem event) {
        this.onPlayerInteract(event,
                net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_ITEM);
    }

    @SubscribeEvent
    public void onPlayerInteractLeftClickBlock(final PlayerInteractEvent.LeftClickBlock event) {
        this.onPlayerInteract(event,
                net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.LEFT_CLICK_BLOCK);
    }

    @SubscribeEvent
    public void onPlayerItemDrop(final ItemTossEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final ItemStack mcItemStack = event.getEntity().getItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerItemDrop(false, uniqueId, username, dcItemStack)) {
            event.setCanceled(true);
            player.addItem(mcItemStack);
        }
    }

    @SubscribeEvent
    public void onPlayerItemPickup(final ItemEntityPickupEvent.Pre event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack itemStack =
                this.plugin.getPlatformFactory().wrapItemStack(event.getItemEntity().getItem());

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerItemPickup(false, uniqueId, username, itemStack)) {
            event.setCanPickup(TriState.FALSE);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        final UUID uniqueId = event.getEntity().getUUID();
        final String username = event.getEntity().getName().getString();

        this.plugin.getEventDispatcher().dispatchPlayerLogin(uniqueId, username);
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        this.plugin.getEventDispatcher().dispatchPlayerLogout(event.getEntity().getUUID(),
                event.getEntity().getName().getString());
    }

    @SubscribeEvent
    public void onServerChat(final ServerChatEvent event) {
        final ServerPlayer player = event.getPlayer();

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerChat(false, player.getUUID(), event.getMessage().getString())) {
            event.setCanceled(true);
        }
    }

    private void onPlayerInteract(@NonNull final PlayerInteractEvent event,
            final net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type type) {
        final Player player = event.getEntity();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final ItemStack mcItemStack = event.getItemStack();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);
        final BlockPos mcPos = event.getPos();
        final BlockState blockState = event.getLevel().getBlockState(mcPos);
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(mcPos);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(NeoForgeBlock.of(blockState, blockEntity));
        final net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos pos =
                net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos.of(mcPos.getX(),
                        mcPos.getY(), mcPos.getZ());

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerInteract(false, type, uniqueId, username, pos, dcItemStack, block)
                && event instanceof final ICancellableEvent cancellableEvent) {
            cancellableEvent.setCanceled(true);
        }
    }

    private net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type getRightClickBlockType(
            final PlayerInteractEvent.@NonNull RightClickBlock event) {
        final Item item = event.getItemStack().getItem();

        // here we just follow the steps Minecraft takes to check if a block can be placed
        if (item instanceof final BlockItem blockItem) {
            final BlockPlaceContext blockPlaceContext = new BlockPlaceContext(
                    new UseOnContext(event.getEntity(), event.getHand(), event.getHitVec()));
            final BlockPlaceContext updatedPlacementContext =
                    blockItem.updatePlacementContext(blockPlaceContext);

            if (updatedPlacementContext != null) {
                try {
                    if (GET_PLACEMENT_STATE_METHOD.invoke(blockItem, updatedPlacementContext)
                            != null) {
                        // should be handled by block place event
                        return net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_BLOCK_PLACE;
                    }
                } catch (final IllegalAccessException | InvocationTargetException e) {
                    this.plugin.getLogger().severe("Could not invoke getPlacementState method.", e);
                }
            }
        }

        // normal right click without placing a block
        return net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_BLOCK;
    }
}
