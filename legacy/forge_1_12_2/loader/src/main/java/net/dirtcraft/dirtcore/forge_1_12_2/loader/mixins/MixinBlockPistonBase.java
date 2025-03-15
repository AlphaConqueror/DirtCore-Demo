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
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {

    @Inject(at = @At("HEAD"), method = "canPush", cancellable = true)
    private static void canPushBlock(final IBlockState blockStateIn, final World worldIn,
            final BlockPos pos, final EnumFacing facing, final boolean destroyBlocks,
            final EnumFacing p_185646_5_, final CallbackInfoReturnable<Boolean> cir) {
        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchBlockPushReactionEvent(ForgeBlock.of(blockStateIn))) {
                cir.setReturnValue(false);
            }
        });
    }
}
