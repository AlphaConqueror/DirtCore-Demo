/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.manager.punishment;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.KickEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnbanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnmuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.WarnEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages punishment related operations in a thread-safe manner.
 *
 * @param <T> the punishment entity type
 */
public interface PunishmentManager<T extends PunishmentEntity> {

    /**
     * Gets the punishments for target.
     *
     * @param context the context
     * @param target  the target unique id
     * @return the punishments
     */
    @NonNull List<T> getForTarget(@NonNull TaskContext context, @NonNull UUID target);

    /**
     * Gets the active ban for a target.
     *
     * @param context the context
     * @param target  the target unique id
     * @return the active ban, if available
     */
    @NonNull Optional<BanEntity> getActiveBan(@NonNull TaskContext context, @NonNull UUID target);

    /**
     * Gets the active mute for a target.
     *
     * @param context the context
     * @param target  the target unique id
     * @return the active mute, if available
     */
    @NonNull Optional<MuteEntity> getActiveMute(@NonNull TaskContext context, @NonNull UUID target);

    /**
     * Gets the warns for a target after a specific timestamp.
     *
     * @param context   the context
     * @param target    the target
     * @param timestamp the timestamp
     * @return the warns
     */
    @NonNull List<WarnEntity> getWarnsAfter(@NonNull TaskContext context, @NonNull UUID target,
            @NonNull Timestamp timestamp);

    /**
     * Gets the punishment from an incident id.
     *
     * @param context    the context
     * @param incidentId the incident id
     * @return the punishment, if available
     */
    @NonNull Optional<PunishmentEntity> getFromIncidentId(@NonNull TaskContext context,
            @NonNull String incidentId);

    /**
     * Generates the next incident id.
     *
     * @param context the context
     * @return the incident id
     */
    @NonNull String nextIncidentId(@NonNull TaskContext context);

    /**
     * Bans a user.
     *
     * @param context    the context
     * @param incidentId the incident id
     * @param sender     the sender
     * @param source     the source
     * @param target     the target
     * @param reason     the reason
     * @param expiry     the expiry
     * @return the ban
     */
    @NonNull BanEntity ban(@NonNull TaskContext context, @NonNull String incidentId,
            @NonNull Sender sender, @Nullable User source, @NonNull User target,
            @NonNull String reason, @Nullable Instant expiry);

    /**
     * Kicks a user.
     *
     * @param context    the context
     * @param incidentId the incident id
     * @param sender     the sender
     * @param target     the target
     * @param reason     the reason
     * @return the kick
     */
    @NonNull KickEntity kick(@NonNull TaskContext context, @NonNull String incidentId,
            @NonNull Sender sender, @NonNull User target, @NonNull String reason);

    /**
     * Kicks all players related to a ban.
     *
     * @param context    the context
     * @param ban        the ban
     * @param authorName the name of the author
     */
    void kickPlayersMatchingBan(@NonNull TaskContext context, @NonNull BanEntity ban,
            @NonNull String authorName);

    /**
     * Mutes a user.
     *
     * @param context    the context
     * @param incidentId the incident id
     * @param sender     the sender
     * @param source     the source
     * @param target     the target
     * @param reason     the reason
     * @param expiry     the expiry
     * @return the mute
     */
    @NonNull MuteEntity mute(@NonNull TaskContext context, @NonNull String incidentId,
            @NonNull Sender sender, @Nullable User source, @NonNull User target,
            @NonNull String reason, @Nullable Instant expiry);

    /**
     * Unbans a user.
     *
     * @param context    the context
     * @param original   the original
     * @param incidentId the incident id
     * @param sender     the sender
     * @param target     the target
     * @param reason     the reason
     * @return the unban
     */
    @NonNull UnbanEntity unban(@NonNull TaskContext context, @NonNull BanEntity original,
            @NonNull String incidentId, @NonNull Sender sender, @NonNull User target,
            @NonNull String reason);

    /**
     * Unmutes a user.
     *
     * @param context    the context
     * @param original   the original
     * @param incidentId the incident id
     * @param sender     the sender
     * @param target     the target
     * @param reason     the reason
     * @return the unmute
     */
    @NonNull UnmuteEntity unmute(@NonNull TaskContext context, @NonNull MuteEntity original,
            @NonNull String incidentId, @NonNull Sender sender, @NonNull User target,
            @NonNull String reason);

    /**
     * Warns a user.
     *
     * @param context    the context
     * @param incidentId the incident id
     * @param sender     the sender
     * @param target     the target
     * @param reason     the reason
     * @return the warn
     */
    @NonNull WarnEntity warn(@NonNull TaskContext context, @NonNull String incidentId,
            @NonNull Sender sender, @NonNull User target, @NonNull String reason);
}
