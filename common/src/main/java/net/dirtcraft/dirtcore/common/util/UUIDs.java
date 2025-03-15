/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import java.util.UUID;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Utilities for working with {@link UUID}s.
 */
public interface UUIDs {

    static @Nullable UUID fromString(final String s) {
        try {
            return UUID.fromString(s);
        } catch (final IllegalArgumentException e) {
            return null;
        }
    }

    static @Nullable UUID parse(final String s) {
        UUID uuid = fromString(s);

        if (uuid == null && s.length() == 32) {
            try {
                uuid = new UUID(Long.parseUnsignedLong(s.substring(0, 16), 16),
                        Long.parseUnsignedLong(s.substring(16), 16));
            } catch (final NumberFormatException ignore) {}
        }

        return uuid;
    }
}
