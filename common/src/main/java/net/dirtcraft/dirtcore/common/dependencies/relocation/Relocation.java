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

package net.dirtcraft.dirtcore.common.dependencies.relocation;

import java.util.Objects;

public final class Relocation {

    private static final String RELOCATION_PREFIX = "net.dirtcraft.dirtcore.lib.";
    private final String pattern;
    private final String relocatedPattern;

    private Relocation(final String pattern, final String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public static Relocation of(final String id, final String pattern) {
        return new Relocation(pattern.replace("{}", "."), RELOCATION_PREFIX + id);
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getRelocatedPattern() {
        return this.relocatedPattern;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.relocatedPattern);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Relocation that = (Relocation) o;
        return Objects.equals(this.pattern, that.pattern) && Objects.equals(this.relocatedPattern,
                that.relocatedPattern);
    }
}
