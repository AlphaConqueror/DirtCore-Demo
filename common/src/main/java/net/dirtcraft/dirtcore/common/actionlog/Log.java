/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.actionlog;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Log {

    private static final Log EMPTY = new Log(ImmutableList.of());
    private final SortedSet<LogEntity> content;

    Log(final List<LogEntity> content) {
        this.content = ImmutableSortedSet.copyOf(content);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Log empty() {
        return EMPTY;
    }

    public SortedSet<LogEntity> getContent() {
        return this.content;
    }

    public SortedSet<LogEntity> getContent(@NonNull final UUID source) {
        return this.content.stream().filter(e -> e.getSource().equals(source))
                .collect(ImmutableCollectors.toSortedSet());
    }

    public static class Builder {

        private final List<LogEntity> content = new ArrayList<>();

        public Builder add(final LogEntity e) {
            this.content.add(e);
            return this;
        }

        public Log build() {
            if (this.content.isEmpty()) {
                return EMPTY;
            }
            return new Log(this.content);
        }
    }

}
