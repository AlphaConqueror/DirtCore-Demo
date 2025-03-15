/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import java.util.List;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.loader.ForgeLoaderPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = ForgeHooks.class, remap = false)
public abstract class MixinForgeHooks {

    @SuppressWarnings("deprecation")
    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet"
            + "/minecraftforge/event/ForgeEventFactory;onPlayerMultiBlockPlace"
            + "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;"
            + "Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/EnumHand;)"
            + "Lnet/minecraftforge/event/world/BlockEvent$MultiPlaceEvent;", shift =
            At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onPlaceItemIntoWorldMulti(final ItemStack itemstack,
            final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
            final float hitX, final float hitY, final float hitZ, final EnumHand hand,
            final CallbackInfoReturnable<EnumActionResult> cir, final int meta, final int size,
            final NBTTagCompound nbt, final EnumActionResult ret, final int newMeta,
            final int newSize, final NBTTagCompound newNBT, final BlockEvent.PlaceEvent placeEvent,
            final List<BlockSnapshot> blockSnapshots) {
        dirtcore$onPlaceItemIntoWorld(player, meta, placeEvent, blockSnapshots);
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet"
            + "/minecraftforge/event/ForgeEventFactory;onPlayerBlockPlace"
            + "(Lnet/minecraft/entity/player/EntityPlayer;"
            + "Lnet/minecraftforge/common/util/BlockSnapshot;Lnet/minecraft/util/EnumFacing;"
            + "Lnet/minecraft/util/EnumHand;)"
            + "Lnet/minecraftforge/event/world/BlockEvent$PlaceEvent;", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onPlaceItemIntoWorldSingle(final ItemStack itemstack,
            final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
            final float hitX, final float hitY, final float hitZ, final EnumHand hand,
            final CallbackInfoReturnable<EnumActionResult> cir, final int meta, final int size,
            final NBTTagCompound nbt, final EnumActionResult ret, final int newMeta,
            final int newSize, final NBTTagCompound newNBT, final BlockEvent.PlaceEvent placeEvent,
            final List<BlockSnapshot> blockSnapshots) {
        dirtcore$onPlaceItemIntoWorld(player, meta, placeEvent, blockSnapshots);
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "TAIL"), locals =
            LocalCapture.CAPTURE_FAILHARD)
    private static void onPlaceItemIntoWorldTail(final ItemStack itemstack,
            final EntityPlayer player, final World world, final BlockPos pos, final EnumFacing side,
            final float hitX, final float hitY, final float hitZ, final EnumHand hand,
            final CallbackInfoReturnable<EnumActionResult> cir, final int meta, final int size,
            final NBTTagCompound nbt, final EnumActionResult ret) {
        if (ret == EnumActionResult.FAIL) {
            final Container container = player.inventoryContainer;

            // fix inventory de-sync
            for (final IContainerListener crafter :
                    ((MixinContainerAccessor) container).getListeners()) {
                crafter.sendAllContents(container, container.getInventory());
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Unique
    private static void dirtcore$onPlaceItemIntoWorld(final EntityPlayer player, final int meta,
            final BlockEvent.PlaceEvent placeEvent, final List<BlockSnapshot> blockSnapshots) {
        if (placeEvent != null && !placeEvent.isCanceled()) {
            ForgeLoaderPlugin.getEventDispatcher().ifPresent(dispatcher -> {
                final BlockSnapshot blockSnapshot = blockSnapshots.get(0);
                final IBlockState blockState = blockSnapshot.getCurrentBlock();
                final World world = blockSnapshot.getWorld();
                final BlockPos pos = blockSnapshot.getPos();
                final TileEntity tileEntity = world.getTileEntity(pos);

                if (dispatcher.dispatchPlayerBlockPlace(false, player.getUniqueID(),
                        player.getName(), ForgeBlock.of(blockState.getBlock(), meta, tileEntity),
                        world, pos.getX(), pos.getY(), pos.getZ(), player instanceof FakePlayer)) {
                    placeEvent.setCanceled(true);
                }
            });
        }
    }
}
