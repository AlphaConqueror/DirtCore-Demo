/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.api.event;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.loader.event.LoaderEventDispatcher;

public interface ForgeLoaderEventDispatcher<B, I, W> extends LoaderEventDispatcher<B, I, W> {

    boolean dispatchPlayerBlockPlace(final boolean initialState, final UUID uniqueId,
            final String username, final B block, final W world, final int x, final int y,
            final int z, boolean isFakePlayer);
}
