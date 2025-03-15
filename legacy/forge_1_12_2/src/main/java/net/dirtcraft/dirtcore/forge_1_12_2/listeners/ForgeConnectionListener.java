/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.listeners;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.api.event.player.PlayerNegotiationEvent;
import net.kyori.adventure.text.Component;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeConnectionListener extends AbstractConnectionListener<PlayerNegotiationEvent,
        NetworkManager> {

    private final DirtCoreForgePlugin plugin;

    public ForgeConnectionListener(final DirtCoreForgePlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @SubscribeEvent
    public void onPlayerNegotiation(final PlayerNegotiationEvent event) {
        final String username = event.getProfile().getName();
        final UUID uniqueId = event.getProfile().isComplete() ? event.getProfile().getId()
                : UUID.nameUUIDFromBytes(
                        ("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));

        this.onPlayerNegotiation(event, event.getConnection(), uniqueId, username);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerLoadFromFile(final PlayerEvent.LoadFromFile event) {
        final EntityPlayer player = event.getEntityPlayer();
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
                    player.sendMessage(
                            this.plugin.getPlatformFactory().transformComponent(component));
                });
            }
        });
    }

    @Override
    protected void disconnect(@NonNull final PlayerNegotiationEvent event,
            @NonNull final NetworkManager connection, @NonNull final Component component) {
        final ITextComponent textComponent =
                this.plugin.getPlatformFactory().transformComponent(component);

        //noinspection unchecked
        connection.sendPacket(new SPacketDisconnect(textComponent),
                future -> connection.closeChannel(textComponent));
        connection.disableAutoRead();
        event.setCanceled(true);
    }

    @Override
    protected String getIPAddress(@NonNull final NetworkManager connection) {
        return connection.getRemoteAddress().toString();
    }
}
