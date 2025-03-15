/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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