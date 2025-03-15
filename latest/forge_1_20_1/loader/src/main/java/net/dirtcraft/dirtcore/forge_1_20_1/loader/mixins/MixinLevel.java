/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.loader.mixins;

import net.dirtcraft.dirtcore.forge_1_20_1.loader.ForgeLoaderPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Level.class)
public abstract class MixinLevel implements LevelAccessor {

    @SuppressWarnings("SpellCheckingInspection")
    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;"
            + "Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At(value = "RETURN"
            , ordinal = 3), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSetBlock(final BlockPos pPos, final BlockState pState, final int pFlags,
            final int pRecursionLeft, final CallbackInfoReturnable<Boolean> cir,
            final LevelChunk levelchunk, final Block block, final BlockSnapshot blockSnapshot,
            final BlockState old, final int oldLight, final int oldOpacity,
            final BlockState blockstate, final BlockState blockstate1) {
        //noinspection DataFlowIssue
        ForgeLoaderPlugin.getEventDispatcher().ifPresent(
                eventDispatcher -> eventDispatcher.dispatchBlockChangeEvent(old, blockstate1,
                        pFlags, (Level) (Object) this, pPos.getX(), pPos.getY(), pPos.getZ()));
    }
}
