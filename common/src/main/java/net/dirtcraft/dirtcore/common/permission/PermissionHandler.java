/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
