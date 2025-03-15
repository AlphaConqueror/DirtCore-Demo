/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Table;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "kicks")
public class KickEntity extends PunishmentEntity {

    protected KickEntity() {}

    public KickEntity(@NonNull final String incidentId, @NonNull final UUID target,
            @NonNull final UUID author, @NonNull final String reason,
            @NonNull final String server) {
        this.incident_id = incidentId;
        this.timestamp = Timestamp.from(Instant.now());
        this.target = target.toString();
        this.author = author.toString();
        this.reason = reason;
        this.server = server;
    }

    @Override
    public Action.@NonNull Type getType() {
        return Action.Type.KICK;
    }
}
