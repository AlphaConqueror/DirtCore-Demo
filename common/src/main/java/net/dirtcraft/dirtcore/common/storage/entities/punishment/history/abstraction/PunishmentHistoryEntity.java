/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ModifiablePunishmentEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class PunishmentHistoryEntity<T extends ModifiablePunishmentEntity<?>> implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(nullable = false)
    @NonNull
    protected Timestamp old_timestamp;

    @Column(nullable = false, length = 36)
    @NonNull
    protected String old_author;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NonNull
    protected String old_reason;

    @Column(nullable = false)
    @NonNull
    protected String old_server;

    @NonNull
    public abstract T getOriginal();

    @NonNull
    public Timestamp getOldTimestamp() {
        return this.old_timestamp;
    }

    @NonNull
    public UUID getOldAuthor() {
        return UUID.fromString(this.old_author);
    }

    @NonNull
    public String getOldReason() {
        return this.old_reason;
    }

    @NonNull
    public String getOldServer() {
        return this.old_server;
    }
}
