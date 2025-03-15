/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit;

import java.util.concurrent.Executor;
import net.dirtcraft.dirtcore.common.scheduler.AbstractJavaScheduler;

public class BukkitSchedulerAdapter extends AbstractJavaScheduler {

    private final Executor sync;

    public BukkitSchedulerAdapter(final DirtCoreBukkitBootstrap bootstrap) {
        super(bootstrap);
        this.sync = r -> bootstrap.getServer().getScheduler()
                .scheduleSyncDelayedTask(bootstrap.getLoader(), r);
    }

    @Override
    public Executor sync() {
        return this.sync;
    }
}
