/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
