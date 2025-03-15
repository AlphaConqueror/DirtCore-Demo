/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api.actionlog;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a logged action.
 */
public interface Action extends Comparable<Action> {

    /**
     * Gets the time when the action occurred.
     *
     * @return the timestamp
     */
    @NonNull Instant getTimestamp();

    /**
     * Gets the source unique id.
     *
     * @return the source unique id
     */
    @NonNull UUID getSource();

    /**
     * Gets the source server.
     *
     * @return the source server
     */
    @NonNull String getSourceServer();

    /**
     * Gets the target unique id.
     *
     * @return the target unique id, if present.
     */
    @NonNull Optional<UUID> getTarget();

    /**
     * Gets the type of the action.
     *
     * @return the type
     */
    @NonNull Type getType();

    /**
     * Gets the authorization needed to receive the action.
     *
     * @return the authorization
     */
    @NonNull Authorization getAuthorization();

    /**
     * Returns a string describing the title of the action.
     *
     * @return the title, if present.
     */
    @NonNull Optional<String> getTitle();

    /**
     * Returns a string describing the action which took place.
     *
     * @return the description, if present
     */
    @NonNull Optional<String> getDescription();

    /**
     * Returns an incident id related to this log.
     *
     * @return the incident id, if present
     */
    @NonNull Optional<String> getIncidentId();

    /**
     * The type of action.
     */
    enum Type {

        /**
         * Admin type action.
         */
        ADMIN("admin", "Admin"),
        /**
         * Ban action.
         */
        BAN("ban", "Ban"),
        /**
         * Banned user tried to join actions.
         */
        BAN_IP_JOIN("ban_ip_join", "Staff"),
        /**
         * Ban actions.
         */
        KICK("kick", "Kick"),
        /**
         * Mute action.
         */
        MUTE("mute", "Mute"),
        /**
         * Staff type action.
         */
        STAFF("staff", "Staff"),
        /**
         * Unban action.
         */
        UNBAN("unban", "Unban"),
        /**
         * Unmute action.
         */
        UNMUTE("unmute", "Unmute"),
        /**
         * Warn action.
         */
        WARN("warn", "Warn");

        @NonNull
        private final String identifier;
        @NonNull
        private final String text;

        Type(@NonNull final String identifier, @NonNull final String text) {
            this.identifier = identifier;
            this.text = text;
        }

        /**
         * String to type.
         *
         * @param id the string
         * @return the type
         */
        @NonNull
        public static Type fromString(@NonNull final String id) {
            return Arrays.stream(values()).filter(t -> t.identifier.equalsIgnoreCase(id))
                    .findFirst().orElseThrow(AssertionError::new);
        }

        /**
         * Gets the identifier.
         *
         * @return the identifier
         */
        @NonNull
        public String getIdentifier() {
            return this.identifier;
        }

        /**
         * Gets the text.
         *
         * @return the text
         */
        @NonNull
        public String getText() {
            return this.text;
        }
    }

    /**
     * The type of users who are supposed to see this action.
     */
    enum Authorization {

        /**
         * Authorization for admins.
         */
        ADMIN("admin"),
        /**
         * Authorization for all staff.
         */
        STAFF("staff");

        @NonNull
        private final String identifier;

        Authorization(@NonNull final String identifier) {
            this.identifier = identifier;
        }


        /**
         * String to authorization.
         *
         * @param id the string
         * @return the authorization
         */
        @NonNull
        public static Authorization fromString(@NonNull final String id) {
            return Arrays.stream(values()).filter(t -> t.identifier.equalsIgnoreCase(id))
                    .findFirst().orElseThrow(AssertionError::new);
        }

        /**
         * Gets the identifier.
         *
         * @return the identifier
         */
        @NonNull
        public String getIdentifier() {
            return this.identifier;
        }
    }

    /**
     * Builds an {@link Action} instance
     */
    interface Builder {

        /**
         * Sets the target for the entry.
         *
         * @param target the target
         * @return the builder
         */
        @NonNull Builder target(UUID target);

        /**
         * Sets the title of the action.
         *
         * @param action the action
         * @return the builder
         */
        @NonNull Builder title(String action);

        /**
         * Sets the action of the entry.
         *
         * @param action the action
         * @return the builder
         */
        @NonNull Builder description(String action);

        /**
         * Sets an incident id related to this log.
         *
         * @param incidentId the incident id
         * @return the builder
         */
        @NonNull Builder incidentId(String incidentId);

        /**
         * Creates a {@link Action} instance from the builder.
         *
         * @return a new log entry instance
         */
        @NonNull Action build();
    }
}
