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

package net.dirtcraft.dirtcore.common.model.manager.user;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.player.PlayerDataEntity;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserIPHistory;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserSettingsEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages user related operations in a thread-safe manner.
 *
 * @param <T> the user type
 */
public interface UserManager<T extends User> {

    /**
     * Creates or updates a user.
     *
     * @param context  the task context
     * @param uniqueId the unique id
     * @param username the username
     */
    void createOrUpdateUser(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String username);

    /**
     * Gets user unique id mapped to their username.
     *
     * @return the user map
     */
    @NonNull Map<@NonNull UUID, @NonNull String> getUserMap();

    /**
     * Gets a user object by unique id.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link User} object or null.
     */
    @Nullable T getUser(@NonNull TaskContext context, @NonNull UUID uniqueId);

    /**
     * Gets a user object by unique id or creates it if not found.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link User} object or the new user.
     */
    @NonNull T getOrCreateUser(@NonNull TaskContext context, @NonNull UUID uniqueId);

    /**
     * Gets a user object by username.
     *
     * @param context  the task context
     * @param username the username to search by
     * @return a {@link User} object or null.
     */
    @Nullable T getUser(@NonNull TaskContext context, @NonNull String username);

    /**
     * Gets multiple user objects by their unique id.
     *
     * @param context   the task context
     * @param uniqueIds the unique ids to search by
     * @return a collection of {@link User} objects
     */
    @NonNull List<T> getUsers(@NonNull TaskContext context, @NonNull Collection<UUID> uniqueIds);

    /**
     * Creates the user settings.
     *
     * @param context  the task context
     * @param uniqueId the unique id
     */
    void createUserSettingsIfNotExisting(@NonNull TaskContext context, @NonNull UUID uniqueId);

    /**
     * Gets a user settings object by unique id.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link UserSettingsEntity} object, if available
     */
    @NonNull Optional<UserSettingsEntity> getUserSettings(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets a user settings object by unique id or creates it if not found.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link UserSettingsEntity} object or the new user settings.
     */
    @NonNull UserSettingsEntity getOrCreateUserSettings(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets the IP history list of a user by unique id.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the user IP history list
     */
    @NonNull List<UserIPHistory> getUserIPHistory(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets the user IP history by IP address.
     *
     * @param context   the context
     * @param uniqueId  the unique id
     * @param ipAddress the IP address
     * @return the user IP history
     */
    @Nullable UserIPHistory getUserIPHistoryByIP(@NonNull TaskContext context,
            @NonNull UUID uniqueId, @NonNull String ipAddress);

    /**
     * Gets all users with a specific IP history entry by IP address.
     *
     * @param context   the context
     * @param ipAddress the IP address
     * @return the users
     */
    @NonNull List<User> getUsersByIP(@NonNull TaskContext context, @NonNull String ipAddress);

    /**
     * Gets all users with IP history entries matching the specified IP addresses.
     *
     * @param context     the context
     * @param ipAddresses the IP addresses
     * @return the users
     */
    @NonNull List<User> getUsersByIPs(@NonNull TaskContext context,
            @NonNull Collection<String> ipAddresses);

    /**
     * Gets all users with IP history entries matching the specified IP addresses
     * and unique ids.
     *
     * @param context     the context
     * @param ipAddresses the IP addresses
     * @param uniqueIds   the unique ids
     * @return the users
     */
    @NonNull List<User> getUsersByIPsFiltered(@NonNull TaskContext context,
            @NonNull Collection<String> ipAddresses, @NonNull Collection<UUID> uniqueIds);

    /**
     * Creates player data for a unique id.
     *
     * @param context  the task context
     * @param uniqueId the unique id
     */
    void createPlayerDataIfNotExisting(@NonNull TaskContext context, @NonNull UUID uniqueId);

    /**
     * Gets a player data object by unique id.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link PlayerDataEntity} object, if available
     */
    @NonNull Optional<PlayerDataEntity> getPlayerData(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets a player data object by unique id or creates it, if not found.
     *
     * @param context  the task context
     * @param uniqueId the unique id to search by
     * @return a {@link PlayerDataEntity} object or the new player data.
     */
    @NonNull PlayerDataEntity getOrCreatePlayerData(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets the names of users who have player data on the specified server.
     *
     * @param context the context
     * @param server  the server
     * @return the names
     */
    @NonNull List<String> getUserNamesByServer(@NonNull TaskContext context,
            @NonNull String server);
}
