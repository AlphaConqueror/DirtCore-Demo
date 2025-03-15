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

package net.dirtcraft.dirtcore.common.model.minecraft.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;

/**
 * Wrapper interface to represent an item stack within the minecraft item stack implementations.
 */
public interface ItemStack extends ItemLike, ItemInfoProvider {


    /**
     * Creates a builder.
     *
     * @param material the material
     * @return the builder
     */
    @NonNull
    static Builder builder(@NonNull final Material material) {
        return new SimpleItemStack.Builder(material);
    }

    /**
     * Creates a builder.
     *
     * @param material  the material
     * @param stackSize the stack size
     * @return the builder
     */
    @NonNull
    static Builder builder(@NonNull final Material material, final int stackSize) {
        return new SimpleItemStack.Builder(material, stackSize);
    }

    /**
     * Sets the lore.
     *
     * @param lore the lore
     */
    void setLore(@NonNull List<Component> lore);

    /**
     * Appends lore.
     *
     * @param loreCollection the additional lore
     */
    void appendLore(@NonNull Collection<Component> loreCollection);

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    @NonNull Component getDisplayName();

    /**
     * Sets the stack size.
     *
     * @param stackSize the stack size
     */
    void setStackSize(int stackSize);

    /**
     * Checks if this item stack has persistent data.
     *
     * @return true, if it has persistent data
     */
    boolean hasPersistentData();

    /**
     * Checks if this item stack matches another item stack
     * in terms of item stack type and  persistent data.
     *
     * @param other the other item stack
     * @return true, if the item stacks match
     */
    boolean isSameItemSamePersistentData(@NonNull ItemStack other);

    /**
     * Checks if this item stack can be transformed into a block.
     *
     * @return true, if it can be transformed into a block
     */
    boolean isBlock();

    /**
     * Gets this item stack as a block.
     *
     * @return the block, if available
     */
    @NonNull Optional<Block> getAsBlock();

    /**
     * Makes a copy of this item stack.
     *
     * @return the copy
     */
    @NonNull ItemStack copy();

    default void appendLore(@NonNull final Component lore,
            @NonNull final Component... loreCollection) {
        final List<Component> components = new ArrayList<>();

        components.add(lore);
        components.addAll(Arrays.asList(loreCollection));
        this.appendLore(components);
    }

    @Override
    @NonNull
    default ItemStack build(@NonNull final DirtCorePlugin plugin) {
        return this;
    }

    @NonNull
    default Component asDisplayComponent(final boolean includeCount) {
        final TextComponent.Builder builder = Component.text();

        if (includeCount) {
            builder.append(Component.text(this.getStackSize()))
                    .append(Component.text("x "));
        }

        return builder.append(this.getDisplayName()).build();
    }

    /**
     * The item like builder.
     */
    interface Builder {

        /**
         * Builds the item like.
         *
         * @return the item like
         */
        @NonNull ItemLike build();

        /**
         * Provides the display name.
         *
         * @param displayName the display name
         * @return the builder
         */
        @CheckReturnValue
        @NonNull Builder displayName(@NonNull Component displayName);

        /**
         * Provides additional lore.
         *
         * @param lore the lore
         * @return the builder
         */
        @CheckReturnValue
        @NonNull Builder appendLore(@NonNull Component lore);
    }

    /**
     * An EMPTY item stack.
     */
    ItemStack EMPTY = new ItemStack() {
        @Override
        public void setLore(@NonNull final List<Component> lore) {}

        @Override
        public void appendLore(@NonNull final Collection<Component> loreCollection) {}

        @Override
        public @NonNull Component getDisplayName() {
            return Component.empty();
        }

        @Override
        public int getStackSize() {
            return 0;
        }

        @Override
        public void setStackSize(final int stackSize) {}

        @Override
        public boolean hasPersistentData() {
            return false;
        }

        @Override
        public boolean isSameItemSamePersistentData(@NonNull final ItemStack other) {
            return Objects.equals(this, other);
        }

        @Override
        public boolean isBlock() {
            return false;
        }

        @Override
        public @NonNull Optional<Block> getAsBlock() {
            return Optional.empty();
        }

        @Override
        public @NonNull ItemStack copy() {
            return EMPTY;
        }

        @Override
        public @NonNull String getIdentifier() {
            return "";
        }

        @Override
        public @NonNull String getMod() {
            return "";
        }

        @Override
        public @Nullable String getPersistentDataAsString() {
            return null;
        }

        @Override
        public boolean persistentDataMatches(@Nullable final String s) {
            return false;
        }

        @Override
        public boolean persistentDataPartiallyMatches(@NonNull final String s) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    };
}
