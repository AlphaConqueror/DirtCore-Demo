/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
