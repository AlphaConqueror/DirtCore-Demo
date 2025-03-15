/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction;

import java.sql.Timestamp;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface RestrictiveAction {

    @NonNull String getIncidentId();

    @NonNull Timestamp getTimestamp();

    @NonNull UUID getTarget();

    @NonNull UUID getAuthor();

    @NonNull String getReason();

    @NonNull String getServer();

    Action.@NonNull Type getType();
}
