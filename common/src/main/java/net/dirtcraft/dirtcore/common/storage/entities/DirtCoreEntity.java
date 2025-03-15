/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities;

import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface DirtCoreEntity {

    String TABLE_PREFIX = DirtCorePlugin.MOD_ID + '_';

    default void save(@NonNull final DirtCorePlugin plugin) {
        plugin.getStorage().performTask(this::save);
    }

    default void save(@NonNull final TaskContext context) {
        context.session().merge(this);
    }
}
