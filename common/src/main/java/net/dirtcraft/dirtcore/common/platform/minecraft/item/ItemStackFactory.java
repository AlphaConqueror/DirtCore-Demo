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

package net.dirtcraft.dirtcore.common.platform.minecraft.item;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory class to make a thread-safe item stack instance.
 *
 * @param <P> the plugin type
 * @param <I> the item stack type
 */
public abstract class ItemStackFactory<P extends DirtCorePlugin, I> {

    private final P plugin;

    public ItemStackFactory(final P plugin) {this.plugin = plugin;}

    protected abstract @NonNull Optional<I> createItemStack(@NonNull final String identifier,
            final int count, @Nullable final String persistentData);

    protected abstract @NonNull Component getDisplayName(@NonNull I itemStack);

    protected abstract @NonNull I transform(@NonNull ItemStack itemStack);

    protected abstract @NonNull ItemStack transform(@NonNull SimpleItemStack itemStack);

    protected abstract void setDisplayName(@NonNull I itemStack, @NonNull Component displayName);

    protected abstract int getStackSize(@NonNull I itemStack);

    protected abstract void setStackSize(@NonNull I itemStack, int stackSize);

    protected abstract @NonNull String getIdentifier(@NonNull I itemStack);

    protected abstract @NonNull String getMod(@NonNull I itemStack);

    protected abstract void appendLore(@NonNull I itemStack,
            @NonNull Collection<Component> loreCollection);

    protected abstract void setLore(@NonNull I itemStack, @NonNull List<Component> loreList);

    protected abstract boolean hasPersistentData(@NonNull I itemStack);

    protected abstract @Nullable String getPersistentDataAsString(@NonNull I itemStack);

    protected abstract boolean isSameItemSamePersistentData(@NonNull I itemStack,
            @NonNull ItemStack other);

    protected abstract boolean persistentDataPartiallyMatches(@NonNull I itemStack,
            @NonNull final String s);

    protected abstract boolean persistentDataMatches(@NonNull I itemStack,
            @Nullable final String s);

    protected abstract boolean isBlock(@NonNull I itemStack);

    protected abstract boolean isEmpty(@NonNull I itemStack);

    protected abstract Optional<Block> getAsBlock(@NonNull I itemStack);

    @NonNull
    protected abstract I copy(@NonNull I itemStack);

    public final ItemStack wrap(final I itemStack) {
        Objects.requireNonNull(itemStack, "itemStack");
        return new AbstractItemStack<>(this, itemStack);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
