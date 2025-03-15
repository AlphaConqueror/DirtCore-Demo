/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1;

import java.util.Optional;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeEventBus extends AbstractEventBus<ModContainer> {

    public NeoForgeEventBus(final DirtCorePlugin plugin, final DirtCoreApiProvider apiProvider) {
        super(plugin, apiProvider);
    }

    @Override
    protected ModContainer checkPlugin(@NonNull final Object mod) throws IllegalArgumentException {
        if (mod instanceof ModContainer) {
            final Optional<? extends ModContainer> modContainer =
                    ModList.get().getModContainerById(((ModContainer) mod).getModId());

            if (modContainer.isPresent()) {
                return modContainer.get();
            }
        }

        throw new IllegalArgumentException(
                "Object " + mod + " (" + mod.getClass().getName() + ") is not a ModContainer.");
    }
}
