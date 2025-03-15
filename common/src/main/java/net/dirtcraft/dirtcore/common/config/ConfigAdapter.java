/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config;

import java.nio.file.Path;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurateConfigAdapter;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class ConfigAdapter extends ConfigurateConfigAdapter {

    public ConfigAdapter(final DirtCorePlugin plugin, final Path path) {
        super(plugin.getLogger(), path);
    }

    @Override
    protected ConfigurationLoader<? extends ConfigurationNode> createLoader(final Path path) {
        return HoconConfigurationLoader.builder().setPath(path).build();
    }
}
