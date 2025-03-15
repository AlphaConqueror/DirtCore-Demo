/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
