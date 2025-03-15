/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
