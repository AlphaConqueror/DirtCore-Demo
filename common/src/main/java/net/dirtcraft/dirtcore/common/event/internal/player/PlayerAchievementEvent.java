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
import net.dirtcraft.dirtcore.api.event.util.Param;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PlayerAchievementEvent extends DirtCoreEvent {

    @Param(0)
    @NonNull UUID getUniqueId();

    @Param(1)
    @NonNull String getUsernameName();

    @Param(2)
    @NonNull String getAchievementName();

    @Param(3)
    @Nullable String getAchievementDescription();
}
