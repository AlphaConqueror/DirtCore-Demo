/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
