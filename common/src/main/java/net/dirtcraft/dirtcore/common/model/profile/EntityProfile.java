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

package net.dirtcraft.dirtcore.common.model.profile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class EntityProfile implements Comparable<EntityProfile> {

    @NonNull
    private final Map<String, Integer> map;
    private final int totalEntities;

    public EntityProfile(@NonNull final Map<String, Integer> map, final int totalEntities) {
        this.map = map;
        this.totalEntities = totalEntities;
    }

    @NonNull
    public static EntityProfile of(@NonNull final Map<String, Integer> map, final int total) {
        return new EntityProfile(map, total);
    }

    @NonNull
    public Component summaryAsComponent() {
        final TextComponent.Builder entityBuilder = Component.text();
        boolean flag = false;

        for (final Map.Entry<String, Integer> entry : this.getSortedEntries()) {
            if (flag) {
                entityBuilder.appendNewline();
            } else {
                flag = true;
            }

            final String key = entry.getKey();
            final int value = entry.getValue();

            entityBuilder.append(Component.text().color(NamedTextColor.GRAY)
                    .append(Component.text(key))
                    .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(value, NamedTextColor.WHITE)));
        }

        return Component.text()
                .append(Component.text("Total Entities: ", NamedTextColor.GRAY))
                .append(Component.text(this.totalEntities)).appendNewline().appendNewline()
                    .append(entityBuilder).build();
    }

    @NonNull
    public HoverEvent<Component> summaryAsHoverEvent() {
        return HoverEvent.showText(this.summaryAsComponent());
    }

    public int getTotalEntities() {
        return this.totalEntities;
    }

    @Override
    public int compareTo(@NotNull final EntityProfile o) {
        // reverse order
        return Integer.compare(o.totalEntities, this.totalEntities);
    }

    @NonNull
    private List<Map.Entry<String, Integer>> getSortedEntries() {
        final List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(this.map.entrySet());

        // sort the entries by value in descending order
        // if the value is the same, compare keys
        sortedEntries.sort(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                .thenComparing(Map.Entry.comparingByKey()));
        return sortedEntries;
    }
}
