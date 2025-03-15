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
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = "dirtcore_votedata")
public class VoteDataEntity {

    @Id
    @Column(name = "unique_id", unique = true, nullable = false, length = 36)
    protected String uniqueId;

    @Column(name = "total_votes", nullable = false)
    @Getter
    protected int totalVotes;

    @Column(name = "claimed_votes", nullable = false)
    @Getter
    protected int claimedVotes;

    @Column(name = "vote_streak", nullable = false)
    @Getter
    protected int voteStreak;

    @Column(name = "claimed_vote_streak", nullable = false)
    @Getter
    protected int claimedVoteStreak;

    @Column(name = "last_vote", nullable = false)
    @Getter
    @NonNull
    protected Timestamp lastVote;

    protected VoteDataEntity() {}

    public VoteDataEntity(final UUID uniqueId) {
        this.uniqueId = uniqueId.toString();
        this.totalVotes = 1;
        this.claimedVotes = 0;
        this.voteStreak = 1;
        this.claimedVoteStreak = 0;
        this.lastVote = Timestamp.from(Instant.now());
    }

    public void increaseTotalVotes() {
        this.totalVotes += 1;
    }

    public int getUnclaimedVotes() {
        return this.totalVotes - this.claimedVotes;
    }

    public void claim(final int amount) {
        if (amount > this.getUnclaimedVotes()) {
            throw new IllegalArgumentException("More claims than actual votes.");
        }

        this.claimedVotes += amount;
        this.claimedVoteStreak = Math.min(this.claimedVoteStreak + amount, this.voteStreak);
    }

    public int getUnclaimedStreakAmount() {
        return this.voteStreak - this.claimedVoteStreak;
    }

    public void resetVoteStreak() {
        this.voteStreak = 1;
        this.claimedVoteStreak = 0;
    }

    public void increaseVoteStreak() {
        this.voteStreak += 1;
    }

    public void setLastVote(@NonNull final Instant instant) {
        this.lastVote = Timestamp.from(instant);
    }
}
