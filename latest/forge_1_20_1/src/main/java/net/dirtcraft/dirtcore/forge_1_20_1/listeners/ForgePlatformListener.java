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

package net.dirtcraft.dirtcore.forge_1_20_1.listeners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.ForgePlatformFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgePlatformListener {

    /**
     * {@link BlockItem#getPlacementState(BlockPlaceContext)}
     */
    private static final Method GET_PLACEMENT_STATE_METHOD;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            GET_PLACEMENT_STATE_METHOD =
                    BlockItem.class.getDeclaredMethod("m_5965_", BlockPlaceContext.class);
            GET_PLACEMENT_STATE_METHOD.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    private final DirtCoreForgePlugin plugin;

    public ForgePlatformListener(@NonNull final DirtCoreForgePlugin plugin) {this.plugin = plugin;}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(final AdvancementEvent.AdvancementEarnEvent event) {
        final Advancement advancement = event.getAdvancement();
        final DisplayInfo display = advancement.getDisplay();

        if (display == null || !display.shouldAnnounceChat() || advancement.getParent() == null) {
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

        if (source.source instanceof Player) {
            final Player player = (Player) source.source;

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
    public void onPlayerAttack(final LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        final Entity source = damageSource.getEntity();

        if (!(source instanceof Player)) {
            return;
        }

        final Player player = (Player) source;
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
        final Block block =
                this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(blockState, blockEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld((Level) event.getLevel());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockBreak(false, uniqueId, username, block, world,
                        ForgePlatformFactory.transformBlockPos(blockPos),
                        player instanceof FakePlayer));
    }

    @SubscribeEvent
    public void onPlayerBlockPlace(final BlockEvent.EntityPlaceEvent event) {
        final Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            return;
        }

        final Player player = (Player) entity;
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final BlockPos blockPos = event.getPos();
        final BlockState blockState = event.getLevel().getBlockState(blockPos);
        final BlockEntity blockEntity = event.getLevel().getBlockEntity(blockPos);
        final Block block =
                this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(blockState, blockEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld((Level) event.getLevel());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockPlace(false, uniqueId, username, block, world, blockPos.getX(),
                        blockPos.getY(), blockPos.getZ(), player instanceof FakePlayer));
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
    public void onPlayerItemPickup(final EntityItemPickupEvent event) {
        final Player player = event.getEntity();
        final UUID uniqueId = player.getUUID();
        final String username = player.getName().getString();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack itemStack =
                this.plugin.getPlatformFactory().wrapItemStack(event.getItem().getItem());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerItemPickup(false, uniqueId, username, itemStack));
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

        // TODO: Maybe instead of cancelling the whole message, use the prefix/suffix utilities
        //  to get rid system messages being used everywhere and enable reporting?
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
        final Block block =
                this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(blockState, blockEntity));
        final net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos pos =
                net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos.of(mcPos.getX(),
                        mcPos.getY(), mcPos.getZ());

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerInteract(false, type, uniqueId, username, pos, dcItemStack, block)) {
            event.setCanceled(true);
        }
    }


    private net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type getRightClickBlockType(
            final PlayerInteractEvent.@NonNull RightClickBlock event) {
        final Item item = event.getItemStack().getItem();

        // here we just follow the steps Minecraft takes to check if a block can be placed
        if (item instanceof BlockItem) {
            final BlockItem blockItem = (BlockItem) item;
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
