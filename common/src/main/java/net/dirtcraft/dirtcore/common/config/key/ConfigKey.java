/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config.key;

import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;

/**
 * Represents a key in the configuration.
 *
 * @param <T> the value type
 */
public interface ConfigKey<T> {

    /**
     * Gets the position of this key within the keys enum.
     *
     * @return the position
     */
    int ordinal();

    /**
     * Sets the ordinal.
     *
     * @param ordinal the ordinal
     */
    void setOrdinal(final int ordinal);

    /**
     * Gets if the config key can be reloaded.
     *
     * @return if the key can be reloaded
     */
    boolean reloadable();

    /**
     * Sets the reloadable flag.
     *
     * @param reloadable if reloadable
     */
    void setReloadable(final boolean reloadable);

    /**
     * Resolves and returns the value mapped to this key using the given config instance.
     *
     * @param adapter the config adapter instance
     * @return the value mapped to this key
     */
    T get(ConfigurationAdapter adapter);
}
