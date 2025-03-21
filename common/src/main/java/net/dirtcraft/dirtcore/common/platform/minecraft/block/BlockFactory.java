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

import java.util.Objects;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory class to make a thread-safe block instance.
 *
 * @param <P> the plugin type
 * @param <B> the block type
 */
public abstract class BlockFactory<P extends DirtCorePlugin, B> {

    private final P plugin;

    public BlockFactory(final P plugin) {this.plugin = plugin;}

    @NonNull
    protected abstract String getIdentifier(@NonNull B block);

    @NonNull
    protected abstract String getMod(@NonNull B block);

    protected abstract boolean isEmpty(@NonNull B block);

    protected abstract boolean hasPersistentData(@NonNull B block);

    @Nullable
    protected abstract String getPersistentDataAsString(@NonNull B block);

    protected abstract boolean persistentDataMatches(@NonNull B block, @Nullable final String s);

    protected abstract boolean persistentDataPartiallyMatches(@NonNull B block, @NonNull String s);

    public final Block wrap(final B block) {
        Objects.requireNonNull(block, "block");
        return new AbstractBlock<>(this, block);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
