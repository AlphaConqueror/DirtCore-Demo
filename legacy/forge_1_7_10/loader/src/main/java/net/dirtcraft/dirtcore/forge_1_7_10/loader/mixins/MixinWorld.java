/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader.mixins;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.loader.ForgeLoaderPlugin;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.BlockSnapshot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(World.class)
public abstract class MixinWorld {

    @Unique
    private final Lock dirtcore$lock = new ReentrantLock();
    @Unique
    private int dirtcore$metadata;

    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/Chunk;getBlock(III)Lnet/minecraft/block/Block;",
            shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSetBlock(final int x, final int y, final int z, final Block blockIn,
            final int metadataIn, final int flags, final CallbackInfoReturnable<Boolean> cir,
            final Chunk chunk, final Block block1, final BlockSnapshot blockSnapshot) {
        try {
            this.dirtcore$lock.lock();
            this.dirtcore$metadata = chunk.getBlockMetadata(x & 15, y, z & 15);
        } finally {
            this.dirtcore$lock.unlock();
        }
    }

    @Inject(method = "setBlock(IIILnet/minecraft/block/Block;II)Z", at = @At(value = "RETURN",
            ordinal = 2), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onSetBlock(final int x, final int y, final int z, final Block blockIn,
            final int metadataIn, final int flags, final CallbackInfoReturnable<Boolean> cir,
            final Chunk chunk, final Block block1, final BlockSnapshot blockSnapshot,
            final boolean flag) {
        if (flag) {
            ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
                try {
                    this.dirtcore$lock.lock();

                    // just to be safe, you never know
                    final ForgeBlock oldBlock = block1 == null ? ForgeBlock.of(Blocks.air, 0)
                            : ForgeBlock.of(block1, this.dirtcore$metadata);

                    //noinspection DataFlowIssue
                    eventDispatcher.dispatchBlockChangeEvent(oldBlock,
                            ForgeBlock.of(blockIn, metadataIn), flags, (World) (Object) this, x, y,
                            z);
                    this.dirtcore$metadata = 0; // reset metadata
                } finally {
                    this.dirtcore$lock.unlock();
                }
            });
        }
    }
}
