/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RevertingPunishmentEntity<T extends ModifiablePunishmentEntity<?>> implements DirtCoreEntity, RestrictiveAction {

    @Id
    @Column(length = 8, nullable = false, unique = true)
    @NonNull
    protected String incident_id;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected Timestamp timestamp;

    @Column(nullable = false, length = 36)
    @NonNull
    protected String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @NonNull
    protected String reason;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String server;

    @NonNull
    public abstract T getOriginal();

    @NonNull
    public MessageEmbed getLogEmbed(@NonNull final TaskContext context,
            @NonNull final LogEntity log, @NonNull final User source, @Nullable final User target) {
        return DiscordEmbeds.LOG_REVERT.build(context, log, source, target, this,
                context.plugin().getUserManager().getOrCreateUser(context, this.getAuthor()));
    }

    @Override
    @NonNull
    public String getIncidentId() {
        return this.incident_id;
    }

    @Override
    @NonNull
    public UUID getTarget() {
        return this.getOriginal().getTarget();
    }

    @Override
    @NonNull
    public UUID getAuthor() {
        return UUID.fromString(this.author);
    }
}
