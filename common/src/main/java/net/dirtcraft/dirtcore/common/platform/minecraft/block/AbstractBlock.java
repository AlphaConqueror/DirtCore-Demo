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

package net.dirtcraft.dirtcore.common.platform.minecraft.block;

import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple implementation of {@link Block} using a {@link BlockFactory}.
 *
 * @param <B> the block type
 */
public final class AbstractBlock<B> implements Block {

    private final BlockFactory<?, B> factory;
    private final B block;

    public AbstractBlock(final BlockFactory<?, B> factory, final B block) {
        this.factory = factory;
        this.block = block;
    }

    @Override
    public @NonNull String getMod() {
        return this.factory.getMod(this.block);
    }

    @Override
    public @Nullable String getPersistentDataAsString() {
        return this.factory.getPersistentDataAsString(this.block);
    }

    @Override
    public boolean persistentDataMatches(@Nullable final String s) {
        return this.factory.persistentDataMatches(this.block, s);
    }

    @Override
    public boolean persistentDataPartiallyMatches(@NonNull final String s) {
        return this.factory.persistentDataPartiallyMatches(this.block, s);
    }

    @Override
    public @NonNull String getIdentifier() {
        return this.factory.getIdentifier(this.block);
    }

    @Override
    public boolean isEmpty() {
        return this.factory.isEmpty(this.block);
    }

    @Override
    public boolean hasPersistentData() {
        return this.factory.hasPersistentData(this.block);
    }
}
