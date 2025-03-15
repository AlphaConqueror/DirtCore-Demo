/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that has a unique identifier and a name.
 */
public interface Identifiable {

    /**
     * Gets the unique id.
     *
     * @return the unique id
     */
    @NonNull UUID getUniqueId();

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NonNull String getName();
}
