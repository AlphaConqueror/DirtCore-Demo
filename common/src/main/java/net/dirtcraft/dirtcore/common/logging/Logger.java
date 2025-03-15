/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.logging;

import net.dirtcraft.storageutils.logging.LoggerAdapter;

/**
 * Represents a logger instance.
 * <p>
 * Functions use '{}' as a placeholder for arguments.
 */
public interface Logger extends LoggerAdapter {

    @Override
    void info(String s, Object... args);

    @Override
    void warn(String s, Object... args);

    @Override
    void severe(String s, Object... args);

    @Override
    void severe(String s, Throwable t);
}
