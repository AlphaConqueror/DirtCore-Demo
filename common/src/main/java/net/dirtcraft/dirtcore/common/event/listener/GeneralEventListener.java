/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.event.listener;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import net.dirtcraft.dirtcore.api.event.EventBus;
import net.dirtcraft.dirtcore.common.event.DirtCoreEventListener;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerDeathEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLoginEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLogoutEvent;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.player.PlayerDataEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.event.PostOrders;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GeneralEventListener implements DirtCoreEventListener {

    private final DirtCorePlugin plugin;
    private final Lock lock = new ReentrantLock();
    @Nullable
    private UUID lastDeathUniqueId = null;

    public GeneralEventListener(final DirtCorePlugin plugin) {this.plugin = plugin;}

    public void onPlayerDeath(final PlayerDeathEvent event) {
        try {
            this.lock.lock();
            this.lastDeathUniqueId = event.getUniqueId();
        } finally {
            this.lock.unlock();
        }
    }

    public void onPlayerLogin(final PlayerLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();

        this.plugin.getStorage().performTask(context -> {
            final PlayerDataEntity playerData =
                    this.plugin.getUserManager().getOrCreatePlayerData(context, uniqueId);
            final User user = this.plugin.getUserManager().getOrCreateUser(context, uniqueId);
            final Component component;

            if (playerData.getLastSeen().isPresent()) {
                component = Component.text()
                        .append(Components.JOIN_MESSAGE_DEFAULT.build(user.formatDisplay(context)))
                        .appendSpace()
                        .append(this.plugin.getMessagingManager()
                                .getJoinMessageFormattedOrDefault(context, user)).build();
            } else {
                component = Component.text()
                        .append(Components.WELCOME_MESSAGE_DEFAULT.build(this.plugin,
                                user.formatDisplay(context))).build();
            }

            this.plugin.getPlatformFactory().broadcast(component);
        });
    }

    public void onPlayerLoginMonitor(final PlayerLoginEvent event) {
        final UUID uniqueId = event.getUniqueId();

        this.plugin.getStorage().performTaskAsync(context -> {
            this.plugin.getUserManager().getOrCreateUser(context, uniqueId).setLastSeenNow(context);

            final PlayerDataEntity playerData =
                    this.plugin.getUserManager().getOrCreatePlayerData(context, uniqueId);

            playerData.setLastSeenNow();
            context.session().merge(playerData);
        });
    }

    public void onPlayerLogout(final PlayerLogoutEvent event) {
        final UUID uniqueId = event.getUniqueId();

        this.plugin.getStorage().performTaskAsync(context -> {
            final User user = this.plugin.getUserManager().getOrCreateUser(context, uniqueId);
            user.setLastSeenNow(context);

            final PlayerDataEntity playerData =
                    this.plugin.getUserManager().getOrCreatePlayerData(context, uniqueId);
            playerData.setLastSeenNow();
            context.session().merge(playerData);

            final TextComponent.Builder builder = Component.text()
                    .append(Components.LEAVE_MESSAGE_DEFAULT.build(user.formatDisplay(context)))
                    .appendSpace()
                        .append(this.plugin.getMessagingManager()
                            .getLeaveMessageFormattedOrDefault(context, user));
            this.plugin.getPlatformFactory().broadcast(builder.build());
        });
    }

    @Override
    public void bind(final EventBus bus) {
        bus.subscribe(PlayerDeathEvent.class, this::onPlayerDeath);
        bus.subscribe(PlayerLoginEvent.class, this::onPlayerLogin);
        bus.subscribe(PlayerLoginEvent.class, this::onPlayerLoginMonitor, PostOrders.LAST);
        bus.subscribe(PlayerLogoutEvent.class, this::onPlayerLogout);
    }

    @NonNull
    public Optional<UUID> getLastDeathUniqueId() {
        try {
            this.lock.lock();
            return Optional.ofNullable(this.lastDeathUniqueId);
        } finally {
            this.lock.unlock();
        }
    }
}
