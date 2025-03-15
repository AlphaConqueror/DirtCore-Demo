/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.concurrent.TimeUnit;

/**
 * A simple expiring set implementation using Caffeine caches
 *
 * @param <E> element type
 */
public class ExpiringSet<E> {

    private final Cache<E, Long> cache;
    private final long lifetime;

    public ExpiringSet(final long duration, final TimeUnit unit) {
        this.cache = CaffeineFactory.newBuilder().expireAfterWrite(duration, unit).build();
        this.lifetime = unit.toMillis(duration);
    }

    public boolean add(final E item) {
        final boolean present = this.contains(item);
        this.cache.put(item, System.currentTimeMillis() + this.lifetime);
        return !present;
    }

    public boolean contains(final E item) {
        final Long timeout = this.cache.getIfPresent(item);
        return timeout != null && timeout > System.currentTimeMillis();
    }

    public void remove(final E item) {
        this.cache.invalidate(item);
    }
}
