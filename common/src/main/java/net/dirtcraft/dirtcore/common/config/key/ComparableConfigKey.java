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

package net.dirtcraft.dirtcore.common.config.key;

import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;

public class ComparableConfigKey<T extends Comparable<T>> extends SimpleConfigKey<T> {

    private T min;
    private T max;

    public ComparableConfigKey(final ConfigKeyFactory<T> factory, final String path, final T def) {
        super(factory, path, def);
    }

    @Override
    public T get(final ConfigurationAdapter adapter) {
        final T value = super.get(adapter);

        if (this.min != null && this.min.compareTo(value) >= 0
                || this.max != null && this.max.compareTo(value) <= 0) {
            adapter.getLogger().warn("Value {} not in range [{},{}].", value, this.min, this.max);
            return this.def();
        }

        return value;
    }

    public void setRange(final T min, final T max) {
        this.min = min;
        this.max = max;
    }

    public T getMin() {
        return this.min;
    }

    public T getMax() {
        return this.max;
    }
}
