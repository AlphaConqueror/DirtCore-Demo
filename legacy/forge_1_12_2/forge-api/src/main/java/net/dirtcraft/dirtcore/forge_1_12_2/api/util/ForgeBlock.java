/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.api.util;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeBlock {

    @NonNull
    private final Block block;
    private final int metadata;
    @Nullable
    private final TileEntity tileEntity;

    private ForgeBlock(@NonNull final Block block, final int metadata,
            @Nullable final TileEntity tileEntity) {
        this.block = block;
        this.metadata = metadata;
        this.tileEntity = tileEntity;
    }

    @NonNull
    public static ForgeBlock of(@NonNull final Block block, final int metadata,
            @Nullable final TileEntity tileEntity) {
        return new ForgeBlock(block, metadata, tileEntity);
    }

    @NonNull
    public static ForgeBlock of(@NonNull final Block block, final int metadata) {
        return of(block, metadata, null);
    }

    @NonNull
    public static ForgeBlock of(@NonNull final IBlockState blockState,
            @Nullable final TileEntity tileEntity) {
        final Block block = blockState.getBlock();
        return new ForgeBlock(block, block.getMetaFromState(blockState), tileEntity);
    }

    @NonNull
    public static ForgeBlock of(@NonNull final IBlockState blockState) {
        final Block block = blockState.getBlock();
        return of(block, block.getMetaFromState(blockState), null);
    }

    @NonNull
    public Block getBlock() {
        return this.block;
    }

    public int getMetadata() {
        return this.metadata;
    }

    @NonNull
    public Optional<TileEntity> getTileEntity() {
        return Optional.ofNullable(this.tileEntity);
    }
}
