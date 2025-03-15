/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.api.implementation;

import net.dirtcraft.dirtcore.api.discord.DiscordManager;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ApiDiscordManager implements DiscordManager {

    @NonNull
    private final DirtCorePlugin plugin;

    public ApiDiscordManager(@NonNull final DirtCorePlugin plugin) {this.plugin = plugin;}
}
