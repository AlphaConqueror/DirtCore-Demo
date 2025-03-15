/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;

/**
 * Represents a sound to be played to players.
 */
public enum Sound {

    /**
     * Played when the CrateOpenGUI is closed.
     */
    CRATE_CLOSE,
    /**
     * Played when the CrateOpenGUI is opened.
     */
    CRATE_OPEN,
    /**
     * Played when the reward from CrateOpenGUI is given.
     */
    CRATE_REWARD,
    /**
     * Played when the {CrateOpenGUI is spun.
     */
    CRATE_SPIN,
    /**
     * Played when a player can not interact with a {@link Slot}.
     */
    GUI_FAILURE,
    /**
     * Played when a player interacts with a {@link Slot}.
     */
    GUI_SUCCESS
}
