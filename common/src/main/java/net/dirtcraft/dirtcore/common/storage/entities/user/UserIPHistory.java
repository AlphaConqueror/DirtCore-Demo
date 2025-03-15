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
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "user_ip_history")
public class UserIPHistory implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false, length = 36)
    @Getter
    protected String target;

    @Column(name = "ip_address", nullable = false)
    @Getter
    @NonNull
    protected String ipAddress;

    @Column(name = "first_seen", nullable = false)
    @Getter
    @NonNull
    protected Timestamp firstSeen;

    @Column(name = "last_seen", nullable = false)
    @Getter
    @NonNull
    protected Timestamp lastSeen;

    @Column(name = "times_seen", nullable = false)
    @Getter
    protected long timesSeen;

    protected UserIPHistory() {}

    public UserIPHistory(@NonNull final UUID target, @NonNull final String ipAddress) {
        final Timestamp now = Timestamp.from(Instant.now());

        this.target = target.toString();
        this.ipAddress = ipAddress;
        this.firstSeen = now;
        this.lastSeen = now;
        this.timesSeen = 1;
    }

    public void setLastSeenNow() {
        this.lastSeen = Timestamp.from(Instant.now());
    }

    public void increaseTimesSeen() {
        this.timesSeen += 1;
    }
}
