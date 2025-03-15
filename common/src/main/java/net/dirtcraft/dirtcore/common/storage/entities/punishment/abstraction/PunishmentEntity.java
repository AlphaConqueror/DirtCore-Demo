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

package net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction;

import com.google.common.collect.ImmutableList;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.pagination.context.PaginationContext;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PunishmentEntity implements DirtCoreEntity, RestrictiveAction {

    @Id
    @Column(length = 8, nullable = false, unique = true)
    @NonNull
    protected String incident_id;

    @Column(nullable = false)
    @Getter
    protected Timestamp timestamp;

    @Column(nullable = false, length = 36)
    protected String target;

    @Column(nullable = false, length = 36)
    protected String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    protected String reason;

    @Column(nullable = false)
    @Getter
    protected String server;

    public void onRenderHistory(@NonNull final PaginationContext paginationContext,
            final ImmutableList.@NonNull Builder<Component> builder) {
        final DirtCorePlugin plugin = paginationContext.getPlugin();
        final Instant timestampInstant = this.timestamp.toInstant();

        builder.add(text().append(text(">", DARK_GRAY, BOLD))
                .append(space())
                .append(text("#" + this.incident_id, WHITE).hoverEvent(
                        HoverEvent.showText(text("Click to copy!", DARK_GRAY))).clickEvent(
                        ClickEvent.clickEvent(plugin.getPlatformFactory().copyEventAction(),
                                this.incident_id)))
                .append(text(" - ", DARK_GRAY))
                .append(text(this.getType().name(), LogEntity.getTextColor(this.getType())))
                .append(text(" - ", DARK_GRAY))
                .append(text().append(
                        text(FormatUtils.formatDateDiff(timestampInstant, Instant.now(), true,
                                true), BLUE)).hoverEvent(
                        HoverEvent.showText(text(FormatUtils.formatDate(timestampInstant), BLUE))))
                .build());

        final TaskContext taskContext = paginationContext.getTaskContextOrException();
        final User author = plugin.getUserManager().getOrCreateUser(taskContext, this.getAuthor());
        final TextComponent.Builder authorLine = text().color(GRAY);

        authorLine.append(text(author.getName()))
                .append(text('@'))
                .append(text(this.server)).hoverEvent(HoverEvent.showText(
                        text(author.getName(), GRAY).appendNewline().appendNewline()
                                .append(text("Click to copy!", DARK_GRAY)))).clickEvent(
                        ClickEvent.clickEvent(plugin.getPlatformFactory().copyEventAction(),
                                author.getName()));
        builder.add(text().append(text("  Author: ", GOLD))
                .append(authorLine).build());

        this.onRenderHistoryMiddle(taskContext, builder);

        builder.add(text().append(text("  Reason: ", GOLD))
                .append(text(this.reason, GRAY).hoverEvent(
                        HoverEvent.showText(text("Click to copy!", DARK_GRAY))).clickEvent(
                        ClickEvent.clickEvent(plugin.getPlatformFactory().copyEventAction(),
                                this.reason))).build());

        this.onRenderHistoryEnd(taskContext, builder);
    }

    @NonNull
    public MessageEmbed getLogEmbed(@NonNull final TaskContext context,
            @NonNull final LogEntity log, @NonNull final User source, @Nullable final User target) {
        final DirtCorePlugin plugin = context.plugin();
        return DiscordEmbeds.LOG_PUNISHMENT.build(plugin, log, source, target, this,
                plugin.getUserManager().getOrCreateUser(context, this.getAuthor()));
    }

    @Override
    @NonNull
    public String getIncidentId() {
        return this.incident_id;
    }

    @Override
    @NonNull
    public UUID getTarget() {
        return UUID.fromString(this.target);
    }

    @Override
    @NonNull
    public UUID getAuthor() {
        return UUID.fromString(this.author);
    }

    protected void onRenderHistoryMiddle(@NonNull final TaskContext context,
            final ImmutableList.@NonNull Builder<Component> builder) {}

    protected void onRenderHistoryEnd(@NonNull final TaskContext context,
            final ImmutableList.@NonNull Builder<Component> builder) {}
}
