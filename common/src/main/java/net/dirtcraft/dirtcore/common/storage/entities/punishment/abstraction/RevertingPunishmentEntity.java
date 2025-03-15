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

package net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RevertingPunishmentEntity<T extends ModifiablePunishmentEntity<?>> implements DirtCoreEntity, RestrictiveAction {

    @Id
    @Column(length = 8, nullable = false, unique = true)
    @NonNull
    protected String incident_id;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected Timestamp timestamp;

    @Column(nullable = false, length = 36)
    @NonNull
    protected String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Getter
    @NonNull
    protected String reason;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String server;

    @NonNull
    public abstract T getOriginal();

    @NonNull
    public MessageEmbed getLogEmbed(@NonNull final TaskContext context,
            @NonNull final LogEntity log, @NonNull final User source, @Nullable final User target) {
        return DiscordEmbeds.LOG_REVERT.build(context, log, source, target, this,
                context.plugin().getUserManager().getOrCreateUser(context, this.getAuthor()));
    }

    @Override
    @NonNull
    public String getIncidentId() {
        return this.incident_id;
    }

    @Override
    @NonNull
    public UUID getTarget() {
        return this.getOriginal().getTarget();
    }

    @Override
    @NonNull
    public UUID getAuthor() {
        return UUID.fromString(this.author);
    }
}
