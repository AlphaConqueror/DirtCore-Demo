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

package net.dirtcraft.dirtcore.common.model.manager.messaging;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import net.dirtcraft.dirtcore.api.messenger.message.type.NetworkChatMessage;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserSettingsEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages limit related operations in a thread-safe manner.
 */
public interface MessagingManager {

    // TODO: Move to config.
    /**
     * The default join message.
     */
    String DEFAULT_JOIN_MESSAGE = "joined the game!";
    /**
     * The default leave message.
     */
    String DEFAULT_LEAVE_MESSAGE = "left the game!";
    /**
     * The default message separator.
     */
    String MESSAGE_SEPARATOR = ": ";
    /**
     * The {@link MiniMessage} instance.
     */
    MiniMessage MINIMESSAGE = MiniMessage.miniMessage();
    /**
     * The pattern used to strip the formatting from a {@link MiniMessage} string.
     */
    Pattern MINIMESSAGE_STRIP_PATTERN = Pattern.compile("<obf>[^<]+</obf>");

    /**
     * Transforms a {@link MiniMessage} string to an unformatted string.
     *
     * @param s the string
     * @return the unformatted string
     */
    @NonNull
    static String minimessageToUnformattedString(@NonNull final String s) {
        final String stripped = MINIMESSAGE_STRIP_PATTERN.matcher(s).replaceAll("");
        return MINIMESSAGE.stripTags(stripped);
    }

    /**
     * Handles a message.
     *
     * @param uniqueId the unique id
     * @param message  the message
     */
    void handleMessage(@NonNull UUID uniqueId, @NonNull String message);

    /**
     * Handles a remote message.
     *
     * @param networkChatMessage the network chat message
     */
    void handleMessageFromRemote(@NonNull NetworkChatMessage networkChatMessage);

    /**
     * Handles a local chat message.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @param message  the message
     */
    void handleLocal(@NonNull TaskContext context, @NonNull UUID uniqueId, @NonNull String message);

    /**
     * Handles a global chat message.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @param message  the message
     */
    void handleGlobal(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String message);

    /**
     * Handles a local staff chat message.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @param message  the message
     */
    void handleStaffLocal(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String message);

    /**
     * Handles a global staff chat message.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @param message  the message
     */
    void handleStaffGlobal(@NonNull TaskContext context, @NonNull UUID uniqueId,
            @NonNull String message);

    /**
     * Changes the read channel.
     *
     * @param context      the context
     * @param userSettings the user settings
     * @param channel      the new read channel
     * @param flag         true, if the channel is to be enabled, false, if otherwise
     */
    void changeReadChannel(@NonNull TaskContext context, @NonNull UserSettingsEntity userSettings,
            @NonNull ChannelType channel, boolean flag);

    /**
     * Sets the write channel.
     *
     * @param context      the context
     * @param userSettings the user settings
     * @param channel      the new write channel
     * @return true, if the channel has been changed
     */
    boolean setWriteChannel(@NonNull TaskContext context, @NonNull UserSettingsEntity userSettings,
            @NonNull ChannelType channel);

    /**
     * Gets last message receiver to reply to.
     *
     * @param uniqueId the sender unique id
     * @return the last message receiver or null
     */
    @Nullable UUID getLastMessageReceiver(@NonNull UUID uniqueId);

    /**
     * Acknowledges a message.
     *
     * @param senderUniqueId   the sender unique id
     * @param receiverUniqueId the receiver unique id
     */
    void acknowledgeMessaging(@NonNull UUID senderUniqueId, @NonNull UUID receiverUniqueId);

    /**
     * Gets the formatted join message of a user.
     *
     * @param context the context
     * @param user    the user
     * @return the formatted join message
     */
    @NonNull Optional<Component> getJoinMessageFormatted(@NonNull TaskContext context,
            @NonNull User user);

    /**
     * Gets the unformatted join message of a user.
     *
     * @param context the context
     * @param user    the user
     * @return the unformatted join message
     */
    @NonNull Optional<String> getJoinMessageUnformatted(@NonNull TaskContext context,
            @NonNull User user);

    /**
     * Gets the formatted leave message of a user.
     *
     * @param context the context
     * @param user    the user
     * @return the formatted leave message
     */
    @NonNull Optional<Component> getLeaveMessageFormatted(@NonNull TaskContext context,
            @NonNull User user);

    /**
     * Gets the unformatted leave message of a user.
     *
     * @param context the context
     * @param user    the user
     * @return the unformatted leave message
     */
    @NonNull Optional<String> getLeaveMessageUnformatted(@NonNull TaskContext context,
            @NonNull User user);

    default @NonNull Component getJoinMessageFormattedOrDefault(@NonNull final TaskContext context,
            @NonNull final User user) {
        return this.getJoinMessageFormatted(context, user)
                .orElse(Component.text(DEFAULT_JOIN_MESSAGE, NamedTextColor.GRAY));
    }

    default @NonNull String getJoinMessageUnformattedOrDefault(@NonNull final TaskContext context,
            @NonNull final User user) {
        return this.getJoinMessageUnformatted(context, user).orElse(DEFAULT_JOIN_MESSAGE);
    }

    default @NonNull Component getLeaveMessageFormattedOrDefault(@NonNull final TaskContext context,
            @NonNull final User user) {
        return this.getLeaveMessageFormatted(context, user)
                .orElse(Component.text(DEFAULT_LEAVE_MESSAGE, NamedTextColor.GRAY));
    }

    default @NonNull String getLeaveMessageUnformattedOrDefault(@NonNull final TaskContext context,
            @NonNull final User user) {
        return this.getLeaveMessageUnformatted(context, user).orElse(DEFAULT_LEAVE_MESSAGE);
    }

    /**
     * The enum Channel type.
     */
    enum ChannelType {

        /**
         * The local channel.
         */
        LOCAL,
        /**
         * The global channel.
         */
        GLOBAL,
        /**
         * The local staff channel.
         */
        STAFF_LOCAL,
        /**
         * The global staff channel.
         */
        STAFF_GLOBAL;

        /**
         * Gets the channel type from an identifier.
         *
         * @param identifier the identifier
         * @return the channel type
         * @throws AssertionError in case the identifier does not match any known channel type
         */
        @NonNull
        public static ChannelType fromIdentifier(@NonNull final String identifier) {
            for (final ChannelType channelType : ChannelType.values()) {
                if (channelType.getIdentifier().equals(identifier)) {
                    return channelType;
                }
            }

            throw new AssertionError(
                    "Could not find channel type with identifier '" + identifier + "'.");
        }

        @NonNull
        public String getIdentifier() {
            return this.name();
        }
    }
}
