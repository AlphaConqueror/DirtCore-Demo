/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that can be limited.
 */
public interface Limitable {

    /**
     * Gets the identifier.
     *
     * @return the identifier
     */
    @NonNull String getIdentifier();

    /**
     * Checks if this instance is empty.
     *
     * @return true, if empty
     */
    boolean isEmpty();
}
