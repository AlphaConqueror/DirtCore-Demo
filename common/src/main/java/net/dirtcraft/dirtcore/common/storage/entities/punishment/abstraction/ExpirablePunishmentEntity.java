/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction;

import com.google.common.collect.ImmutableList;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.ExpirablePunishmentHistoryEntity;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

@Entity
public abstract class ExpirablePunishmentEntity<T extends ExpirablePunishmentEntity<?, ?>,
        H extends ExpirablePunishmentHistoryEntity<?>> extends ModifiablePunishmentEntity<H> {

    @Column
    @Nullable
    protected Timestamp expiry;

    @Nullable
    public abstract RevertingPunishmentEntity<T> getReverting();

    public boolean isReverted() {
        return this.getReverting() != null;
    }

    @Override
    public void onRenderHistoryMiddle(@NonNull final TaskContext context,
            final ImmutableList.@NonNull Builder<Component> builder) {
        final TextComponent.Builder durationRow = text().content("  ")
                .append(text("Duration: ", GOLD))
                .append(this.formatDurationNow());

        builder.add(durationRow.build());
        super.onRenderHistoryMiddle(context, builder);
    }

    @NonNull
    public Component formatDurationNow() {
        final boolean isReverted = this.isReverted();
        final Instant now = Instant.now();

        if (this.isPermanent()) {
            return this.formatPermanent(isReverted, now);
        } else {
            assert this.expiry != null;
            final Instant expiryInstant = this.expiry.toInstant();
            final boolean isExpired = expiryInstant.isBefore(now);
            final Instant timestampInstant = this.timestamp.toInstant();
            return this.formatTemporary(isExpired, isReverted, timestampInstant, expiryInstant,
                    now);
        }
    }

    public boolean isPermanent() {
        return this.expiry == null;
    }

    @NonNull
    public Optional<Timestamp> getExpiry() {
        return Optional.ofNullable(this.expiry);
    }

    public void setExpiry(@Nullable final Instant expiry) {
        this.expiry = expiry == null ? null : Timestamp.from(expiry);
    }

    @NotNull
    @Override
    protected TextComponent.Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final H oldHistory, @NonNull final H newHistory) {
        return this.formatEntry(super.formatHistoryEntry(context, index, oldHistory, newHistory),
                oldHistory.getOldExpiry(), newHistory.getOldExpiry());
    }

    @NotNull
    @Override
    protected TextComponent.Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final H history) {
        return this.formatEntry(super.formatHistoryEntry(context, index, history),
                history.getOldExpiry(), this.expiry);
    }

    @Override
    protected void onRenderModifiableHistory(@NonNull final TaskContext context,
            final TextComponent.@NonNull Builder historyBuilder, final int historySize) {
        if (this.isReverted()) {
            historyBuilder.appendNewline()
                    .append(this.formatRevertingEntry(context, historySize + 1));
        }
    }

    protected TextComponent.@NonNull Builder formatRevertingEntry(
            @NonNull final TaskContext context, final int index) {
        final RevertingPunishmentEntity<T> reverting = Objects.requireNonNull(this.getReverting());
        final User author =
                context.plugin().getUserManager().getOrCreateUser(context, reverting.getAuthor());
        final Action.Type type = reverting.getType();
        final Instant timestamp = reverting.getTimestamp().toInstant();

        return text().color(GRAY)
                .append(text().color(WHITE)
                        .append(text("#"))
                        .append(text(index)))
                .append(text(" - ", DARK_GRAY))
                .append(text(type.name(), LogEntity.getTextColor(type)))
                .append(text(" - ", DARK_GRAY))
                .append(text(FormatUtils.formatDateDiff(timestamp, Instant.now(), true, true),
                        BLUE)).appendNewline()
                .append(text("  Timestamp: ", GOLD))
                .append(text(FormatUtils.formatDate(timestamp), BLUE)).appendNewline()
                .append(text("  Author: ", GOLD))
                .append(text(author.getName()))
                .append(text('@'))
                .append(text(reverting.getServer())).appendNewline()
                .append(text("  Reason: ", GOLD))
                .append(text(reverting.getReason()));
    }

    @NotNull
    private TextComponent.Builder formatEntry(final TextComponent.@NonNull Builder builder,
            @Nullable final Timestamp oldExpiry, @Nullable final Timestamp newExpiry) {
        builder.appendNewline()
                .append(text("  Expires: ", GOLD));

        final boolean expiryChanged =
                (oldExpiry != null || newExpiry != null) && (oldExpiry == null || newExpiry == null
                        || oldExpiry.toInstant().getEpochSecond() != newExpiry.toInstant()
                        .getEpochSecond());
        final String oldExpiryFormatted =
                oldExpiry == null ? "never" : FormatUtils.formatDate(oldExpiry.toInstant());

        if (expiryChanged) {
            final String newExpiryFormatted =
                    newExpiry == null ? "never" : FormatUtils.formatDate(newExpiry.toInstant());

            builder.append(CHANGED_BEFORE_AFTER.build(oldExpiryFormatted, newExpiryFormatted));
        } else {
            builder.append(text(oldExpiryFormatted));
        }

        return builder;
    }

    @NonNull
    private Component formatPermanent(final boolean isReverted, @NonNull final Instant now) {
        if (isReverted) {
            assert this.getReverting() != null;
            final Instant revertTimestamp = this.getReverting().getTimestamp().toInstant();
            return text("Permanent", GREEN, STRIKETHROUGH).hoverEvent(
                    this.createHoverText("Reverted ", GREEN, revertTimestamp, now));
        } else {
            return text("Permanent", RED);
        }
    }

    @NonNull
    private Component formatTemporary(final boolean isExpired, final boolean isReverted,
            @NonNull final Instant timestampInstant, @NonNull final Instant expiryInstant,
            @NonNull final Instant now) {
        final Style style;

        if (isExpired || isReverted) {
            style = Style.style(GREEN, STRIKETHROUGH);
        } else {
            style = Style.style(RED);
        }

        final TextComponent.Builder builder = text().append(
                text(FormatUtils.formatDateDiff(timestampInstant, expiryInstant, false, false),
                        style));

        if (isReverted) {
            assert this.getReverting() != null;
            final Instant revertTimestamp = this.getReverting().getTimestamp().toInstant();
            builder.hoverEvent(this.createHoverText("Reverted ", GREEN, revertTimestamp, now));
        } else {
            if (isExpired) {
                builder.hoverEvent(this.createHoverText("Expired ", GREEN, expiryInstant, now));
            } else {
                builder.hoverEvent(this.createHoverText("Expires in ", RED, now, expiryInstant));
            }
        }

        return builder.build();
    }

    @NonNull
    private HoverEvent<Component> createHoverText(@NonNull final String label,
            @NonNull final NamedTextColor color, @NonNull final Instant from,
            @NonNull final Instant to) {
        return HoverEvent.showText(text().color(GRAY)
                .append(text(label))
                .append(text(FormatUtils.formatDateDiff(from, to, false, true), color))
                .appendNewline()
                .append(text("Date: "))
                .append(text(FormatUtils.formatDate(from), BLUE)));
    }
}
