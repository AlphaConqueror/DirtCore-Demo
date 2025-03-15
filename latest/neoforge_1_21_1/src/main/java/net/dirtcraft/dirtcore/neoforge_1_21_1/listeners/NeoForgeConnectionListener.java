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

package net.dirtcraft.dirtcore.neoforge_1_21_1.listeners;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.api.event.player.PlayerNegotiationEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeConnectionListener extends AbstractConnectionListener<PlayerNegotiationEvent
        , Connection> {

    private final DirtCoreNeoForgePlugin plugin;

    public NeoForgeConnectionListener(final DirtCoreNeoForgePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SubscribeEvent
    public void onPlayerNegotiation(final PlayerNegotiationEvent event) {
        final GameProfile gameProfile = event.getProfile();
        final String username = gameProfile.getName();
        final UUID uniqueId = gameProfile.getId();

        this.onPlayerNegotiation(event, event.getConnection(), uniqueId, username);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        final ServerPlayer player = (ServerPlayer) event.getEntity();
        final GameProfile profile = player.getGameProfile();

        this.plugin.getStorage().performTask(context -> {
            final User user = this.plugin.getUserManager().getUser(context, profile.getId());

            if (user == null) {
                context.queue(() -> {
                    if (!this.getUniqueConnections().contains(profile.getId())) {
                        this.plugin.getLogger()
                                .warn("User " + profile.getId() + " - " + profile.getName()
                                        + " doesn't have data pre-loaded, they have never been "
                                        + "processed during pre-login in this session.");
                    } else {
                        this.plugin.getLogger()
                                .warn("User " + profile.getId() + " - " + profile.getName()
                                        + " doesn't currently have data pre-loaded, but they have "
                                        + "been processed before in this session.");
                    }

                    final Component component = Components.LOADING_STATE_ERROR.build();
                    player.sendSystemMessage(
                            this.plugin.getPlatformFactory().transformComponent(component));
                });
            }
        });
    }

    @Override
    protected void disconnect(@NonNull final PlayerNegotiationEvent event,
            @NonNull final Connection connection, @NonNull final Component component) {
        final MutableComponent mutableComponent =
                this.plugin.getPlatformFactory().transformComponent(component);

        connection.send(new ClientboundLoginDisconnectPacket(mutableComponent));
        connection.disconnect(mutableComponent);
        event.setCanceled(true);
    }

    @Override
    protected String getIPAddress(@NonNull final Connection connection) {
        return connection.getRemoteAddress().toString();
    }
}
