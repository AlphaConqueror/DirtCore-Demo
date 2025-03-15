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

import net.dirtcraft.dirtcore.common.model.gui.util.PaginationGUI;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;

/**
 * Represents a material to be used in {@link ItemStack}s and {@link Block}s.
 */
public enum Material {

    /**
     * Represents different channels in the ChannelSelectionGUI.
     */
    CHANNEL,
    /**
     * Represents something that is disabled.
     */
    DISABLED,
    /**
     * Represents something that is enabled.
     */
    ENABLED,
    /**
     * Used in the KitShowGUI to claim the viewed kit.
     */
    KIT_CLAIM,
    /**
     * Represents an empty slot in a {@link Container}.
     */
    MENU_EMPTY,
    /**
     * Represents the current page in a {@link PaginationGUI}.
     */
    PAGE_CURRENT,
    /**
     * Represents the next page in a {@link PaginationGUI}.
     */
    PAGE_NEXT,
    /**
     * Represents the previous page in a {@link PaginationGUI}.
     */
    PAGE_PREVIOUS,
    /**
     * Represents a channel in read mode in the ChannelSelectionGUI.
     */
    READ,
    /**
     * Represents the reward in the CrateOpenGUI.
     */
    REWARD_INDICATOR,
    /**
     * Represents a channel in write mode in the ChannelSelectionGUI.
     */
    WRITE
}
