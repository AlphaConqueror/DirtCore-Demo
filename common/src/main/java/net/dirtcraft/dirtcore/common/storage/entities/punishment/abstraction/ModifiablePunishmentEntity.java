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
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.PunishmentHistoryEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

public abstract class ModifiablePunishmentEntity<H extends PunishmentHistoryEntity<?>> extends PunishmentEntity {

    protected static final Components.Args2<String, String> CHANGED_BEFORE_AFTER =
            (before, after) -> text().append(text(before, RED, ITALIC, STRIKETHROUGH))
                    .append(text(after, GREEN)).build();

    @NonNull
    public abstract List<H> getHistory();

    @Override
    public void onRenderHistoryEnd(@NonNull final TaskContext context,
            final ImmutableList.@NonNull Builder<Component> builder) {
        final List<H> history = this.getHistory();

        if (!history.isEmpty()) {
            history.sort(Comparator.comparing(o -> o.getOldTimestamp()));

            final TextComponent.Builder historyLine = text().content("  ")
                    .append(text("History: ", GOLD));
            final TextComponent.Builder historyBuilder = text();
            final int historySize = history.size();
            H previousEntry = history.get(0);

            for (int i = 1; i < historySize; i++) {
                final H entry = history.get(i);

                historyBuilder.append(this.formatHistoryEntry(context, i, previousEntry, entry))
                        .appendNewline();
                previousEntry = entry;
            }

            historyBuilder.append(this.formatHistoryEntry(context, historySize, previousEntry));

            this.onRenderModifiableHistory(context, historyBuilder, historySize);

            builder.add(historyLine.append(text().style(Style.style(DARK_GRAY, ITALIC))
                            .append(text('['))
                            .append(text("Hover for details", GRAY))
                            .append(text(']')).hoverEvent(HoverEvent.showText(historyBuilder.build())))
                    .build());
        }

        super.onRenderHistoryEnd(context, builder);
    }

    public void setTimestampNow() {
        this.timestamp = Timestamp.from(Instant.now());
    }

    public void setAuthor(@NonNull final UUID author) {
        this.author = author.toString();
    }

    public void setReason(@NonNull final String reason) {
        this.reason = reason;
    }

    public void setServer(@NonNull final String server) {
        this.server = server;
    }

    protected TextComponent.@NonNull Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final H oldHistory, @NonNull final H newHistory) {
        return this.formatEntry(context, index, oldHistory.getOldTimestamp().toInstant(),
                oldHistory.getOldAuthor(), newHistory.getOldAuthor(), oldHistory.getOldReason(),
                newHistory.getOldReason(), oldHistory.getOldServer(), newHistory.getOldServer());
    }

    protected TextComponent.@NonNull Builder formatHistoryEntry(@NonNull final TaskContext context,
            final int index, @NonNull final H history) {
        return this.formatEntry(context, index, history.getOldTimestamp().toInstant(),
                history.getOldAuthor(), this.getAuthor(), history.getOldReason(), this.getReason(),
                history.getOldServer(), this.getServer());
    }

    protected TextComponent.@NonNull Builder formatEntry(@NonNull final TaskContext context,
            final int index, final Instant oldTimestamp, @NonNull final UUID oldAuthor,
            @NonNull final UUID newAuthor, @NonNull final String oldReason,
            @NonNull final String newReason, @NonNull final String oldServer,
            @NonNull final String newServer) {
        final TextComponent.Builder builder = text().color(GRAY)
                .append(text().color(WHITE)
                        .append(text("#"))
                        .append(text(index)))
                .append(text(" - ", DARK_GRAY))
                .append(text(FormatUtils.formatDateDiff(oldTimestamp, Instant.now(), true, true),
                        BLUE)).appendNewline()
                .append(text("  Author: ", GOLD));
        final boolean authorChanged = !newAuthor.equals(oldAuthor);
        final boolean serverChanged = !newServer.equals(oldServer);
        final DirtCorePlugin plugin = context.plugin();
        final User historyAuthor = plugin.getUserManager().getOrCreateUser(context, oldAuthor);
        final String historyAuthorName = historyAuthor.getName();

        if (authorChanged && serverChanged) {
            final User punishmentAuthor =
                    plugin.getUserManager().getOrCreateUser(context, newAuthor);
            builder.append(CHANGED_BEFORE_AFTER.build(historyAuthorName + '@' + oldServer,
                    punishmentAuthor.getName() + '@' + newServer));
        } else {
            if (authorChanged) {
                final User punishmentAuthor =
                        plugin.getUserManager().getOrCreateUser(context, newAuthor);
                builder.append(
                        CHANGED_BEFORE_AFTER.build(historyAuthorName, punishmentAuthor.getName()));
            } else {
                builder.append(text(historyAuthorName));
            }

            builder.append(text('@'));

            if (serverChanged) {
                builder.append(CHANGED_BEFORE_AFTER.build(oldServer, newServer));
            } else {
                builder.append(text(oldServer));
            }
        }

        builder.appendNewline()
                .append(text("  Reason: ", GOLD));

        final boolean reasonChanged = !newReason.equals(oldReason);

        if (reasonChanged) {
            builder.append(CHANGED_BEFORE_AFTER.build(oldReason, newReason));
        } else {
            builder.append(text(oldReason));
        }

        return builder;
    }

    protected void onRenderModifiableHistory(@NonNull final TaskContext context,
            final TextComponent.@NonNull Builder historyBuilder, final int historySize) {}
}
