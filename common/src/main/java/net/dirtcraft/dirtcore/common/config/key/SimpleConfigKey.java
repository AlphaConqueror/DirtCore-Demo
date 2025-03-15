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

import java.util.Collection;
import java.util.Set;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;

/**
 * Basic {@link ConfigKey} implementation.
 *
 * @param <T> the value type
 */
public class SimpleConfigKey<T> implements ConfigKey<T> {

    private final ConfigKeyFactory<T> factory;
    private final String path;
    private final T def;
    private int ordinal = -1;
    private boolean reloadable = true;
    private Set<T> possibilities;

    SimpleConfigKey(final ConfigKeyFactory<T> factory, final String path, final T def) {
        this.factory = factory;
        this.path = path;
        this.def = def;
    }

    public ConfigKeyFactory<T> factory() {
        return this.factory;
    }

    public String path() {
        return this.path;
    }

    public T def() {
        return this.def;
    }

    @Override
    public int ordinal() {
        return this.ordinal;
    }

    @Override
    public void setOrdinal(final int ordinal) {
        this.ordinal = ordinal;
    }

    @Override
    public boolean reloadable() {
        return this.reloadable;
    }

    @Override
    public void setReloadable(final boolean reloadable) {
        this.reloadable = reloadable;
    }

    @Override
    public T get(final ConfigurationAdapter adapter) {
        final T result = this.factory.getValue(adapter, this.path, this.def);

        if (this.possibilities == null || this.possibilities.contains(result)) {
            return result;
        }

        adapter.getLogger().warn("Value '{}' is not in {}.", result, this.possibilities());
        return this.def;
    }

    public Collection<T> possibilities() {
        return this.possibilities;
    }

    public void setPossibilities(final Set<T> possibilities) {
        this.possibilities = possibilities;
    }
}
