/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.loader;

/**
 * Runtime exception used if there is a problem during loading
 */
public class LoadingException extends RuntimeException {

    public LoadingException(final String message) {
        super(message);
    }

    public LoadingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
