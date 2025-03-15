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

package net.dirtcraft.dirtcore.common.storage.entities.player;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.ChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The server specific player data.
 */
@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "player_data")
public class PlayerDataEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(name = "unique_id", length = 36, nullable = false)
    @NonNull
    protected String uniqueId;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String server;

    @Column(nullable = false)
    @Getter
    @Setter
    protected double balance;

    @Column(name = "last_seen")
    @Nullable
    protected Timestamp lastSeen;

    @Column(name = "active_chat_marker")
    @Nullable
    protected String activeChatMarker;

    @Column(name = "active_prefix")
    @Nullable
    protected String activePrefix;

    @Column(name = "join_message")
    @Getter
    @Nullable
    @Setter
    protected String joinMessage;

    @Column(name = "leave_message")
    @Getter
    @Nullable
    @Setter
    protected String leaveMessage;

    protected PlayerDataEntity() {}

    public PlayerDataEntity(@NonNull final DirtCorePlugin plugin, @NonNull final UUID uniqueId) {
        this.uniqueId = uniqueId.toString();
        this.server = plugin.getServerIdentifier();
        this.balance = 0d;
        this.activeChatMarker = null;
        this.activePrefix = null;
        this.joinMessage = null;
        this.leaveMessage = null;
    }

    @NonNull
    public UUID getUniqueId() {
        return UUID.fromString(this.uniqueId);
    }

    public void depositBalance(final Double amount) {this.balance += amount;}

    public void withdrawBalance(final Double amount) {this.balance -= amount;}

    @NonNull
    public Optional<Timestamp> getLastSeen() {
        return Optional.ofNullable(this.lastSeen);
    }

    public void setLastSeenNow() {
        this.lastSeen = Timestamp.from(Instant.now());
    }

    @NonNull
    public Optional<ChatMarkerEntity> getChatMarker(@NonNull final TaskContext context) {
        final Optional<String> markerNameOptional = this.getActiveMarker();

        if (markerNameOptional.isPresent()) {
            final String markerName = markerNameOptional.get();
            final ChatMarkerEntity marker =
                    context.session().get(ChatMarkerEntity.class, markerName);
            final DirtCorePlugin plugin = context.plugin();

            if (marker == null) {
                context.queue(() -> plugin.getLogger()
                        .warn("Could not find chat marker with name '{}'.", markerName));
                return Optional.empty();
            }

            // active chat marker might have been revoked or user has lost chat marker permission
            if (!plugin.getChatManager()
                    .isChatMarkerAvailable(context, this.getUniqueId(), markerName)) {
                return Optional.empty();
            }

            return Optional.of(marker);
        }

        return Optional.empty();
    }

    @NonNull
    public Component getChatMarkerComponentOrDefault(@NonNull final TaskContext context) {
        return this.getChatMarker(context).map(ChatMarkerEntity::getDisplayAsComponent)
                .orElse(Components.CHAT_MARKER_DEFAULT);
    }

    public void setChatMarker(@NonNull final TaskContext context,
            @NonNull final ChatMarkerEntity chatMarker) {
        this.setActiveMarker(chatMarker);
        context.session().merge(this);
        context.queue(() -> context.plugin().getLogger()
                .info("Set active chat marker of user with id '{}' to '{}'.", this.uniqueId,
                        chatMarker.getName()));
    }

    public void unsetChatMarker(@NonNull final TaskContext context) {
        this.setActiveMarker(null);
        context.session().merge(this);
        context.queue(() -> context.plugin().getLogger()
                .info("Unset active chat marker of user with id '{}'.", this.uniqueId));
    }

    @NonNull
    public Optional<PrefixEntity> getPrefix(@NonNull final TaskContext context) {
        final Optional<String> prefixNameOptional = this.getActivePrefix();

        if (prefixNameOptional.isPresent()) {
            final String prefixName = prefixNameOptional.get();
            final PrefixEntity prefix = context.session().get(PrefixEntity.class, prefixName);
            final DirtCorePlugin plugin = context.plugin();

            if (prefix == null) {
                context.queue(() -> plugin.getLogger()
                        .warn("Could not find prefix with name '{}'.", prefixName));
                return Optional.empty();
            }

            // active chat marker might have been revoked or user has lost chat marker permission
            if (!plugin.getChatManager()
                    .isPrefixAvailable(context, this.getUniqueId(), prefixName)) {
                return Optional.empty();
            }

            return Optional.of(prefix);
        }

        return Optional.empty();
    }

    public void setPrefix(@NonNull final TaskContext context, @NonNull final PrefixEntity prefix) {
        this.setActivePrefix(prefix);
        context.session().merge(this);
        context.queue(() -> context.plugin().getLogger()
                .info("Set active prefix of user with id '{}' to '{}'.", this.uniqueId,
                        prefix.getName()));
    }

    public void unsetPrefix(@NonNull final TaskContext context) {
        this.setActivePrefix(null);
        context.session().merge(this);
        context.queue(() -> context.plugin().getLogger()
                .info("Unset active prefix of user with id '{}'.", this.uniqueId));
    }

    @NonNull
    protected Optional<String> getActiveMarker() {
        return Optional.ofNullable(this.activeChatMarker);
    }

    protected void setActiveMarker(@Nullable final ChatMarkerEntity activeMarker) {
        this.activeChatMarker = activeMarker == null ? null : activeMarker.getName();
    }

    @NonNull
    protected Optional<String> getActivePrefix() {
        return Optional.ofNullable(this.activePrefix);
    }

    protected void setActivePrefix(@Nullable final PrefixEntity activePrefix) {
        this.activePrefix = activePrefix == null ? null : activePrefix.getName();
    }
}
