/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.context;

import java.util.LinkedList;
import java.util.Queue;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.Session;

public class DirtCoreTaskContext implements TaskContext {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final Session session;
    @NonNull
    private final Queue<Runnable> queue = new LinkedList<>();

    public DirtCoreTaskContext(@NonNull final DirtCorePlugin plugin,
            @NonNull final Session session) {
        this.plugin = plugin;
        this.session = session;
    }

    @Override
    public @NonNull DirtCorePlugin plugin() {
        return this.plugin;
    }

    @Override
    public @NonNull Session session() {
        return this.session;
    }

    @Override
    public void queue(@NonNull final Runnable runnable) {
        this.queue.add(runnable);
    }

    @Override
    public void executeTasks() {
        while (!this.queue.isEmpty()) {
            this.queue.poll().run();
        }
    }
}
