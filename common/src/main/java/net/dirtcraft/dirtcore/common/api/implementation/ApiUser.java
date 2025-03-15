/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.api.implementation;

import com.google.common.base.Preconditions;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ApiUser implements net.dirtcraft.dirtcore.api.model.user.User {

    private final User handle;

    public ApiUser(final User handle) {
        this.handle = handle;
    }

    public static User cast(final net.dirtcraft.dirtcore.api.model.user.User u) {
        Preconditions.checkState(u instanceof ApiUser,
                "Illegal instance " + u.getClass() + " cannot be handled by this implementation.");
        return ((ApiUser) u).getHandle();
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.handle.getUniqueId();
    }

    @Override
    public String getUsername() {
        return this.handle.getName();
    }

    @Override
    public int hashCode() {
        return this.handle.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ApiUser)) {
            return false;
        }

        final ApiUser that = (ApiUser) o;
        return this.handle.equals(that.handle);
    }

    User getHandle() {
        return this.handle;
    }
}
