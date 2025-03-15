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
