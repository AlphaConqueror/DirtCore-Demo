/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.api.event.player;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

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
