/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.tasks;

import java.util.concurrent.TimeUnit;
import net.dirtcraft.dirtcore.common.cache.BufferedRequest;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

/**
 * System wide sync task for DirtCore.
 *
 * <p>Ensures that all local data is consistent with the storage.</p>
 */
public class SyncTask implements Runnable {

    private final DirtCorePlugin plugin;

    public SyncTask(final DirtCorePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs the update task
     *
     * <p>Called <b>async</b>.</p>
     */
    @Override
    public void run() {
        this.plugin.getLimitManager().loadAllLimitedBlocks().join();
        this.plugin.getRestrictionManager().loadAllRestrictions().join();
    }

    public static class Buffer extends BufferedRequest<Void> {

        private final DirtCorePlugin plugin;

        public Buffer(final DirtCorePlugin plugin) {
            super(500L, TimeUnit.MILLISECONDS, plugin.getBootstrap().getScheduler());
            this.plugin = plugin;
        }

        @Override
        protected Void perform() {
            new SyncTask(this.plugin).run();
            return null;
        }
    }
}
