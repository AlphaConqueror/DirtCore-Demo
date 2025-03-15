/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.listeners;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.kyori.adventure.text.Component;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeConnectionListener extends AbstractConnectionListener<PlayerNegotiationEvent,
        Connection> {

    private final DirtCoreForgePlugin plugin;

    public ForgeConnectionListener(final DirtCoreForgePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SubscribeEvent
    public void onPlayerNegotiation(final PlayerNegotiationEvent event) {
        final String username = event.getProfile().getName();
        final UUID uniqueId = event.getProfile().isComplete() ? event.getProfile().getId()
                : UUIDUtil.createOfflinePlayerUUID(username);

        this.onPlayerNegotiation(event, event.getConnection(), uniqueId, username);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoadFromFile(final PlayerEvent.LoadFromFile event) {
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
    }

    @Override
    protected String getIPAddress(@NonNull final Connection connection) {
        return connection.getRemoteAddress().toString();
    }
}
