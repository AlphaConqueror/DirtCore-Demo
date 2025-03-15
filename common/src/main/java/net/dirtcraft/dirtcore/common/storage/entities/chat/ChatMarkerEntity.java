/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.chat;

import com.google.common.collect.ImmutableList;
import java.util.function.BiFunction;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "chat_marker")
public class ChatMarkerEntity implements DirtCoreEntity, Comparable<ChatMarkerEntity> {

    @Transient
    private static final BiFunction<String, String, Component> CHAT_MARKER_USE =
            (identifier, command) -> Components.USE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to set this chat marker.", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.runCommand(command + ' ' + identifier));

    @Id
    @Getter
    @NonNull
    protected String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @NonNull
    @Setter
    protected String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NonNull
    @Setter
    protected String display;

    protected ChatMarkerEntity() {}

    public ChatMarkerEntity(@NonNull final String name, @NonNull final String description,
            @NonNull final String display) {
        this.name = name;
        this.description = description;
        this.display = display;
    }

    @NonNull
    public Component getDisplayAsComponent() {
        return MessagingManager.MINIMESSAGE.deserialize(this.display).hoverEvent(
                HoverEvent.showText(text().color(GRAY)
                        .append(text("Name: ", GOLD))
                        .append(text(this.name)).appendNewline()
                        .append(text("Description: ", GOLD))
                        .append(text(this.description))));
    }

    @NonNull
    public String getDisplayUnformatted() {
        return MessagingManager.minimessageToUnformattedString(this.display);
    }

    @NonNull
    public String getDisplayAsString() {
        return this.display;
    }

    public void render(final ImmutableList.@NonNull Builder<Component> builder,
            final boolean hasUnlocked, @Nullable final String setCommand) {
        final TextComponent.Builder nameBuilder = text().append(text(">", DARK_GRAY, BOLD))
                .append(space())
                .append(text("Name: ", GOLD))
                .append(text(this.name, WHITE));

        if (!hasUnlocked) {
            nameBuilder.append(text('*', DARK_AQUA).hoverEvent(HoverEvent.showText(
                    text("This chat marker is available via permission node.", DARK_AQUA))));
        }

        if (setCommand != null) {
            nameBuilder.appendSpace()
                    .append(CHAT_MARKER_USE.apply(this.name, setCommand));
        }

        builder.add(nameBuilder.build())
                .add(text().content("  ")
                        .append(text("Description: ", GOLD))
                        .append(text(this.description, GRAY)).build())
                .add(text().content("  ")
                        .append(text("Display: ", GOLD))
                        .append(this.getDisplayAsComponent()).build());
    }

    @Override
    public int compareTo(@NotNull final ChatMarkerEntity other) {
        return this.name.compareTo(other.name);
    }
}
