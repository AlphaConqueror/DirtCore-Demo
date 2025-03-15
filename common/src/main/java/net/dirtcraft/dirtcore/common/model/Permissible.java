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

import java.util.Collection;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that hold permissions.
 */
public interface Permissible {

    /**
     * Checks if the Permissible has a permission.
     *
     * @param permission the permission to check for
     * @return true, if the permissible has the permission
     */
    boolean hasPermission(@NonNull String permission);

    /**
     * Checks if the Permissible is part of a group.
     *
     * @param name the name
     * @return true, if the permissible is part of the group
     */
    boolean isPartOfGroup(@NonNull String name);

    /**
     * Gets the groups of this permissible instance.
     *
     * @return the groups
     */
    @NonNull Collection<String> getGroups();

    /**
     * Checks if the Permissible has a permission.
     *
     * @param permission the permission to check for
     * @return true, if the permissible has the permission
     */
    default boolean hasPermission(final Permission permission) {
        return permission == Permission.NONE || this.hasPermission(permission.getPermission());
    }

    /**
     * Checks if the sender punishment exempt.
     *
     * @return true, if exempt
     */
    default boolean isPunishmentExempt() {
        return this.hasPermission(Permission.PUNISHMENT_EXEMPT);
    }
}
