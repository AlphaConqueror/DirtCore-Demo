/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.model.user;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A player which holds permission data.
 */
public interface User {

    /**
     * Gets the users unique ID
     *
     * @return the users Mojang assigned unique id
     */
    @NonNull UUID getUniqueId();

    /**
     * Gets the users username
     *
     * <p>Returns null if no username is known for the user.</p>
     *
     * @return the users username
     */
    @Nullable String getUsername();
}
