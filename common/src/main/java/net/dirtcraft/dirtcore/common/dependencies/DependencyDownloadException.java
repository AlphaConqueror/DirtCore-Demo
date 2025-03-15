/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.dependencies;

/**
 * Exception thrown if a dependency cannot be downloaded.
 */
public class DependencyDownloadException extends Exception {

    public DependencyDownloadException() {

    }

    public DependencyDownloadException(final String message) {
        super(message);
    }

    public DependencyDownloadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public DependencyDownloadException(final Throwable cause) {
        super(cause);
    }
}
