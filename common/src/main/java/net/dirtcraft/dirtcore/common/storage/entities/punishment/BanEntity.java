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

package net.dirtcraft.dirtcore.common.storage.entities.punishment;

import com.google.common.collect.ImmutableList;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Setter;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RevertingPunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.BanHistoryEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "bans")
public class BanEntity extends ExpirablePunishmentEntity<BanEntity, BanHistoryEntity> {

    @OneToMany(mappedBy = "original")
    protected List<BanHistoryEntity> history;

    @Nullable
    @OneToOne
    @Setter
    protected UnbanEntity unban;
    @Column(nullable = false)
    protected boolean ip_banned;

    protected BanEntity() {}

    public BanEntity(@NonNull final String incidentId, @NonNull final UUID target,
            @NonNull final UUID author, @NonNull final String reason, @NonNull final String server,
            @Nullable final Instant expiry, final boolean ipBanned) {
        this.incident_id = incidentId;
        this.timestamp = Timestamp.from(Instant.now());
        this.target = target.toString();
        this.author = author.toString();
        this.reason = reason;
        this.server = server;
        this.expiry = expiry == null ? null : Timestamp.from(expiry);
        this.history = Collections.emptyList();
        this.unban = null;
        this.ip_banned = ipBanned;
    }

    @Override
    public Action.@NonNull Type getType() {
        return Action.Type.BAN;
    }

    @Override
    public @NonNull List<BanHistoryEntity> getHistory() {
        return this.history;
    }

    @Override
    public void onRenderHistoryEnd(@NonNull final TaskContext context,
            @NotNull final ImmutableList.Builder<Component> builder) {
        builder.add(text("  ").append(text("IP-Banned: ", NamedTextColor.GOLD).append(
                text(this.isIpBanned(),
                        this.isIpBanned() ? NamedTextColor.GREEN : NamedTextColor.RED))));
        super.onRenderHistoryEnd(context, builder);
    }

    @Override
    public @Nullable RevertingPunishmentEntity<BanEntity> getReverting() {
        return this.unban;
    }

    @NotNull
    @Override
    protected TextComponent.Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final BanHistoryEntity oldHistory,
            @NonNull final BanHistoryEntity newHistory) {
        return this.formatEntry(super.formatHistoryEntry(context, index, oldHistory, newHistory),
                oldHistory.wasIpBanned(), newHistory.wasIpBanned());
    }

    @NotNull
    @Override
    protected TextComponent.Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final BanHistoryEntity history) {
        return this.formatEntry(super.formatHistoryEntry(context, index, history),
                history.wasIpBanned(), this.isIpBanned());
    }

    public boolean isIpBanned() {
        return this.ip_banned;
    }

    public void setIpBanned(final boolean ipBanned) {
        this.ip_banned = ipBanned;
    }

    @NotNull
    protected TextComponent.Builder formatEntry(final TextComponent.@NonNull Builder builder,
            final boolean oldIpBanned, final boolean newIpBanned) {
        builder.appendNewline()
                .append(text("  IP-Banned: ", NamedTextColor.GOLD));

        final boolean ipBannedChanged = newIpBanned != oldIpBanned;

        if (ipBannedChanged) {
            builder.append(CHANGED_BEFORE_AFTER.build(String.valueOf(oldIpBanned),
                    String.valueOf(newIpBanned)));
        } else {
            builder.append(text(oldIpBanned));
        }

        return builder;
    }
}
