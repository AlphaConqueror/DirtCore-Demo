/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;
import net.dirtcraft.dirtcore.common.config.key.ConfigKey;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;

public class KeyedConfiguration {

    private final Logger logger;
    private final ConfigurationAdapter adapter;
    private final List<ConfigKey<?>> keys;
    private final ValuesMap values;

    public KeyedConfiguration(final Logger logger, final ConfigurationAdapter adapter,
            final List<ConfigKey<?>> keys) {
        this.logger = logger;
        this.adapter = adapter;
        this.keys = keys;
        this.values = new ValuesMap(keys.size());
    }

    /**
     * Initialises the given pseudo-enum keys class.
     *
     * @param keysClass the keys class
     * @return the list of keys defined by the class with their ordinal values set
     */
    public static List<ConfigKey<?>> initialise(final Class<?> keysClass) {
        // get a list of all keys
        final List<ConfigKey<?>> keys = Arrays.stream(keysClass.getFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> ConfigKey.class.equals(f.getType())).map(f -> {
                    try {
                        return (ConfigKey<?>) f.get(null);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(ImmutableCollectors.toList());

        // set ordinal values
        for (int i = 0; i < keys.size(); i++) {
            keys.get(i).setOrdinal(i);
        }

        return keys;
    }

    /**
     * Gets the value of a given context key.
     *
     * @param key the key
     * @param <T> the key return type
     * @return the value mapped to the given key. May be null.
     */
    public <T> T get(final ConfigKey<T> key) {
        return this.values.get(key);
    }

    /**
     * Reloads the configuration.
     */
    public void reload() {
        this.adapter.reload();
        this.load(false);
    }

    protected void init() {
        this.load(true);
    }

    protected void load(final boolean initial) {
        for (final ConfigKey<?> key : this.keys) {
            if (initial || key.reloadable()) {
                this.values.put(key, key.get(this.adapter));
            }
        }
    }

    public static class ValuesMap {

        private final Object[] values;

        public ValuesMap(final int size) {
            this.values = new Object[size];
        }

        @SuppressWarnings("unchecked")
        public <T> T get(final ConfigKey<T> key) {
            return (T) this.values[key.ordinal()];
        }

        public void put(final ConfigKey<?> key, final Object value) {
            this.values[key.ordinal()] = value;
        }
    }
}
