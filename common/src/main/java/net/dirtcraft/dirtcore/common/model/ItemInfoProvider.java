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

package net.dirtcraft.dirtcore.common.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an object that provides item information.
 */
public interface ItemInfoProvider {

    /**
     * Gets the identifier.
     *
     * @return the identifier
     */
    @NonNull String getIdentifier();

    /**
     * Gets the mod.
     *
     * @return the mod
     */
    @NonNull String getMod();

    /**
     * Gets the persistent data as a string.
     *
     * @return the persistent data
     */
    @Nullable String getPersistentDataAsString();

    /**
     * Checks if the provided persistent data matches.
     *
     * @param s the persistent data as a string
     * @return true, if it matches
     */
    boolean persistentDataMatches(@Nullable String s);

    /**
     * Checks if the provided persistent data partially matches.
     * Persistent data partially matches if all entries in the provided persistent data exist in
     * the persistent data of this instance and are equal.
     *
     * @param s the persistent data as a string
     * @return true, if it partially matches
     */
    boolean persistentDataPartiallyMatches(@NonNull String s);

    /**
     * Checks if this instance is empty.
     *
     * @return true, if empty
     */
    boolean isEmpty();

    /**
     * Gets the stack size.
     *
     * @return the stack size
     */
    default int getStackSize() {
        return 1;
    }

    /**
     * Formats this instance into a string.
     *
     * @param includePersistentData if the persistent data should be included.
     */
    @NonNull
    default String asString(final boolean includePersistentData) {
        final StringBuilder builder = new StringBuilder(this.getIdentifier());
        final String persistentData = this.getPersistentDataAsString();

        if (includePersistentData && persistentData != null) {
            builder.append(persistentData);
        }

        return builder.toString();
    }
}
