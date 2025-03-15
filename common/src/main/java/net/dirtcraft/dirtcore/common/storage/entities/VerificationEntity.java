/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.manager.vote.VerificationManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = "dirtcore_verification")
public class VerificationEntity {

    @Id
    @Column(name = "discord_user_id")
    @Getter
    protected long discordUserId;

    @Column(name = "unique_id", unique = true, length = 36)
    @Nullable
    protected String uniqueId;

    @Column(length = 8)
    @Getter
    @Nullable
    protected String code;

    @Column(nullable = false)
    protected Timestamp timestamp;

    protected VerificationEntity() {}

    public VerificationEntity(final long discordUserId, @NonNull final UUID minecraftUniqueId) {
        this.discordUserId = discordUserId;
        this.uniqueId = minecraftUniqueId.toString();
        this.setTimestampNow();
    }

    public VerificationEntity(final long discordUserId, @NonNull final String code) {
        this.discordUserId = discordUserId;
        this.setCode(code);
    }

    public boolean isLinked() {
        return this.uniqueId != null;
    }

    public void link(@NonNull final UUID uniqueId) {
        this.uniqueId = uniqueId.toString();
        this.code = null;
        this.setTimestampNow();
    }

    @NonNull
    public Optional<UUID> getUniqueId() {
        return this.uniqueId == null ? Optional.empty()
                : Optional.of(UUID.fromString(this.uniqueId));
    }

    public void setCode(@NonNull final String code) {
        this.code = code;
        this.setTimestampNow();
    }

    @NonNull
    public Instant getTimestamp() {
        return this.timestamp.toInstant();
    }

    public boolean codeAboutToExpire() {
        return this.getTimestamp().plus(VerificationManager.EXPIRY_MINUTES
                        - VerificationManager.ABOUT_TO_EXPIRE_MINUTES, ChronoUnit.MINUTES)
                .isBefore(Instant.now());
    }

    private void setTimestampNow() {
        this.timestamp = Timestamp.from(Instant.now());
    }
}
