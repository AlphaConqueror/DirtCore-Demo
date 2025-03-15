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
