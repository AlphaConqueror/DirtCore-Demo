/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.item;

import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that can be built into an {@link ItemStack}.
 */
public interface ItemLike {

    /**
     * Builds an item stack.
     *
     * @param plugin the plugin
     * @return the item stack
     */
    @NonNull ItemStack build(@NonNull DirtCorePlugin plugin);

    /**
     * Checks if this item like is empty.
     *
     * @return true, if empty
     */
    boolean isEmpty();
}
