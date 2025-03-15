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

package net.dirtcraft.dirtcore.common.command.abstraction.context;

import java.util.Objects;
import net.dirtcraft.dirtcore.common.command.abstraction.ImmutableStringReader;

public class StringRange {

    private final int start;
    private final int end;

    public StringRange(final int start, final int end) {
        this.start = start;
        this.end = end;
    }

    public static StringRange at(final int pos) {
        return new StringRange(pos, pos);
    }

    public static StringRange between(final int start, final int end) {
        return new StringRange(start, end);
    }

    public static StringRange encompassing(final StringRange a, final StringRange b) {
        return new StringRange(Math.min(a.getStart(), b.getStart()),
                Math.max(a.getEnd(), b.getEnd()));
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public String get(final ImmutableStringReader reader) {
        return reader.getString().substring(this.start, this.end);
    }

    public String get(final String string) {
        return string.substring(this.start, this.end);
    }

    public boolean isEmpty() {
        return this.start == this.end;
    }

    public int getLength() {
        return this.end - this.start;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.start, this.end);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StringRange)) {
            return false;
        }
        final StringRange that = (StringRange) o;
        return this.start == that.start && this.end == that.end;
    }

    @Override
    public String toString() {
        return "StringRange{" + "start=" + this.start + ", end=" + this.end + '}';
    }
}
