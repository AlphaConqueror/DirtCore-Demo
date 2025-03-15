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

import java.util.Objects;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;
import net.dirtcraft.dirtcore.common.command.abstraction.context.StringRange;

public class Suggestion implements Comparable<Suggestion> {

    private final StringRange range;
    private final String text;
    private final Message tooltip;

    public Suggestion(final StringRange range, final String text) {
        this(range, text, null);
    }

    public Suggestion(final StringRange range, final String text, final Message tooltip) {
        this.range = range;
        this.text = text;
        this.tooltip = tooltip;
    }

    public StringRange getRange() {
        return this.range;
    }

    public String getText() {
        return this.text;
    }

    public Message getTooltip() {
        return this.tooltip;
    }

    public String apply(final String input) {
        if (this.range.getStart() == 0 && this.range.getEnd() == input.length()) {
            return this.text;
        }
        final StringBuilder result = new StringBuilder();
        if (this.range.getStart() > 0) {
            result.append(input.substring(0, this.range.getStart()));
        }
        result.append(this.text);
        if (this.range.getEnd() < input.length()) {
            result.append(input.substring(this.range.getEnd()));
        }
        return result.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.range, this.text, this.tooltip);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Suggestion)) {
            return false;
        }
        final Suggestion that = (Suggestion) o;
        return Objects.equals(this.range, that.range) && Objects.equals(this.text, that.text)
                && Objects.equals(this.tooltip, that.tooltip);
    }

    @Override
    public String toString() {
        return "Suggestion{" + "range=" + this.range + ", text='" + this.text + '\'' + ", tooltip='"
                + this.tooltip + '\'' + '}';
    }

    @Override
    public int compareTo(final Suggestion o) {
        return this.text.compareTo(o.text);
    }

    public int compareToIgnoreCase(final Suggestion b) {
        return this.text.compareToIgnoreCase(b.text);
    }

    public Suggestion expand(final String command, final StringRange range) {
        if (range.equals(this.range)) {
            return this;
        }
        final StringBuilder result = new StringBuilder();
        if (range.getStart() < this.range.getStart()) {
            result.append(command.substring(range.getStart(), this.range.getStart()));
        }
        result.append(this.text);
        if (range.getEnd() > this.range.getEnd()) {
            result.append(command.substring(this.range.getEnd(), range.getEnd()));
        }
        return new Suggestion(range, result.toString(), this.tooltip);
    }
}
