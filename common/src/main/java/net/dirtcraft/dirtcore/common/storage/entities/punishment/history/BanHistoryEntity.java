/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.punishment.history;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.abstraction.ExpirablePunishmentHistoryEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "ban_history")
public class BanHistoryEntity extends ExpirablePunishmentHistoryEntity<BanEntity> {

    @ManyToOne
    protected BanEntity original;

    @Column(nullable = false)
    protected boolean old_ip_banned;

    protected BanHistoryEntity() {}

    public BanHistoryEntity(@NonNull final BanEntity original) {
        this.original = original;
        this.old_timestamp = original.getTimestamp();
        this.old_author = original.getAuthor().toString();
        this.old_reason = original.getReason();
        this.old_server = original.getServer();
        this.old_expiry = original.getExpiry().orElse(null);
        this.old_ip_banned = original.isIpBanned();
    }

    @Override
    public @NonNull BanEntity getOriginal() {
        return this.original;
    }

    public boolean wasIpBanned() {
        return this.old_ip_banned;
    }
}
