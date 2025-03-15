/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.permission;

import java.util.Collection;
import java.util.Collections;
import java.util.OptionalInt;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractVanillaPermissionHandler<S> implements PermissionHandler<S> {

    protected abstract boolean isConsole(final UUID uniqueId);

    protected abstract boolean isOp(final UUID uniqueId);

    @NonNull
    protected abstract UUID getUniqueId(final S sender);

    @Override
    public boolean hasPermission(@NonNull final S sender, @NonNull final String permission) {
        return this.hasPermission(this.getUniqueId(sender), permission);
    }

    @Override
    public boolean hasPermission(@NonNull final UUID uniqueId, @NonNull final String permission) {
        return this.isConsole(uniqueId) || this.isOp(uniqueId);
    }

    @Override
    public @NonNull OptionalInt getGroupWeightByName(@NonNull final String name) {
        return OptionalInt.empty();
    }

    @Override
    public boolean isPartOfGroup(@NonNull final UUID uniqueId, @NonNull final String name) {
        return false;
    }

    @Override
    public @NonNull Collection<String> getGroups(@NonNull final UUID uniqueId) {
        return Collections.emptySet();
    }
}
