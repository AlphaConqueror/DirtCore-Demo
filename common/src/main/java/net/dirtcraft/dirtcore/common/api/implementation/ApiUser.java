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
