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
