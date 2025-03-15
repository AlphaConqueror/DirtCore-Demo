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

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.suggestion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.context.StringRange;

public class Suggestions {

    private static final Suggestions EMPTY = new Suggestions(StringRange.at(0), new ArrayList<>());

    private final StringRange range;
    private final List<Suggestion> suggestions;

    public Suggestions(final StringRange range, final List<Suggestion> suggestions) {
        this.range = range;
        this.suggestions = suggestions;
    }

    public static CompletableFuture<Suggestions> empty() {
        return CompletableFuture.completedFuture(EMPTY);
    }

    public static Suggestions merge(final String command, final Collection<Suggestions> input) {
        if (input.isEmpty()) {
            return EMPTY;
        } else if (input.size() == 1) {
            return input.iterator().next();
        }

        final Set<Suggestion> texts = new HashSet<>();
        for (final Suggestions suggestions : input) {
            texts.addAll(suggestions.getList());
        }
        return create(command, texts);
    }

    public static Suggestions create(final String command,
            final Collection<Suggestion> suggestions) {
        if (suggestions.isEmpty()) {
            return EMPTY;
        }
        int start = Integer.MAX_VALUE;
        int end = Integer.MIN_VALUE;
        for (final Suggestion suggestion : suggestions) {
            start = Math.min(suggestion.getRange().getStart(), start);
            end = Math.max(suggestion.getRange().getEnd(), end);
        }
        final StringRange range = new StringRange(start, end);
        final Set<Suggestion> texts = new HashSet<>();
        for (final Suggestion suggestion : suggestions) {
            texts.add(suggestion.expand(command, range));
        }
        final List<Suggestion> sorted = new ArrayList<>(texts);
        sorted.sort((a, b) -> a.compareToIgnoreCase(b));
        return new Suggestions(range, sorted);
    }

    public StringRange getRange() {
        return this.range;
    }

    public List<Suggestion> getList() {
        return this.suggestions;
    }

    public boolean isEmpty() {
        return this.suggestions.isEmpty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.range, this.suggestions);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestions)) {
            return false;
        }
        final Suggestions that = (Suggestions) o;
        return Objects.equals(this.range, that.range) && Objects.equals(this.suggestions,
                that.suggestions);
    }

    @Override
    public String toString() {
        return "Suggestions{" + "range=" + this.range + ", suggestions=" + this.suggestions + '}';
    }
}
