/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.log;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.actionlog.ActionComparator;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "log")
public class LogEntity implements Action, DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    @Column(nullable = false)
    protected long timestamp;
    @Column(nullable = false)
    @NonNull
    protected String source_uuid;
    @Column(nullable = false)
    @NonNull
    protected String source_server;
    @Column
    @Nullable
    protected String target_uuid;
    @Column(nullable = false)
    @NonNull
    protected String type;
    @Column(nullable = false)
    @NonNull
    protected String authorization;
    @Column
    @Nullable
    protected String title;
    @Column(columnDefinition = "TEXT")
    @Nullable
    protected String description;
    @Nullable
    protected String incident_id;

    protected LogEntity() {}

    public LogEntity(final long timestamp, @NonNull final UUID source,
            @NonNull final String sourceServer, @Nullable final UUID target,
            @NonNull final Type type, @NonNull final Authorization authorization,
            @Nullable final String title, @Nullable final String description,
            @Nullable final String incidentId) {
        this.timestamp = timestamp;
        this.source_uuid = source.toString();
        this.source_server = sourceServer;
        this.target_uuid = target == null ? null : target.toString();
        this.type = type.getIdentifier();
        this.authorization = authorization.getIdentifier();
        this.title = title;
        this.description = description;
        this.incident_id = incidentId;
    }

    @NonNull
    public static Builder builder(final long timestamp, @NonNull final UUID source,
            @NonNull final String sourceServer, @NonNull final Type type,
            @NonNull final Authorization authorization) {
        return new Builder(timestamp, source, sourceServer, type, authorization);
    }

    @NonNull
    public static Builder builder(@NonNull final UUID source, @NonNull final String sourceServer,
            @NonNull final Type type, @NonNull final Authorization authorization) {
        return new Builder(Instant.now().getEpochSecond(), source, sourceServer, type,
                authorization);
    }

    @NonNull
    public static Permission getPermission(@NonNull final Authorization authorization) {
        switch (authorization) {
            case ADMIN:
                return Permission.LOG_NOTIFY_ADMIN;
            case STAFF:
                return Permission.LOG_NOTIFY_STAFF;
            default:
                throw new AssertionError();
        }
    }

    @NonNull
    public static NamedTextColor getTextColor(@NonNull final Type type) {
        switch (type) {
            case ADMIN:
                return NamedTextColor.DARK_RED;
            case STAFF:
                return NamedTextColor.GOLD;
            case BAN:
            case BAN_IP_JOIN:
            case KICK:
            case MUTE:
            case WARN:
                return NamedTextColor.RED;
            case UNBAN:
            case UNMUTE:
                return NamedTextColor.GREEN;
            default:
                throw new AssertionError();
        }
    }

    @Override
    public @NonNull Instant getTimestamp() {
        return Instant.ofEpochSecond(this.timestamp);
    }

    @Override
    public @NonNull UUID getSource() {
        return UUID.fromString(this.source_uuid);
    }

    @Override
    public @NonNull String getSourceServer() {
        return this.source_server;
    }

    @Override
    public @NonNull Optional<UUID> getTarget() {
        return this.target_uuid == null ? Optional.empty()
                : Optional.of(UUID.fromString(this.target_uuid));
    }

    @Override
    @NonNull
    public Type getType() {
        return Type.fromString(this.type);
    }

    @Override
    @NonNull
    public Authorization getAuthorization() {
        return Authorization.fromString(this.authorization);
    }

    @Override
    public @NonNull Optional<String> getTitle() {
        return Optional.ofNullable(this.title);
    }

    @Override
    public @NonNull Optional<String> getDescription() {
        return Optional.ofNullable(this.description);
    }

    @Override
    public @NonNull Optional<String> getIncidentId() {
        return Optional.ofNullable(this.incident_id);
    }

    @Override
    public int compareTo(@NonNull final Action other) {
        Objects.requireNonNull(other, "other");
        return ActionComparator.INSTANCE.compare(this, other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.timestamp, this.source_uuid, this.source_server,
                this.target_uuid, this.type, this.authorization, this.title, this.description,
                this.incident_id);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final LogEntity log = (LogEntity) o;
        return this.id == log.id && this.timestamp == log.timestamp && Objects.equals(
                this.source_uuid, log.source_uuid) && Objects.equals(this.source_server,
                log.source_server) && Objects.equals(this.target_uuid, log.target_uuid)
                && Objects.equals(this.type, log.type) && Objects.equals(this.authorization,
                log.authorization) && Objects.equals(this.title, log.title) && Objects.equals(
                this.description, log.description) && Objects.equals(this.incident_id,
                log.incident_id);
    }

    public static class Builder implements Action.Builder {

        private final long timestamp;
        @NonNull
        private final UUID source;
        @NonNull
        private final String sourceServer;
        @NonNull
        private final Type type;
        @NonNull
        private final Authorization authorization;
        @Nullable
        private UUID target = null;
        @Nullable
        private String title = null;
        @Nullable
        private String description = null;
        @Nullable
        private String incidentId = null;

        private Builder(final long timestamp, @NonNull final UUID source,
                @NonNull final String sourceServer, @NonNull final Type type,
                @NonNull final Authorization authorization) {
            this.timestamp = timestamp;
            this.source = source;
            this.sourceServer = sourceServer;
            this.type = type;
            this.authorization = authorization;
        }

        @Override
        @NonNull
        public Builder target(final UUID target) {
            this.target = target;
            return this;
        }

        @Override
        @NonNull
        public Builder title(final String title) {
            this.title = title;
            return this;
        }

        @Override
        @NonNull
        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        @Override
        @NonNull
        public Builder incidentId(final String incidentId) {
            this.incidentId = incidentId;
            return this;
        }

        @Override
        @NonNull
        public LogEntity build() {
            return new LogEntity(this.timestamp, this.source, this.sourceServer, this.target,
                    this.type, this.authorization, this.title, this.description, this.incidentId);
        }
    }
}
