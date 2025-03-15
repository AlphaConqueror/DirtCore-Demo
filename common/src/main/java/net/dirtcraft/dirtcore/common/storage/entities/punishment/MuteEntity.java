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

package net.dirtcraft.dirtcore.common.storage.entities.punishment;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Setter;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RevertingPunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.MuteHistoryEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "mutes")
public class MuteEntity extends ExpirablePunishmentEntity<MuteEntity, MuteHistoryEntity> {

    @OneToMany(mappedBy = "original")
    protected List<MuteHistoryEntity> history;

    @Nullable
    @OneToOne
    @Setter
    protected UnmuteEntity unmute;

    protected MuteEntity() {}

    public MuteEntity(@NonNull final String incidentId, @NonNull final UUID target,
            @NonNull final UUID author, @NonNull final String reason, @NonNull final String server,
            @Nullable final Instant expiry) {
        this.incident_id = incidentId;
        this.timestamp = Timestamp.from(Instant.now());
        this.target = target.toString();
        this.author = author.toString();
        this.reason = reason;
        this.server = server;
        this.expiry = expiry == null ? null : Timestamp.from(expiry);
        this.history = Collections.emptyList();
        this.unmute = null;
    }

    @Override
    public Action.@NonNull Type getType() {
        return Action.Type.MUTE;
    }

    @Override
    public @NonNull List<MuteHistoryEntity> getHistory() {
        return this.history;
    }

    @Override
    public @Nullable RevertingPunishmentEntity<MuteEntity> getReverting() {
        return this.unmute;
    }
}
