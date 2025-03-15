/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
