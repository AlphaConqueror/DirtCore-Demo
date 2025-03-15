/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.kit;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "kit_claim_entries")
public class KitClaimEntryEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected KitEntity original;

    @Column(nullable = false, length = 36)
    @NonNull
    protected String target;

    @SuppressWarnings("NotNullFieldNotInitialized")
    @Column(nullable = false)
    @Getter
    @NonNull
    protected Timestamp timestamp;

    protected KitClaimEntryEntity() {}

    public KitClaimEntryEntity(@NonNull final KitEntity original, @NonNull final UUID target) {
        this.original = original;
        this.target = target.toString();
        this.setTimestampNow();
    }

    public void setTimestampNow() {
        this.timestamp = Timestamp.from(Instant.now());
    }
}
