/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.event.internal.player;

import java.util.UUID;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.type.Cancellable;
import net.dirtcraft.dirtcore.api.event.util.Param;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface PlayerBlockPlaceEvent extends DirtCoreEvent, Cancellable {

    @Param(0)
    @NonNull UUID getUniqueId();

    @Param(1)
    @NonNull String getUsername();

    @Param(2)
    @NonNull Block getBlock();

    @Param(3)
    @NonNull World getWorld();

    @Param(4)
    int getX();

    @Param(5)
    int getY();

    @Param(6)
    int getZ();

    @Param(7)
    boolean isFakePlayer();
}
