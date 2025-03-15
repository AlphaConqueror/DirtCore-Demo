/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.util;

import java.util.Optional;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeBlock {

    @NonNull
    private final BlockState blockState;
    @Nullable
    private final BlockEntity blockEntity;

    private ForgeBlock(@NonNull final BlockState blockState,
            @Nullable final BlockEntity blockEntity) {
        this.blockState = blockState;
        this.blockEntity = blockEntity;
    }

    @NonNull
    public static ForgeBlock of(@NonNull final BlockState blockState,
            @Nullable final BlockEntity blockEntity) {
        return new ForgeBlock(blockState, blockEntity);
    }

    @NonNull
    public static ForgeBlock of(@NonNull final BlockState blockState) {
        return new ForgeBlock(blockState, null);
    }

    @NonNull
    public static ForgeBlock of(@NonNull final Block block) {
        return new ForgeBlock(block.defaultBlockState(), null);
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
