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

package net.dirtcraft.dirtcore.common.cache;

import com.google.common.collect.ForwardingMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class LoadingMap<K, V> extends ForwardingMap<K, V> implements Map<K, V> {

    private final Map<K, V> map;
    private final Function<K, V> function;

    private LoadingMap(final Map<K, V> map, final Function<K, V> function) {
        this.map = map;
        this.function = function;
    }

    public static <K, V> LoadingMap<K, V> of(final Map<K, V> map, final Function<K, V> function) {
        return new LoadingMap<>(map, function);
    }

    public static <K, V> LoadingMap<K, V> of(final Function<K, V> function) {
        return of(new ConcurrentHashMap<>(), function);
    }

    public V getIfPresent(final K key) {
        return this.map.get(key);
    }

    @Override
    protected Map<K, V> delegate() {
        return this.map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(final Object key) {
        final V value = this.map.get(key);

        if (value != null) {
            return value;
        }

        return this.map.computeIfAbsent((K) key, this.function);
    }
}