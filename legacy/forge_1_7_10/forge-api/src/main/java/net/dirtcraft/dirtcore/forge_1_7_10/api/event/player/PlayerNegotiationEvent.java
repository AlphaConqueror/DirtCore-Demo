/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.api.event.player;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.minecraft.network.NetworkManager;

@Cancelable
public class PlayerNegotiationEvent extends Event {

    private final NetworkManager connection;
    private final GameProfile profile;
    private final List<Future<Void>> futures;

    public PlayerNegotiationEvent(final NetworkManager connection, final GameProfile profile,
            final List<Future<Void>> futures) {
        this.connection = connection;
        this.profile = profile;
        this.futures = futures;
    }

    /**
     * Enqueue work to be completed asynchronously before the login proceeds.
     */
    public void enqueueWork(final Runnable runnable) {
        this.enqueueWork(CompletableFuture.runAsync(runnable));
    }

    /**
     * Enqueue work to be completed asynchronously before the login proceeds.
     */
    public void enqueueWork(final Future<Void> future) {
        this.futures.add(future);
    }

    public NetworkManager getConnection() {
        return this.connection;
    }

    public GameProfile getProfile() {
        return this.profile;
    }
}
