/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.user;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "users")
public class UserEntity implements DirtCoreEntity {

    @Id
    @Column(name = "unique_id", length = 36, nullable = false, unique = true)
    @Getter
    @NonNull
    protected String uniqueId;

    @Column(length = 36, nullable = false)
    @Getter
    @NonNull
    @Setter
    protected String username;

    @Column(name = "last_seen")
    @Nullable
    protected Timestamp lastSeen;

    protected UserEntity() {}

    public UserEntity(@NonNull final UUID uniqueId, @NonNull final String username) {
        this.uniqueId = uniqueId.toString();
        this.username = username;
        this.lastSeen = null;
    }

    @NonNull
    public Optional<Timestamp> getLastSeen() {
        return Optional.ofNullable(this.lastSeen);
    }

    public void setLastSeenNow() {
        this.lastSeen = Timestamp.from(Instant.now());
    }
}
