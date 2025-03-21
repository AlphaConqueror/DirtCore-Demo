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

package net.dirtcraft.dirtcore.common.storage.entities.chat;

import java.util.List;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "staff_prefix")
public class StaffPrefixEntity implements DirtCoreEntity, Comparable<StaffPrefixEntity> {

    @Id
    @Getter
    @NonNull
    protected String name;

    @Column(name = "full_name", nullable = false, columnDefinition = "TEXT")
    @NonNull
    @Setter
    protected String fullName;

    @Column(name = "full_display", nullable = false, columnDefinition = "TEXT")
    @NonNull
    @Setter
    protected String fullDisplay;

    @Column(name = "short_display", nullable = false, columnDefinition = "TEXT")
    @NonNull
    @Setter
    protected String shortDisplay;

    protected StaffPrefixEntity() {}

    public StaffPrefixEntity(@NonNull final String name, @NonNull final String fullName,
            @NonNull final String fullDisplay, @NonNull final String shortDisplay) {
        this.name = name.toLowerCase(Locale.ROOT);
        this.fullName = fullName;
        this.fullDisplay = fullDisplay;
        this.shortDisplay = shortDisplay;
    }

    public @NonNull Component getFullNameAsComponent() {
        return this.transformMiniMessage(this.fullName);
    }

    @NonNull
    public String getFullNameAsString() {
        return this.fullName;
    }

    public @NonNull Component getFullDisplayAsComponent() {
        return this.transformMiniMessage(this.fullDisplay);
    }

    public @NonNull Component getFullDisplayAsComponent(
            @NonNull final List<StaffPrefixEntity> staffPrefixes) {
        return this.transformMiniMessage(this.fullDisplay)
                .hoverEvent(this.generateHoverEvent(staffPrefixes));
    }

    public @NonNull String getFullDisplayUnformatted() {
        return MessagingManager.minimessageToUnformattedString(this.fullDisplay);
    }

    @NonNull
    public String getFullDisplayAsString() {
        return this.fullDisplay;
    }

    @NonNull
    public Component getShortDisplayAsComponent() {
        return this.transformMiniMessage(this.shortDisplay);
    }

    @NonNull
    public Component getShortDisplayAsComponent(
            @NonNull final List<StaffPrefixEntity> staffPrefixes) {
        return this.transformMiniMessage(this.shortDisplay)
                .hoverEvent(this.generateHoverEvent(staffPrefixes));
    }

    @NonNull
    public String getShortDisplayUnformatted() {
        return MessagingManager.minimessageToUnformattedString(this.shortDisplay);
    }

    @NonNull
    public String getShortDisplayAsString() {
        return this.shortDisplay;
    }

    @Override
    public int compareTo(@NotNull final StaffPrefixEntity other) {
        return this.name.compareTo(other.name);
    }

    @NonNull
    protected Component transformMiniMessage(@NonNull final String message) {
        return MessagingManager.MINIMESSAGE.deserialize(message);
    }

    @NonNull
    protected HoverEvent<Component> generateHoverEvent(
            @NonNull final List<StaffPrefixEntity> staffPrefixes) {
        final TextComponent.Builder builder = Component.text().color(NamedTextColor.GOLD)
                .append(Component.text("This user is part of staff."));

        staffPrefixes.forEach(staffPrefix -> builder.appendNewline()
                .append(Component.text(" - "))
                .append(staffPrefix.getFullNameAsComponent()));
        return HoverEvent.showText(builder.build());
    }
}
