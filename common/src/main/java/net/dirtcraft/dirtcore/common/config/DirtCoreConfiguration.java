/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config;

import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;
import net.dirtcraft.dirtcore.common.logging.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreConfiguration extends KeyedConfiguration {

    public DirtCoreConfiguration(@NonNull final Logger logger,
            @NonNull final ConfigurationAdapter adapter) {
        super(logger, adapter, ConfigKeys.getKeys());

        this.init();
    }

    @Override
    protected void load(final boolean initial) {
        super.load(initial);
    }
}
