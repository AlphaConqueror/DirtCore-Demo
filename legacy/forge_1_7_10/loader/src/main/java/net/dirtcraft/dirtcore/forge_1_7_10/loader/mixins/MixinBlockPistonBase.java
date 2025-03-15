/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader.mixins;

import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.loader.ForgeLoaderPlugin;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockPistonBase.class)
public abstract class MixinBlockPistonBase {

    @Inject(at = @At("HEAD"), method = "canPushBlock", cancellable = true)
    private static void canPushBlock(final Block p_150080_0_, final World p_150080_1_,
            final int p_150080_2_, final int p_150080_3_, final int p_150080_4_,
            final boolean p_150080_5_, final CallbackInfoReturnable<Boolean> cir) {
        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchBlockPushReactionEvent(ForgeBlock.of(p_150080_0_,
                    p_150080_1_.getBlockMetadata(p_150080_2_, p_150080_3_, p_150080_4_)))) {
                cir.setReturnValue(false);
            }
        });
    }
}
