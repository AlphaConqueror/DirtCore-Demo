/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
