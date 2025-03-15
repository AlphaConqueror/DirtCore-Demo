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

package net.dirtcraft.dirtcore.common.model.manager.chat;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.Permissible;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.chat.ChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.UnlockedChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.UnlockedPrefixEntity;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages chat related operations in a thread-safe manner.
 */
public interface ChatManager {

    // TODO: Move to config.
    /**
     * The default chat marker.
     */
    String DEFAULT_CHAT_MARKER = "»";

    /**
     * Registers a chat marker.
     *
     * @param context     the context
     * @param name        the name
     * @param description the description
     * @param display     the display in {@link MiniMessage} format
     */
    void registerChatMarker(@NonNull TaskContext context, @NonNull String name,
            @NonNull String description, @NonNull String display);

    /**
     * Deletes a chat marker.
     * Consequently, all related {@link UnlockedChatMarkerEntity} objects will be deleted.
     *
     * @param context    the context
     * @param chatMarker the chat marker
     */
    void deleteChatMarker(@NonNull TaskContext context, @NonNull ChatMarkerEntity chatMarker);

    /**
     * Edits a chat marker.
     *
     * @param context     the context
     * @param original    the original chat marker
     * @param name        the name
     * @param description the description
     * @param display     the display in {@link MiniMessage} format
     */
    void editChatMarker(@NonNull TaskContext context, @NonNull ChatMarkerEntity original,
            @Nullable String name, @Nullable String description, @Nullable String display);

    /**
     * Grants a chat marker.
     *
     * @param context    the context
     * @param uniqueId   the unique id of the granted
     * @param prefixName the prefix name
     */
    void grantChatMarker(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String prefixName);

    /**
     * Revokes a chat marker.
     *
     * @param context    the context
     * @param uniqueId   the unique id of the revoked
     * @param markerName the marker name
     */
    void revokeChatMarker(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String markerName);

    /**
     * Gets the unlocked chat markers via unique id.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the chat markers
     */
    @NonNull Collection<ChatMarkerEntity> getUnlockedChatMarkers(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all accessible chat markers a user has access to via enough permission.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the chat markers
     */
    @NonNull Collection<ChatMarkerEntity> getPermissibleChatMarkers(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all available chat markers.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the chat markers
     */
    @NonNull Map<ChatMarkerEntity, Boolean> getAvailableChatMarkers(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all chat marker names.
     *
     * @param context the context
     * @return the chat marker names
     */
    @NonNull Collection<String> getChatMarkerNames(@NonNull TaskContext context);

    /**
     * Checks if a chat marker exists.
     *
     * @param context    the context
     * @param markerName the chat marker name
     * @return true, if it exists
     */
    boolean chatMarkerExists(@NonNull TaskContext context, @NonNull String markerName);

    /**
     * Checks if a user has unlocked a chat marker.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param markerName the marker name
     * @return true, if unlocked
     */
    boolean hasChatMarkerUnlocked(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String markerName);

    /**
     * Gets the names of all unlocked chat markers.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the chat marker names
     */
    @NonNull Collection<String> getUnlockedChatMarkerNames(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Checks if a chat marker is available for a user.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param markerName the marker name
     * @return true, if available
     */
    boolean isChatMarkerAvailable(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String markerName);

    /**
     * Gets all available chat marker names.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the chat marker names
     */
    @NonNull Collection<String> getAvailableChatMarkerNames(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Registers a prefix.
     *
     * @param context     the context
     * @param name        the name
     * @param description the description
     * @param display     the display in {@link MiniMessage} format
     */
    void registerPrefix(@NonNull TaskContext context, @NonNull String name,
            @NonNull String description, @NonNull String display);

    /**
     * Deletes a prefix.
     * Consequently, all related {@link UnlockedPrefixEntity} objects will be deleted.
     *
     * @param context the context
     * @param prefix  the prefix
     */
    void deletePrefix(@NonNull TaskContext context, @NonNull PrefixEntity prefix);

    /**
     * Edits a prefix.
     *
     * @param context     the context
     * @param original    the original
     * @param name        the name
     * @param description the description
     * @param display     the display in {@link MiniMessage} format
     */
    void editPrefix(@NonNull TaskContext context, @NonNull PrefixEntity original,
            @Nullable String name, @Nullable String description, @Nullable String display);

    /**
     * Grants a prefix.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param prefixName the prefix name
     */
    void grantPrefix(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String prefixName);

    /**
     * Revokes a prefix.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param prefixName the prefix name
     */
    void revokePrefix(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String prefixName);

    /**
     * Gets the unlocked prefixes via unique id.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the prefixes
     */
    @NonNull Collection<PrefixEntity> getUnlockedPrefixes(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all accessible prefixes a user has access to via enough permission.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the prefixes
     */
    @NonNull Collection<PrefixEntity> getPermissiblePrefixes(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all available prefixes.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the prefixes
     */
    @NonNull Map<PrefixEntity, Boolean> getAvailablePrefixes(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets all prefix names.
     *
     * @param context the context
     * @return the prefix names
     */
    @NonNull Collection<String> getPrefixNames(@NonNull TaskContext context);

    /**
     * Checks if a prefix exists.
     *
     * @param context    the context
     * @param prefixName the prefix name
     * @return true, if it exists
     */
    boolean prefixExists(@NonNull TaskContext context, @NonNull String prefixName);

    /**
     * Checks if a user has unlocked a prefix.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param prefixName the prefix name
     * @return true, if unlocked
     */
    boolean hasPrefixUnlocked(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String prefixName);

    /**
     * Gets the names of all unlocked chat markers.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the prefix names
     */
    @NonNull Collection<String> getUnlockedPrefixNames(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Checks if a prefix is available for a user.
     *
     * @param context    the context
     * @param uniqueId   the unique id
     * @param prefixName the prefix name
     * @return true, if available
     */
    boolean isPrefixAvailable(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String prefixName);

    /**
     * Gets all available prefix names.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the prefix names
     */
    @NonNull Collection<String> getAvailablePrefixNames(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Registers a staff prefix.
     *
     * @param context      the context
     * @param name         the name
     * @param fullName     the full name
     * @param fullDisplay  the full display in {@link MiniMessage} format
     * @param shortDisplay the short display in {@link MiniMessage} format
     */
    void registerStaffPrefix(@NonNull TaskContext context, @NonNull String name,
            @NonNull String fullName, @NonNull String fullDisplay, @NonNull String shortDisplay);

    /**
     * Deletes a staff prefix.
     *
     * @param context the context
     * @param prefix  the staff prefix
     */
    void deleteStaffPrefix(@NonNull TaskContext context, @NonNull StaffPrefixEntity prefix);

    /**
     * Edits a staff prefix.
     *
     * @param context      the context
     * @param original     the original
     * @param name         the name
     * @param fullName     the full name
     * @param fullDisplay  the full display in {@link MiniMessage} format
     * @param shortDisplay the short display in {@link MiniMessage} format
     */
    void editStaffPrefix(@NonNull TaskContext context, @NonNull StaffPrefixEntity original,
            @Nullable String name, @Nullable String fullName, @Nullable String fullDisplay,
            @Nullable String shortDisplay);

    /**
     * Gets all staff prefix names.
     *
     * @return the staff prefix names
     */
    @NonNull List<String> getStaffPrefixNames();

    /**
     * Checks if a staff prefix exists.
     *
     * @param context    the context
     * @param prefixName the staff prefix name
     * @return true, if it exists
     */
    boolean staffPrefixExists(@NonNull TaskContext context, @NonNull String prefixName);

    /**
     * Gets a staff prefix.
     *
     * @param context     the context
     * @param permissible the permissible
     * @return the staff prefix, if available
     */
    @NonNull Optional<StaffPrefixEntity> getStaffPrefix(@NonNull TaskContext context,
            @NonNull Permissible permissible);

    /**
     * Gets all staff prefixes ordered by their assigned weight.
     * The weight is determined by the permission handler in use.
     *
     * @param context     the context
     * @param permissible the permissible
     * @return the staff prefixes
     */
    @NonNull List<StaffPrefixEntity> getStaffPrefixesOrdered(@NonNull TaskContext context,
            @NonNull Permissible permissible);
}
