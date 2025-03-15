/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1;

import net.dirtcraft.dirtcore.common.scheduler.AbstractJavaScheduler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class NeoForgeSchedulerAdapter extends AbstractJavaScheduler {

    public NeoForgeSchedulerAdapter(final DirtCoreNeoForgeBootstrap bootstrap) {
        super(bootstrap);
    }

    @SubscribeEvent
    public void onServerTick(final ServerTickEvent.Post event) {
        this.executeRemainingSyncTasks();
    }
}
