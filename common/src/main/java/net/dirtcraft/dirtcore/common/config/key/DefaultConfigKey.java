/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config.key;

import java.util.function.Function;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;

public class DefaultConfigKey<T> implements ConfigKey<T> {

    private final Function<? super ConfigurationAdapter, ? extends T> function;

    private int ordinal = -1;
    private boolean reloadable = true;

    DefaultConfigKey(final Function<? super ConfigurationAdapter, ? extends T> function) {
        this.function = function;
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
        return this.function.apply(adapter);
    }
}
