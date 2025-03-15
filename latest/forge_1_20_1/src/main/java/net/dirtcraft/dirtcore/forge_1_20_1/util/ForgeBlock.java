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
