/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.loader.mixins;

import net.dirtcraft.dirtcore.neoforge_1_21_1.loader.NeoForgeLoaderPlugin;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockBehaviour$BlockStateBase {

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "getPistonPushReaction", at = @At("HEAD"), cancellable = true)
    private void onGetPistonPushReaction(final CallbackInfoReturnable<PushReaction> cir) {
        NeoForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchBlockPushReactionEvent((BlockState) (Object) this)) {
                cir.setReturnValue(PushReaction.BLOCK);
            }
        });
    }
}
