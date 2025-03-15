/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.context;

import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.Session;

public interface TaskContext extends net.dirtcraft.storageutils.taskcontext.TaskContext {

    @NonNull
    static TaskContext create(@NonNull final DirtCorePlugin plugin,
            @NonNull final Session session) {
        return new DirtCoreTaskContext(plugin, session);
    }

    /**
     * Gets the plugin instance.
     *
     * @return the plugin
     */
    @NonNull DirtCorePlugin plugin();
}
