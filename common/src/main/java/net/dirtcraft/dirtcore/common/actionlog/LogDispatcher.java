/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.actionlog;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.DiscordManager;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.model.Identifiable;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RestrictiveAction;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class LogDispatcher {

    private final DirtCorePlugin plugin;

    public LogDispatcher(final DirtCorePlugin plugin) {
        this.plugin = plugin;
    }

    public void dispatch(@NonNull final LogEntity log, @Nullable final Sender sender,
            @NonNull final MessageEmbed embed, @NonNull final Component component) {
        this.plugin.getStorage().performTask(context -> {
            context.session().persist(log);

            context.queue(() -> {
                this.plugin.getMessagingService().ifPresent(service -> service.pushLog(log));
                this.broadcast(log, sender, component);
                this.broadcastToDiscord(log, embed);
            });
        });
    }

    public void broadcastFromApi(@NonNull final LogEntity log) {
        this.plugin.getMessagingService()
                .ifPresent(extendedMessagingService -> extendedMessagingService.pushLog(log));
        this.dispatchFromRemote(log);
    }

    public void dispatchFromRemote(@NonNull final LogEntity log) {
        this.plugin.getStorage().performTask(context -> {
            final User source =
                    this.plugin.getUserManager().getOrCreateUser(context, log.getSource());
            final AtomicReference<User> target = new AtomicReference<>();
            final AtomicReference<RestrictiveAction> restrictiveAction = new AtomicReference<>();
            final AtomicReference<User> author = new AtomicReference<>();
            final AtomicReference<User> punishmentTarget = new AtomicReference<>();

            if (log.getTarget().isPresent()) {
                target.set(this.plugin.getUserManager()
                        .getOrCreateUser(context, log.getTarget().get()));
            }

            if (log.getIncidentId().isPresent()) {
                final String incidentId = log.getIncidentId().get();
                final Optional<PunishmentEntity> punishmentOptional =
                        this.plugin.getPunishmentManager().getFromIncidentId(context, incidentId);

                if (!punishmentOptional.isPresent()) {
                    context.queue(() -> this.plugin.getLogger()
                            .warn("Punishment with id '{}' not found.", incidentId));
                    return;
                }

                final RestrictiveAction currentRestrictiveAction =
                        this.getRestrictiveAction(punishmentOptional.get());

                restrictiveAction.set(currentRestrictiveAction);
                author.set(this.plugin.getUserManager()
                        .getOrCreateUser(context, currentRestrictiveAction.getAuthor()));
                punishmentTarget.set(this.plugin.getUserManager()
                        .getOrCreateUser(context, currentRestrictiveAction.getTarget()));
            }

            context.queue(() -> {
                final Component component;

                switch (log.getType()) {
                    case BAN_IP_JOIN:
                        component = Components.LOG_BANNED_IP_JOIN.build(log, source, target.get(),
                                restrictiveAction.get(), author.get(), punishmentTarget.get());
                        break;
                    default:
                        component = Components.LOG.build(log, source, target.get(),
                                restrictiveAction.get(), author.get());
                        break;
                }

                this.broadcast(log, null, component);
            });
        });
    }

    public void dispatchPunishmentExempt(@NonNull final Sender sender, @NonNull final User source,
            @NonNull final Identifiable target, @NonNull final String description) {
        final LogEntity log =
                LogEntity.builder(sender.getUniqueId(), this.plugin.getServerIdentifier(),
                                Action.Type.ADMIN, Action.Authorization.ADMIN).target(target.getUniqueId())
                        .title("Punishment exemption check.").description(description).build();

        this.dispatch(log, sender, DiscordEmbeds.LOG.build(this.plugin, log, source, target),
                Components.LOG.build(log, source, target, null, null));
    }

    @NonNull
    private RestrictiveAction getRestrictiveAction(@NonNull final PunishmentEntity punishment) {
        RestrictiveAction currentRestrictiveAction = punishment;

        if (currentRestrictiveAction instanceof ExpirablePunishmentEntity) {
            final ExpirablePunishmentEntity<?, ?> expirablePunishment =
                    (ExpirablePunishmentEntity<?, ?>) currentRestrictiveAction;

            if (expirablePunishment.isReverted()) {
                assert expirablePunishment.getReverting() != null;
                currentRestrictiveAction = expirablePunishment.getReverting();
            }
        }
        return currentRestrictiveAction;
    }

    private void broadcast(@NonNull final LogEntity log, @Nullable final Sender sender,
            @NonNull final Component component) {
        this.plugin.getPlatformFactory().getOnlineSenders()
                .filter(s -> LogEntity.getPermission(log.getAuthorization()).isAuthorized(s))
                .filter(s -> sender == null || !s.getUniqueId().equals(sender.getUniqueId()))
                .forEach(s -> s.sendMessage(component));
    }

    private void broadcastToDiscord(@NonNull final LogEntity log,
            @NonNull final MessageEmbed embed) {
        final Optional<DiscordManager> discordManagerOptional =
                this.plugin.getDiscordBotClient().map(DiscordBotClient::getDiscordManager);

        if (!discordManagerOptional.isPresent()) {
            return;
        }

        final DiscordManager discordManager = discordManagerOptional.get();
        final Optional<TextChannel> channel;

        switch (log.getAuthorization()) {
            case ADMIN:
                channel = discordManager.getAdminLogChannel();
                break;
            case STAFF:
                channel = discordManager.getStaffLogChannel();
                break;
            default:
                channel = Optional.empty();
                break;
        }

        channel.ifPresent(c -> c.sendMessageEmbeds(embed).queue());
    }
}
