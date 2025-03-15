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

package net.dirtcraft.dirtcore.forge_1_12_2.listeners;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.ForgePlatformFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgePlatformListener {

    @NonNull
    private final DirtCoreForgePlugin plugin;

    public ForgePlatformListener(@NonNull final DirtCoreForgePlugin plugin) {this.plugin = plugin;}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(final AdvancementEvent event) {
        final Advancement advancement = event.getAdvancement();
        final DisplayInfo display = advancement.getDisplay();

        if (display == null || !display.shouldAnnounceToChat() || advancement.getParent() == null) {
            return;
        }

        final EntityPlayer player = event.getEntityPlayer();
        final String advancementTitle = display.getTitle().getUnformattedText();
        final String description = display.getDescription().getUnformattedText();

        this.plugin.getEventDispatcher()
                .dispatchPlayerAchievement(player.getUniqueID(), player.getName(), advancementTitle,
                        description);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCommand(final CommandEvent event) {
        final ICommandSender source = event.getSender();
        final UUID uniqueId;
        final String username;

        if (source instanceof EntityPlayer) {
            final EntityPlayer player = (EntityPlayer) source;

            uniqueId = player.getUniqueID();
            username = player.getGameProfile().getName();
        } else {
            uniqueId = Sender.CONSOLE_UUID;
            username = Sender.CONSOLE_NAME;
        }

        final String commandLine = String.format("%s %s", event.getCommand().getName(),
                String.join(" ", event.getParameters()));
        final boolean cancel = this.plugin.getEventDispatcher()
                .dispatchServerCommand(false, uniqueId, username, commandLine);

        event.setCanceled(cancel);
    }

    @SubscribeEvent
    public void onPlayerAttack(final LivingAttackEvent event) {
        final DamageSource damageSource = event.getSource();
        final Entity trueSource = damageSource.getTrueSource();
        final Entity source = trueSource == null ? damageSource.getImmediateSource() : trueSource;

        if (!(source instanceof EntityPlayer)) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) source;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(player.getHeldItemMainhand());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerAttack(false, uniqueId, username, dcItemStack));
    }

    @SubscribeEvent
    public void onPlayerBlockBreak(final BlockEvent.BreakEvent event) {
        final EntityPlayer player = event.getPlayer();
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();
        final IBlockState blockState = event.getState();
        final net.minecraft.block.Block mcBlock = blockState.getBlock();
        final int metadata = mcBlock.getMetaFromState(blockState);
        final net.minecraft.world.World mcWorld = event.getWorld();
        final BlockPos blockPos = event.getPos();
        final TileEntity tileEntity = mcWorld.getTileEntity(blockPos);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(ForgeBlock.of(mcBlock, metadata, tileEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld(mcWorld);

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockBreak(false, uniqueId, username, block, world,
                        ForgePlatformFactory.transformBlockPos(blockPos),
                        player instanceof FakePlayer));
    }

    @SubscribeEvent
    public void onPlayerDeath(final LivingDeathEvent event) {
        final EntityLivingBase entity = (EntityLivingBase) event.getEntity();

        if (entity instanceof EntityPlayer) {
            final DamageSource damageSource = event.getSource();
            final String deathMessage = damageSource.getDeathMessage(entity).getUnformattedText();

            this.plugin.getEventDispatcher()
                    .dispatchPlayerDeath(entity.getUniqueID(), entity.getName(), deathMessage);
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
        final EntityPlayer player = event.getPlayer();
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();
        final ItemStack mcItemStack = event.getEntityItem().getItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerItemDrop(false, uniqueId, username, dcItemStack)) {
            event.setCanceled(true);
            player.addItemStackToInventory(mcItemStack);
        }
    }

    @SubscribeEvent
    public void onPlayerItemPickup(final EntityItemPickupEvent event) {
        final EntityPlayer player = event.getEntityPlayer();
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack itemStack =
                this.plugin.getPlatformFactory().wrapItemStack(event.getItem().getItem());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerItemPickup(false, uniqueId, username, itemStack));
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        final EntityPlayer player = event.player;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();

        this.plugin.getEventDispatcher().dispatchPlayerLogin(uniqueId, username);
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        final EntityPlayerMP player = (EntityPlayerMP) event.player;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();

        this.plugin.getStorage().performTask(context -> {
            final Optional<BanEntity> banEntityOptional =
                    this.plugin.getPunishmentManager().getActiveBan(context, uniqueId);

            // TODO: Find a better way. Players disconnecting due to mod mismatch trigger logouts
            //  but not logins. 
            // only dispatch if player is not banned or ban just happened
            // legacy Forge is special
            if (!banEntityOptional.isPresent() || banEntityOptional.get().getTimestamp().toInstant()
                    .plusSeconds(2).isAfter(Instant.now())) {
                context.queue(() -> this.plugin.getEventDispatcher()
                        .dispatchPlayerLogout(uniqueId, username));
            }
        });
    }

    @SubscribeEvent
    public void onServerChat(final ServerChatEvent event) {
        final EntityPlayerMP player = event.getPlayer();

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerChat(false, player.getUniqueID(), event.getMessage())) {
            event.setCanceled(true);
        }
    }

    private void onPlayerInteract(
            final net.minecraftforge.event.entity.player.@NonNull PlayerInteractEvent event,
            final net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type type) {
        final EntityPlayer player = event.getEntityPlayer();
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getName();
        final ItemStack mcItemStack = player.getHeldItemMainhand();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);
        final net.minecraft.world.World world = player.getEntityWorld();
        final BlockPos blockPos = event.getPos();
        final IBlockState blockState = world.getBlockState(blockPos);
        final net.minecraft.block.Block mcBlock = blockState.getBlock();
        final int metadata = mcBlock.getMetaFromState(blockState);
        final TileEntity tileEntity = world.getTileEntity(blockPos);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(ForgeBlock.of(mcBlock, metadata, tileEntity));

        if (this.plugin.getEventDispatcher().dispatchPlayerInteract(false, type, uniqueId, username,
                ForgePlatformFactory.transformBlockPos(blockPos), dcItemStack, block)) {
            event.setCanceled(true);
        }
    }

    /**
     * Modified version of
     * {@link ItemBlock#onItemUse(EntityPlayer, net.minecraft.world.World, BlockPos, EnumHand, EnumFacing, float, float, float)}
     */
    private net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type getRightClickBlockType(
            final PlayerInteractEvent.@NonNull RightClickBlock event) {
        final EntityPlayer player = event.getEntityPlayer();
        final EnumHand hand = event.getHand();
        final ItemStack itemstack = player.getHeldItem(hand);
        final Item item = itemstack.getItem();

        if (item instanceof ItemBlock) {
            final EnumFacing facing = event.getFace();

            if (facing != null) {
                final net.minecraft.world.World worldIn = event.getWorld();
                BlockPos pos = event.getPos();
                final IBlockState iblockstate = worldIn.getBlockState(pos);
                final net.minecraft.block.Block block = iblockstate.getBlock();

                if (!block.isReplaceable(worldIn, pos)) {
                    pos = pos.offset(facing);
                }

                final ItemBlock itemBlock = (ItemBlock) item;
                final net.minecraft.block.Block rawBlock = itemBlock.getBlock();

                if (!itemstack.isEmpty() && player.canPlayerEdit(pos, facing, itemstack)
                        && worldIn.mayPlace(rawBlock, pos, false, facing, player)) {
                    final Vec3d hitVec = event.getHitVec();
                    final float hitX;
                    final float hitY;
                    final float hitZ;

                    if (hitVec == null) {
                        hitX = 0;
                        hitY = 0;
                        hitZ = 0;
                    } else {
                        hitX = (float) hitVec.x;
                        hitY = (float) hitVec.y;
                        hitZ = (float) hitVec.z;
                    }

                    final int i = itemBlock.getMetadata(itemstack.getMetadata());
                    final IBlockState newState =
                            rawBlock.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, i,
                                    player, hand);

                    if (this.canSetBlock(worldIn, pos, newState)) {
                        // should be handled by block place event
                        return net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_BLOCK_PLACE;
                    }
                }
            }
        }

        // normal right click without placing a block
        return net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_BLOCK;
    }

    /**
     * Modified version of
     * {@link net.minecraft.world.World#setBlockState(BlockPos, IBlockState, int)}
     *
     * @return true, if the block can be set
     */
    private boolean canSetBlock(final net.minecraft.world.@NonNull World world,
            @NonNull final BlockPos pos, @NonNull final IBlockState newState) {
        if (world.isOutsideBuildHeight(pos)) {
            return false;
        }

        if (!world.isRemote
                && world.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        }

        final Chunk chunk = world.getChunk(pos);
        return this.canSetBlockState(chunk, pos, newState);
    }

    /**
     * Modified version of
     * {@link Chunk#setBlockState(BlockPos, IBlockState)}
     *
     * @return true, if the block can be set
     */
    private boolean canSetBlockState(@NonNull final Chunk chunk, @NonNull final BlockPos pos,
            @NonNull final IBlockState state) {
        final IBlockState iblockstate = chunk.getBlockState(pos);

        if (iblockstate == state) {
            return false;
        }

        final net.minecraft.block.Block block = state.getBlock();
        final ExtendedBlockStorage extendedblockstorage =
                chunk.getBlockStorageArray()[pos.getY() >> 4];
        return extendedblockstorage != Chunk.NULL_BLOCK_STORAGE || block != Blocks.AIR;
    }
}
