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

package net.dirtcraft.dirtcore.forge_1_7_10.listeners;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.ForgePlatformFactory;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.lwjgl.input.Keyboard;

public class ForgePlatformListener {

    /**
     * {@link Achievement#achievementDescription}
     */
    private static final Field ACHIEVEMENT_DESCRIPTION;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            ACHIEVEMENT_DESCRIPTION = Achievement.class.getDeclaredField("field_75996_k");
            ACHIEVEMENT_DESCRIPTION.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @NonNull
    private final DirtCoreForgePlugin plugin;

    public ForgePlatformListener(@NonNull final DirtCoreForgePlugin plugin) {this.plugin = plugin;}

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAchievement(final AchievementEvent event) {
        final Achievement achievement = event.achievement;

        if (achievement == null) {
            return;
        }

        final EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
        final StatisticsFile statisticsFile = player.getStatFile();

        if (statisticsFile.hasAchievementUnlocked(achievement)
                || !statisticsFile.canUnlockAchievement(achievement)) {
            return;
        }

        final String achievementDescription;

        try {
            achievementDescription = (String) ACHIEVEMENT_DESCRIPTION.get(achievement);
        } catch (final IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        }

        final String statName =
                achievementDescription.substring(0, achievementDescription.length() - 5);
        final String advancementTitle = StatCollector.translateToLocal(statName);
        final String description = StatCollector.translateToLocalFormatted(achievementDescription,
                Keyboard.getKeyName(18));

        this.plugin.getEventDispatcher()
                .dispatchPlayerAchievement(player.getUniqueID(), player.getCommandSenderName(),
                        advancementTitle, description);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onCommand(final CommandEvent event) {
        final ICommandSender source = event.sender;
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

        final String commandLine = String.format("%s %s", event.command.getCommandName(),
                String.join(" ", event.parameters));
        final boolean cancel = this.plugin.getEventDispatcher()
                .dispatchServerCommand(false, uniqueId, username, commandLine);

        event.setCanceled(cancel);
    }

    @SubscribeEvent
    public void onPlayerAttack(final LivingAttackEvent event) {
        final DamageSource damageSource = event.source;
        final Entity source = damageSource.getEntity();

        if (!(source instanceof EntityPlayer)) {
            return;
        }

        final EntityPlayer player = (EntityPlayer) source;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();
        final ItemStack mcItemStack = player.getHeldItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                mcItemStack == null
                        ? net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack.EMPTY
                        : this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerAttack(false, uniqueId, username, dcItemStack));
    }

    @SubscribeEvent
    public void onPlayerBlockBreak(final BlockEvent.BreakEvent event) {
        final EntityPlayer player = event.getPlayer();
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();
        final net.minecraft.block.Block mcBlock = event.block;
        final int metadata = event.blockMetadata;
        final net.minecraft.world.World mcWorld = event.world;
        final int x = event.x;
        final int y = event.y;
        final int z = event.z;
        final TileEntity tileEntity = mcWorld.getTileEntity(x, y, z);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(ForgeBlock.of(mcBlock, metadata, tileEntity));
        final World world = this.plugin.getPlatformFactory().wrapWorld(mcWorld);

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerBlockBreak(false, uniqueId, username, block, world,
                        ForgePlatformFactory.transformBlockPos(x, y, z),
                        player instanceof FakePlayer));
    }

    @SubscribeEvent
    public void onPlayerDeath(final LivingDeathEvent event) {
        final EntityLivingBase entity = event.entityLiving;

        if (entity instanceof EntityPlayer) {
            final DamageSource damageSource = event.source;
            final String deathMessage = damageSource.getDeathMessage(entity).getUnformattedText();

            this.plugin.getEventDispatcher()
                    .dispatchPlayerDeath(entity.getUniqueID(), entity.getCommandSenderName(),
                            deathMessage);
        }
    }

    @SubscribeEvent
    public void onEntityInteract(final EntityInteractEvent event) {
        final Entity target = event.target;

        this.onPlayerInteract(event, (int) target.posY, (int) target.posX, (int) target.posZ,
                net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.ENTITY_INTERACT);
    }

    @SubscribeEvent
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final int x = event.x;
        final int y = event.y;
        final int z = event.z;

        switch (event.action) {
            case LEFT_CLICK_BLOCK:
                this.onPlayerInteract(event, x, y, z,
                        net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.LEFT_CLICK_BLOCK);
                break;
            case RIGHT_CLICK_AIR:
                this.onPlayerInteract(event, x, y, z,
                        net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.Type.RIGHT_CLICK_ITEM);
                break;
            case RIGHT_CLICK_BLOCK:
                this.onPlayerInteract(event, x, y, z, this.getRightClickBlockType(event));
                break;
            default:
                throw new AssertionError();
        }
    }

    @SubscribeEvent
    public void onPlayerItemDrop(final ItemTossEvent event) {
        final EntityPlayer player = event.player;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();
        final ItemStack mcItemStack = event.entityItem.getEntityItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerItemDrop(false, uniqueId, username, dcItemStack)) {
            event.setCanceled(true);
            player.inventory.addItemStackToInventory(mcItemStack);
        }
    }

    @SubscribeEvent
    public void onPlayerItemPickup(final EntityItemPickupEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack itemStack =
                this.plugin.getPlatformFactory().wrapItemStack(event.item.getEntityItem());

        event.setCanceled(this.plugin.getEventDispatcher()
                .dispatchPlayerItemPickup(false, uniqueId, username, itemStack));
    }

    @SubscribeEvent
    public void onPlayerLogin(final PlayerEvent.PlayerLoggedInEvent event) {
        final UUID uniqueId = event.player.getUniqueID();
        final String username = event.player.getCommandSenderName();

        this.plugin.getEventDispatcher().dispatchPlayerLogin(uniqueId, username);
    }

    @SubscribeEvent
    public void onPlayerLogout(final PlayerEvent.PlayerLoggedOutEvent event) {
        final EntityPlayerMP player = (EntityPlayerMP) event.player;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();

        this.plugin.getStorage().performTask(context -> {
            final Optional<BanEntity> banEntityOptional =
                    this.plugin.getPunishmentManager().getActiveBan(context, uniqueId);

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
        final EntityPlayerMP player = event.player;

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerChat(false, player.getUniqueID(), event.message)) {
            event.setCanceled(true);
        }
    }

    private void onPlayerInteract(
            final net.minecraftforge.event.entity.player.@NonNull PlayerEvent event, final int x,
            final int y, final int z,
            final net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type type) {
        final EntityPlayer player = event.entityPlayer;
        final UUID uniqueId = player.getUniqueID();
        final String username = player.getCommandSenderName();
        final ItemStack mcItemStack = player.getHeldItem();
        final net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack dcItemStack =
                mcItemStack == null
                        ? net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack.EMPTY
                        : this.plugin.getPlatformFactory().wrapItemStack(mcItemStack);
        final net.minecraft.world.World world = player.getEntityWorld();
        final net.minecraft.block.Block mcBlock = world.getBlock(x, y, z);
        final int metadata = world.getBlockMetadata(x, y, z);
        final TileEntity tileEntity = world.getTileEntity(x, y, z);
        final Block block = this.plugin.getPlatformFactory()
                .wrapBlock(ForgeBlock.of(mcBlock, metadata, tileEntity));
        final net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos pos =
                net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos.of(x, y, z);

        if (this.plugin.getEventDispatcher()
                .dispatchPlayerInteract(false, type, uniqueId, username, pos, dcItemStack, block)) {
            event.setCanceled(true);
        }
    }

    /**
     * Modified version of
     * {@link ItemBlock#onItemUse(ItemStack, EntityPlayer, net.minecraft.world.World, int, int, int, int, float, float, float)}
     */
    private net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent.@NonNull Type getRightClickBlockType(
            @NonNull final PlayerInteractEvent event) {
        final EntityPlayer player = event.entityPlayer;
        final ItemStack itemStack = player.getHeldItem();

        if (itemStack != null) {
            final Item item = itemStack.getItem();

            if (item instanceof ItemBlock) {
                final ItemBlock itemBlock = (ItemBlock) item;
                final net.minecraft.world.World world = event.world;
                int x = event.x;
                int y = event.y;
                int z = event.z;
                int side = event.face;
                final net.minecraft.block.Block block = world.getBlock(x, y, z);

                if (block == Blocks.snow_layer && (world.getBlockMetadata(x, y, z) & 7) < 1) {
                    side = 1;
                } else if (block != Blocks.vine && block != Blocks.tallgrass
                        && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z)) {
                    if (side == 0) {
                        --y;
                    }

                    if (side == 1) {
                        ++y;
                    }

                    if (side == 2) {
                        --z;
                    }

                    if (side == 3) {
                        ++z;
                    }

                    if (side == 4) {
                        --x;
                    }

                    if (side == 5) {
                        ++x;
                    }
                }

                final net.minecraft.block.Block blockInstance = itemBlock.blockInstance;

                if (itemStack.stackSize != 0 && player.canPlayerEdit(x, y, z, side, itemStack)
                        && y != 255 && !blockInstance.getMaterial().isSolid()
                        && world.canPlaceEntityOnSide(blockInstance, x, y, z, false, side, player,
                        itemStack)) {
                    final int metadata = itemBlock.getMetadata(itemStack.getMetadata());
                    // we default hitX, hitY and hitZ to zero because they are not being provided
                    final int metadataUpdated =
                            blockInstance.onBlockPlaced(world, x, y, z, side, 0, 0, 0, metadata);

                    if (this.canSetBlock(world, x, y, z, blockInstance, metadataUpdated)) {
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
     * {@link net.minecraft.world.World#setBlock(int, int, int, net.minecraft.block.Block, int, int)}
     *
     * @return true, if the block can be set
     */
    private boolean canSetBlock(final net.minecraft.world.World world, final int x, final int y,
            final int z, final net.minecraft.block.Block blockIn, final int metadataIn) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000 && y >= 0 && y < 256) {
            final Chunk chunk = world.getChunkFromChunkCoords(x >> 4, z >> 4);
            return this.canSetBlockIDWithMetadata(chunk, x & 15, y, z & 15, blockIn, metadataIn);
        }

        return false;
    }

    /**
     * Modified version of
     * {@link Chunk#setBlockIDWithMetadata(int, int, int, net.minecraft.block.Block, int)}
     *
     * @return true, if the block can be set
     */
    private boolean canSetBlockIDWithMetadata(final Chunk chunk, final int chunkX, final int y,
            final int chunkZ, final net.minecraft.block.Block block, final int metadataIn) {
        final net.minecraft.block.Block block1 = chunk.getBlock(chunkX, y, chunkZ);
        final int k1 = chunk.getBlockMetadata(chunkX, y, chunkZ);

        if (block1 == block && k1 == metadataIn) {
            return false;
        }

        final ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[y >> 4];
        return extendedblockstorage != null || block != Blocks.air;
    }
}
