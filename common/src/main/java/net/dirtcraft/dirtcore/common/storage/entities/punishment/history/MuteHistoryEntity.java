/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.history;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.ExpirablePunishmentHistoryEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "mute_history")
public class MuteHistoryEntity extends ExpirablePunishmentHistoryEntity<MuteEntity> {

    @ManyToOne
    protected MuteEntity original;

    protected MuteHistoryEntity() {}

    public MuteHistoryEntity(@NonNull final MuteEntity original) {
        this.original = original;
        this.old_timestamp = original.getTimestamp();
        this.old_author = original.getAuthor().toString();
        this.old_reason = original.getReason();
        this.old_server = original.getServer();
        this.old_expiry = original.getExpiry().orElse(null);
    }

    @Override
    public @NonNull MuteEntity getOriginal() {
        return this.original;
    }
}
