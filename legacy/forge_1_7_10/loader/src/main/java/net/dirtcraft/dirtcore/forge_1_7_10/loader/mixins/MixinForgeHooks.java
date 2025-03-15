/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader.mixins;

import java.util.List;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.loader.ForgeLoaderPlugin;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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

    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet"
            + "/minecraftforge/event/ForgeEventFactory;onPlayerMultiBlockPlace"
            + "(Lnet/minecraft/entity/player/EntityPlayer;Ljava/util/List;"
            + "Lnet/minecraftforge/common/util/ForgeDirection;)"
            + "Lnet/minecraftforge/event/world/BlockEvent$MultiPlaceEvent;", shift =
            At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onPlaceItemIntoWorldMulti(final ItemStack itemstack,
            final EntityPlayer player, final World world, final int x, final int y, final int z,
            final int side, final float hitX, final float hitY, final float hitZ,
            final CallbackInfoReturnable<Boolean> cir, final int meta, final int size,
            final NBTTagCompound nbt, final boolean flag, final int newMeta, final int newSize,
            final NBTTagCompound newNBT, final BlockEvent.PlaceEvent placeEvent,
            final List<net.minecraftforge.common.util.BlockSnapshot> blockSnapshots) {
        dirtcore$onPlaceItemIntoWorld(player, meta, placeEvent, blockSnapshots);
    }

    @Inject(method = "onPlaceItemIntoWorld", at = @At(value = "INVOKE_ASSIGN", target = "Lnet"
            + "/minecraftforge/event/ForgeEventFactory;onPlayerBlockPlace"
            + "(Lnet/minecraft/entity/player/EntityPlayer;"
            + "Lnet/minecraftforge/common/util/BlockSnapshot;"
            + "Lnet/minecraftforge/common/util/ForgeDirection;)"
            + "Lnet/minecraftforge/event/world/BlockEvent$PlaceEvent;", shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onPlaceItemIntoWorldSingle(final ItemStack itemstack,
            final EntityPlayer player, final World world, final int x, final int y, final int z,
            final int side, final float hitX, final float hitY, final float hitZ,
            final CallbackInfoReturnable<Boolean> cir, final int meta, final int size,
            final NBTTagCompound nbt, final boolean flag, final int newMeta, final int newSize,
            final NBTTagCompound newNBT, final BlockEvent.PlaceEvent placeEvent,
            final List<net.minecraftforge.common.util.BlockSnapshot> blockSnapshots) {
        dirtcore$onPlaceItemIntoWorld(player, meta, placeEvent, blockSnapshots);
    }

    @Unique
    private static void dirtcore$onPlaceItemIntoWorld(final EntityPlayer player, final int meta,
            final BlockEvent.PlaceEvent placeEvent, final List<BlockSnapshot> blockSnapshots) {
        if (placeEvent != null && !placeEvent.isCanceled()) {
            ForgeLoaderPlugin.getEventDispatcher().ifPresent(dispatcher -> {
                final BlockSnapshot blockSnapshot = blockSnapshots.get(0);
                final Block block = blockSnapshot.getCurrentBlock();
                final World world = blockSnapshot.getWorld();
                final int x = blockSnapshot.x;
                final int y = blockSnapshot.y;
                final int z = blockSnapshot.z;
                final TileEntity tileEntity = world.getTileEntity(x, y, z);

                if (dispatcher.dispatchPlayerBlockPlace(false, player.getUniqueID(),
                        player.getCommandSenderName(), ForgeBlock.of(block, meta, tileEntity),
                        world, x, y, z, player instanceof FakePlayer)) {
                    placeEvent.setCanceled(true);
                }
            });
        }
    }
}
