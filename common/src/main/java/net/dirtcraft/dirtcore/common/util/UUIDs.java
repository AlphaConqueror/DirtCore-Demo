/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
