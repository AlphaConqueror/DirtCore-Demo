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
