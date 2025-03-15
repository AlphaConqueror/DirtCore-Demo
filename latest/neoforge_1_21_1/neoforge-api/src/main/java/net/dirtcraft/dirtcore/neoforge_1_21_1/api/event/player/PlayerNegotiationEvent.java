/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.api.event.player;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.concurrent.Future;
import net.minecraft.network.Connection;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * A cancellable variant of
 * {@link net.neoforged.neoforge.event.entity.player.PlayerNegotiationEvent}.
 */
public class PlayerNegotiationEvent extends net.neoforged.neoforge.event.entity.player.PlayerNegotiationEvent implements ICancellableEvent {

    public PlayerNegotiationEvent(final Connection connection, final GameProfile profile,
            final List<Future<Void>> futures) {
        super(connection, profile, futures);
    }
}
