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
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.query.Flag;
import net.luckperms.api.query.QueryOptions;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractLPPermissionHandler<S> implements PermissionHandler<S> {

    private final LuckPerms luckPerms;

    public AbstractLPPermissionHandler() {
        this.luckPerms = LuckPermsProvider.get();
    }

    @NonNull
    protected abstract UUID getUUID(final S sender);

    protected abstract boolean isConsole(final S sender);

    @Override
    public boolean hasPermission(@NonNull final S sender, @NonNull final String permission) {
        return this.isConsole(sender) || this.getUser(this.getUUID(sender)).getCachedData()
                .getPermissionData().checkPermission(permission).asBoolean();
    }

    @Override
    public boolean hasPermission(@NonNull final UUID uniqueId, @NonNull final String permission) {
        return uniqueId.equals(Sender.CONSOLE_UUID) || this.getUser(uniqueId).getCachedData()
                .getPermissionData().checkPermission(permission).asBoolean();
    }

    @Override
    public @NonNull OptionalInt getGroupWeightByName(@NonNull final String name) {
        return this.getGroup(name).map(group -> {
            final OptionalInt weightOptional = group.getWeight();
            // return the weight if present or default to 0
            return weightOptional.isPresent() ? weightOptional : OptionalInt.of(0);
        }).orElse(OptionalInt.empty());
    }

    @Override
    public boolean isPartOfGroup(@NonNull final UUID uniqueId, @NonNull final String name) {
        return this.getGroups(uniqueId).stream().anyMatch(group -> group.equals(name));
    }

    @Override
    public @NonNull Collection<String> getGroups(@NonNull final UUID uniqueId) {
        final User user = this.getUser(uniqueId);
        // gets the directly inherited groups; we can override the context, since it is empty
        return user.getInheritedGroups(QueryOptions.defaultContextualOptions().toBuilder()
                        .context(this.luckPerms.getContextManager().getStaticContext())
                        .flag(Flag.RESOLVE_INHERITANCE, false).build()).stream().map(Group::getName)
                .collect(Collectors.toSet());
    }

    @NonNull
    protected Optional<Group> getGroup(@NonNull final String name) {
        final Group group = this.luckPerms.getGroupManager().getGroup(name);

        if (group == null) {
            return this.luckPerms.getGroupManager().loadGroup(name).join();
        }

        return Optional.of(group);
    }

    protected User getUser(final UUID uniqueId) {
        final UserManager userManager = this.luckPerms.getUserManager();
        User user = userManager.getUser(uniqueId);

        if (user == null) {
            user = userManager.loadUser(uniqueId).join();
        }

        return user;
    }
}
