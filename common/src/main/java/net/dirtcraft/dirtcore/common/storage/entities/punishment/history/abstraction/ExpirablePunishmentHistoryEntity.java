/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
public abstract class ExpirablePunishmentHistoryEntity<T extends ExpirablePunishmentEntity<?, ?>> extends PunishmentHistoryEntity<T> {

    @Column
    @Nullable
    protected Timestamp old_expiry;

    public Timestamp getOldExpiry() {
        return this.old_expiry;
    }
}
