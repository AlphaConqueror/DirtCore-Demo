/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.util;

import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NeoForgeBlock {

    @NonNull
    private final BlockState blockState;
    @Nullable
    private final BlockEntity blockEntity;

    private NeoForgeBlock(@NonNull final BlockState blockState,
            @Nullable final BlockEntity blockEntity) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
    }

    @NonNull
    public static NeoForgeBlock of(@NonNull final BlockState blockState,
            @Nullable final BlockEntity blockEntity) {
        return new NeoForgeBlock(blockState, blockEntity);
    }

    @NonNull
    public static NeoForgeBlock of(@NonNull final BlockState blockState) {
        return new NeoForgeBlock(blockState, null);
    }

    @NonNull
    public static NeoForgeBlock of(@NonNull final Block block) {
        return new NeoForgeBlock(block.defaultBlockState(), null);
    }

    @NonNull
    public BlockState getBlockState() {
        return this.blockState;
    }

    @NonNull
    public Optional<BlockEntity> getBlockEntity() {
        return Optional.ofNullable(this.blockEntity);
    }
}
