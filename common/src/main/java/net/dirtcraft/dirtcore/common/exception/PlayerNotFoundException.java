/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.exception;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(@NonNull final UUID uniqueId) {
        super("Could not find player with id '" + uniqueId + "'.");
    }
}
