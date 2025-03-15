/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.loader.ForgeLoaderPlugin;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class MixinWorld {

    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;"
            + "Lnet/minecraft/block/state/IBlockState;I)Z", at = @At(value = "RETURN", ordinal =
            3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSetBlock(final BlockPos pos, final IBlockState newState, final int flags,
            final CallbackInfoReturnable<Boolean> cir, final Chunk chunk,
            final BlockSnapshot blockSnapshot, final IBlockState oldState, final int oldLight,
            final int oldOpacity, final IBlockState iblockstate) {
        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            //noinspection DataFlowIssue
            eventDispatcher.dispatchBlockChangeEvent(ForgeBlock.of(oldState),
                    ForgeBlock.of(newState), flags, (World) (Object) this, pos.getX(), pos.getY(),
                    pos.getZ());
        });
    }
}
