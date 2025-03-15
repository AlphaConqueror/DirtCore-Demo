/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A simple expiring map implementation using Caffeine caches
 *
 * @param <K> key type
 * @param <V> value type
 */
public class ExpiringMap<K, V> {

    private final Cache<K, ExpiringValue<V>> cache;
    private final long lifetime;

    public ExpiringMap(final long duration, final TimeUnit unit) {
        this.cache = CaffeineFactory.newBuilder().expireAfterWrite(duration, unit).build();
        this.lifetime = unit.toMillis(duration);
    }

    public boolean add(final K key, final V value) {
        final boolean present = this.contains(key);
        this.cache.put(key, new ExpiringValue<>(System.currentTimeMillis() + this.lifetime, value));
        return !present;
    }

    public boolean contains(final K key) {
        final ExpiringValue<V> expiringValue = this.cache.getIfPresent(key);
        return expiringValue != null && expiringValue.getTimeout() > System.currentTimeMillis();
    }

    public Optional<V> get(final K key) {
        final ExpiringValue<V> expiringValue = this.cache.getIfPresent(key);
        return Optional.ofNullable(
                expiringValue != null && expiringValue.getTimeout() > System.currentTimeMillis()
                        ? expiringValue.getValue() : null);
    }

    public void remove(final K key) {
        this.cache.invalidate(key);
    }

    private static class ExpiringValue<V> {

        private final long timeout;
        private final V value;

        private ExpiringValue(final long timeout, final V value) {
            this.timeout = timeout;
            this.value = value;
        }

        public long getTimeout() {
            return this.timeout;
        }

        public V getValue() {
            return this.value;
        }
    }
}
