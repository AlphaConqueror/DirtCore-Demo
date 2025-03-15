/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEventBus extends AbstractEventBus<ModContainer> {

    public ForgeEventBus(final DirtCorePlugin plugin, final DirtCoreApiProvider apiProvider) {
        super(plugin, apiProvider);
    }

    @Override
    protected ModContainer checkPlugin(@NonNull final Object mod) throws IllegalArgumentException {
        final ModContainer modContainer =
                Loader.instance().getModList().stream().filter(m -> m == mod).findFirst()
                        .orElse(null);

        if (modContainer != null) {
            return modContainer;
        }

        throw new IllegalArgumentException(
                "Object " + mod + " (" + mod.getClass().getName() + ") is not a ModContainer.");
    }
}
