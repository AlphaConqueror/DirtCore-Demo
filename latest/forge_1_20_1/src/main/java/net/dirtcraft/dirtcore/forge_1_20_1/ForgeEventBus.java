/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1;

import java.util.Optional;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEventBus extends AbstractEventBus<ModContainer> {

    public ForgeEventBus(final DirtCorePlugin plugin, final DirtCoreApiProvider apiProvider) {
        super(plugin, apiProvider);
    }

    @Override
    protected ModContainer checkPlugin(@NonNull final Object mod) throws IllegalArgumentException {
        final Optional<? extends ModContainer> modContainer =
                ModList.get().getModContainerByObject(mod);

        if (modContainer.isPresent()) {
            return modContainer.get();
        }

        throw new IllegalArgumentException(
                "Object " + mod + " (" + mod.getClass().getName() + ") is not a ModContainer.");
    }
}
