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

package net.dirtcraft.dirtcore.common.permission;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Handler for permission related queries.
 *
 * @param <S> the sender parameter
 */
public interface PermissionHandler<S> {

    /**
     * Checks if a sender has a permission.
     *
     * @param sender     the sender
     * @param permission the permission to check for
     * @return true, if the sender has the permission
     */
    boolean hasPermission(@NonNull S sender, @NonNull String permission);

    /**
     * Checks if a sender has a permission.
     *
     * @param uniqueId   the unique id of the sender
     * @param permission the permission to check for
     * @return true, if the sender has the permission
     */
    boolean hasPermission(@NonNull UUID uniqueId, @NonNull String permission);

    /**
     * Gets the weight of a permission group by its name.
     *
     * @param name the name of the group
     * @return the group weight
     */
    @NonNull OptionalInt getGroupWeightByName(@NonNull String name);

    /**
     * Checks if a sender is part of a group.
     *
     * @param uniqueId the unique id of the sender
     * @param name     the name of the group
     * @return true, if the sender is part of a group
     */
    boolean isPartOfGroup(@NonNull UUID uniqueId, @NonNull String name);

    /**
     * Gets the groups of a sender.
     *
     * @param uniqueId the unique id of the sender
     * @return the groups
     */
    @NonNull Collection<String> getGroups(@NonNull UUID uniqueId);
}
