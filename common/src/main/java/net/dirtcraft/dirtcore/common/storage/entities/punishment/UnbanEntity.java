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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RevertingPunishmentEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "unbans")
public class UnbanEntity extends RevertingPunishmentEntity<BanEntity> {

    @NonNull
    @OneToOne
    protected BanEntity original;

    protected UnbanEntity() {}

    public UnbanEntity(@NonNull final BanEntity original, @NonNull final UUID author,
            @NonNull final String reason, @NonNull final String server) {
        this.incident_id = original.getIncidentId();
        this.timestamp = Timestamp.from(Instant.now());
        this.author = author.toString();
        this.reason = reason;
        this.server = server;
        this.original = original;
    }

    @Override
    public Action.@NonNull Type getType() {
        return Action.Type.UNBAN;
    }

    @Override
    public @NonNull BanEntity getOriginal() {
        return this.original;
    }
}
