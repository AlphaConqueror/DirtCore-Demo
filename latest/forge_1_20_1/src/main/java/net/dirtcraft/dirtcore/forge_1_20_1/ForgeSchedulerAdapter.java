/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1;

import net.dirtcraft.dirtcore.common.scheduler.AbstractJavaScheduler;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeSchedulerAdapter extends AbstractJavaScheduler {

    public ForgeSchedulerAdapter(final DirtCoreForgeBootstrap bootstrap) {
        super(bootstrap);
    }

    @SubscribeEvent
    public void onServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.executeRemainingSyncTasks();
        }
    }
}
