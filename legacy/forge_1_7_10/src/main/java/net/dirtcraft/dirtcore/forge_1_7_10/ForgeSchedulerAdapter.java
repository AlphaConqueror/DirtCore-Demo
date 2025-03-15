/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.dirtcraft.dirtcore.common.scheduler.AbstractJavaScheduler;

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
