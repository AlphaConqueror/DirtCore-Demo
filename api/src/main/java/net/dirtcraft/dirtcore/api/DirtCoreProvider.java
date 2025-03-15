/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api;

import org.checkerframework.checker.nullness.qual.NonNull;

import static org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Provides static access to the {@link DirtCore} API.
 *
 * <p>Ideally, the ServiceManager for the platform should be used to obtain an
 * instance, however, this provider can be used if this is not viable.</p>
 */
public final class DirtCoreProvider {

    private static DirtCore instance = null;

    @Internal
    private DirtCoreProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

    /**
     * Gets an instance of the {@link DirtCore} API,
     * throwing {@link IllegalStateException} if the API is not loaded yet.
     *
     * <p>This method will never return null.</p>
     *
     * @return an instance of the DirtCore API
     * @throws IllegalStateException if the API is not loaded yet
     */
    public static @NonNull DirtCore get() {
        final DirtCore instance = DirtCoreProvider.instance;

        if (instance == null) {
            throw new NotLoadedException();
        }

        return instance;
    }

    @Internal
    static void register(final DirtCore instance) {
        DirtCoreProvider.instance = instance;
    }

    @Internal
    static void unregister() {
        DirtCoreProvider.instance = null;
    }

    /**
     * Exception thrown when the API is requested before it has been loaded.
     */
    private static final class NotLoadedException extends IllegalStateException {

        private static final String MESSAGE = "The DirtCore API has not been loaded yet!\n";

        NotLoadedException() {
            super(MESSAGE);
        }
    }
}
