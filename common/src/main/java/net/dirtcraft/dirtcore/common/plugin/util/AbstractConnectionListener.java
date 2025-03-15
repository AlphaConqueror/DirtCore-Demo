/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserIPHistory;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Abstract listener utility for handling new player connections.
 */
public abstract class AbstractConnectionListener<E, C> {

    private final DirtCorePlugin plugin;
    private final Set<UUID> uniqueConnections = ConcurrentHashMap.newKeySet();

    protected AbstractConnectionListener(final DirtCorePlugin plugin) {
        this.plugin = plugin;
    }

    protected abstract void disconnect(@NonNull E event, @NonNull C connection,
            @NonNull Component component);

    protected abstract String getIPAddress(@NonNull C connection);

    /**
     * Gets the unique players which have connected to the server since it started.
     *
     * @return the unique connections
     */
    public Set<UUID> getUniqueConnections() {
        return this.uniqueConnections;
    }

    public void loadUser(@NonNull final TaskContext context, @NonNull final UUID uniqueId,
            @NonNull final String username) {
        final long startTime = System.currentTimeMillis();

        // create user
        this.plugin.getUserManager().createOrUpdateUser(context, uniqueId, username);
        // create settings
        this.plugin.getUserManager().createUserSettingsIfNotExisting(context, uniqueId);
        // create player data
        this.plugin.getUserManager().createPlayerDataIfNotExisting(context, uniqueId);

        context.queue(() -> {
            final long time = System.currentTimeMillis() - startTime;

            if (time >= 1000) {
                this.plugin.getLogger().warn("Processing login for {} took {}ms.", username, time);
            }
        });
    }

    /**
     * On player negotiation.
     *
     * @param event      the event
     * @param connection the connection
     * @param uniqueId   the unique id
     * @param username   the username
     */
    protected void onPlayerNegotiation(@NonNull final E event, @NonNull final C connection,
            @NonNull final UUID uniqueId, @NonNull final String username) {
        try {
            this.recordConnection(uniqueId);

            final AtomicReference<User> source = new AtomicReference<>();
            final AtomicReference<User> target = new AtomicReference<>();

            this.plugin.getStorage().performTask(context -> {
                this.loadUser(context, uniqueId, username);
                source.set(
                        this.plugin.getUserManager().getOrCreateUser(context, Sender.CONSOLE_UUID));
                target.set(this.plugin.getUserManager().getOrCreateUser(context, uniqueId));
            });

            // record IP-Address
            this.plugin.getStorage().performTaskAsync(context -> {
                // IP-Address save
                final String ipAddress = this.parseIPAddress(this.getIPAddress(connection));
                UserIPHistory userIpHistory = this.plugin.getUserManager()
                        .getUserIPHistoryByIP(context, uniqueId, ipAddress);

                if (userIpHistory == null) {
                    userIpHistory = new UserIPHistory(uniqueId, ipAddress);
                    context.session().persist(userIpHistory);
                } else {
                    userIpHistory.setLastSeenNow();
                    userIpHistory.increaseTimesSeen();
                    context.session().merge(userIpHistory);
                }
            });

            final List<CompletableFuture<ConnectionResult>> connectionResultsFutures =
                    new ArrayList<>();

            connectionResultsFutures.add(this.plugin.getStorage().performTaskAsync(context -> {
                // ban check
                final Optional<BanEntity> activeBanOptional =
                        this.plugin.getPunishmentManager().getActiveBan(context, uniqueId);

                if (activeBanOptional.isPresent()) {
                    final BanEntity activeBan = activeBanOptional.get();
                    final User author = this.plugin.getUserManager()
                            .getOrCreateUser(context, activeBan.getAuthor());

                    return ConnectionResult.of(() -> {
                        final LogEntity log = LogEntity.builder(Sender.CONSOLE_UUID,
                                        this.plugin.getServerIdentifier(), Action.Type.STAFF,
                                        Action.Authorization.STAFF).target(uniqueId)
                                .title("Banned user tried to join!")
                                .incidentId(activeBan.getIncidentId()).build();

                        this.disconnect(event, connection,
                                this.plugin.getPlatformFactory().banScreenComponent()
                                        .build(activeBan, author.getName()));
                        this.plugin.getLogDispatcher().dispatch(log, null,
                                DiscordEmbeds.LOG_BANNED_USER_JOIN.build(this.plugin, log,
                                        source.get(), target.get(), activeBan, author),
                                Components.LOG.build(log, source.get(), target.get(), activeBan,
                                        author));
                    });
                }

                return ConnectionResult.empty();
            }));

            connectionResultsFutures.add(this.plugin.getStorage().performTaskAsync(context -> {
                // IP ban check
                // take previous IPs into consideration
                final List<UserIPHistory> userIpHistories =
                        this.plugin.getUserManager().getUserIPHistory(context, uniqueId);
                final List<String> userIps =
                        userIpHistories.stream().map(UserIPHistory::getIpAddress)
                                .collect(Collectors.toList());
                final List<User> users =
                        this.plugin.getUserManager().getUsersByIPs(context, userIps);
                BanPair banPair = null;

                // first sort the bans into IP bans and normal bans
                for (final User user : users) {
                    // skip active ban for current user since it has already been checked
                    if (user.getUniqueId().equals(uniqueId)) {
                        continue;
                    }

                    final Optional<BanEntity> activeUserBanOptional =
                            this.plugin.getPunishmentManager()
                                    .getActiveBan(context, user.getUniqueId());

                    if (activeUserBanOptional.isPresent()) {
                        final BanEntity ban = activeUserBanOptional.get();

                        if (ban.isIpBanned()) {
                            final User author = this.plugin.getUserManager()
                                    .getOrCreateUser(context, ban.getAuthor());

                            return ConnectionResult.of(() -> {
                                final LogEntity log = LogEntity.builder(Sender.CONSOLE_UUID,
                                                this.plugin.getServerIdentifier(),
                                                Action.Type.BAN_IP_JOIN,
                                                Action.Authorization.STAFF).target(uniqueId)
                                        .title("User with banned IP tried to join!")
                                        .incidentId(ban.getIncidentId()).build();

                                this.disconnect(event, connection,
                                        this.plugin.getPlatformFactory().banScreenComponent()
                                                .build(ban, author.getName()));
                                this.plugin.getLogDispatcher().dispatch(log, null,
                                        DiscordEmbeds.LOG_BANNED_IP_USER_JOIN.build(this.plugin,
                                                log, source.get(), target.get(), ban, author, user),
                                        Components.LOG_BANNED_IP_JOIN.build(log, source.get(),
                                                target.get(), ban, author, user));
                            });
                        }

                        if (banPair == null) {
                            // record first match
                            banPair = new BanPair(user, ban);
                        }
                    }
                }

                // notify staff about the first ban match; do not kick user
                if (banPair != null) {
                    final User user = banPair.user;
                    final BanEntity ban = banPair.ban;
                    final User author =
                            this.plugin.getUserManager().getOrCreateUser(context, ban.getAuthor());

                    return ConnectionResult.of(() -> {
                        final LogEntity log = LogEntity.builder(Sender.CONSOLE_UUID,
                                        this.plugin.getServerIdentifier(), Action.Type.BAN_IP_JOIN,
                                        Action.Authorization.STAFF).target(uniqueId)
                                .title("User IP matched the IP of a banned user.")
                                .incidentId(ban.getIncidentId()).build();

                        this.plugin.getLogDispatcher().dispatch(log, null,
                                DiscordEmbeds.LOG_BANNED_IP_USER_JOIN.build(this.plugin, log,
                                        source.get(), target.get(), ban, author, user),
                                Components.LOG_BANNED_IP_JOIN.build(log, source.get(), target.get(),
                                        ban, author, user));
                    });
                }

                return ConnectionResult.empty();
            }));

            for (final CompletableFuture<ConnectionResult> completableFuture :
                    connectionResultsFutures) {
                final ConnectionResult connectionResult = completableFuture.join();

                // execute connection results until a runnable was run
                if (connectionResult.execute()) {
                    break;
                }
            }
        } catch (final Exception ex) {
            this.plugin.getLogger()
                    .severe("Exception occurred whilst loading data for " + uniqueId + " - "
                            + username, ex);
        }
    }

    protected void recordConnection(final UUID uniqueId) {
        this.uniqueConnections.add(uniqueId);
    }

    @NonNull
    protected String parseIPAddress(@NonNull final String s) {
        final String s1 = s.substring(s.indexOf("/") + 1);
        final int index = s1.lastIndexOf(":");

        return index == -1 ? s1 : s1.substring(0, index);
    }

    private static class BanPair {

        private final User user;
        private final BanEntity ban;

        private BanPair(final User user, final BanEntity ban) {
            this.user = user;
            this.ban = ban;
        }
    }

    private static class ConnectionResult {

        @Nullable
        private final Runnable executeLater;

        private ConnectionResult(@Nullable final Runnable executeLater) {
            this.executeLater = executeLater;
        }

        @NonNull
        public static ConnectionResult empty() {
            return new ConnectionResult(null);
        }

        @NonNull
        public static ConnectionResult of(@NonNull final Runnable executeLater) {
            return new ConnectionResult(executeLater);
        }

        public boolean execute() {
            if (this.executeLater == null) {
                return false;
            }

            this.executeLater.run();
            return true;
        }
    }
}
