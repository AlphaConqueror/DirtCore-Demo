CREATE TABLE `dirtcore_ban_history`
(
    `id`                   bigint(20)   NOT NULL,
    `old_author`           varchar(36)  NOT NULL,
    `old_reason`           text         NOT NULL,
    `old_server`           varchar(255) NOT NULL,
    `old_timestamp`        datetime     NOT NULL,
    `old_expiry`           datetime   DEFAULT NULL,
    `old_ip_banned`        bit(1)       NOT NULL,
    `original_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_ban_history_original_incident_id` (`original_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_bans`
(
    `incident_id`       varchar(8)   NOT NULL,
    `author`            varchar(36)  NOT NULL,
    `reason`            text         NOT NULL,
    `server`            varchar(255) NOT NULL,
    `target`            varchar(36)  NOT NULL,
    `timestamp`         datetime     NOT NULL,
    `expiry`            datetime   DEFAULT NULL,
    `ip_banned`         bit(1)       NOT NULL,
    `unban_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_bans_incident_id` (`incident_id`),
    KEY `fk_bans_unban_incident_id` (`unban_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_chat_marker`
(
    `name`        varchar(255) NOT NULL,
    `description` text         NOT NULL,
    `display`     text         NOT NULL,
    PRIMARY KEY (`name`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crate_content_commands`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `command`     varchar(255) NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crate_content_commands_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crate_content_items`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier`     varchar(255) NOT NULL,
    `persistentData` text       DEFAULT NULL,
    `original_id`    bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crate_content_items_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crate_contents`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `give_item`   bit(1)     NOT NULL,
    `max_amount`  int(11)    NOT NULL,
    `min_amount`  int(11)    NOT NULL,
    `tickets`     int(11)    NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crate_contents_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crate_keys`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier`     varchar(255) NOT NULL,
    `persistentData` text       DEFAULT NULL,
    `original_id`    bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crate_keys_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crate_locations`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `world`       varchar(255) NOT NULL,
    `x`           int(11)      NOT NULL,
    `y`           int(11)      NOT NULL,
    `z`           int(11)      NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crate_locations_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_crates`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `display_name`     varchar(255) NOT NULL,
    `should_broadcast` bit(1)       NOT NULL,
    `name`             varchar(255) NOT NULL,
    `server`           varchar(255) NOT NULL,
    `key_id`           bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_crates_key_id` (`key_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_kicks`
(
    `incident_id` varchar(8)   NOT NULL,
    `author`      varchar(36)  NOT NULL,
    `reason`      text         NOT NULL,
    `server`      varchar(255) NOT NULL,
    `target`      varchar(36)  NOT NULL,
    `timestamp`   datetime     NOT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_kicks_incident_id` (`incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_kit_claim_entries`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT,
    `target`      varchar(36) NOT NULL,
    `timestamp`   datetime    NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_kit_claim_entries_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_kit_items`
(
    `id`             bigint(20)   NOT NULL,
    `identifier`     varchar(255) NOT NULL,
    `persistentData` text       DEFAULT NULL,
    `stack_size`     int(11)      NOT NULL,
    `original_id`    bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_kit_items_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_kits`
(
    `id`           bigint(20)   NOT NULL AUTO_INCREMENT,
    `cooldown`     bigint(20) DEFAULT NULL,
    `display_name` varchar(255) NOT NULL,
    `name`         varchar(255) NOT NULL,
    `server`       varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_limited_block_entries`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `unique_id`   varchar(255) NOT NULL,
    `world`       varchar(255) NOT NULL,
    `x`           int(11)      NOT NULL,
    `y`           int(11)      NOT NULL,
    `z`           int(11)      NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_limited_block_entries_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_limited_block_rules`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `amount`      bigint(20)   NOT NULL,
    `rule`        varchar(255) NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_limited_block_rules_original_id` (`original_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_limited_blocks`
(
    `id`         bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier` varchar(255) DEFAULT NULL,
    `reason`     text         DEFAULT NULL,
    `server`     varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_log`
(
    `id`            bigint(20)   NOT NULL AUTO_INCREMENT,
    `authorization` varchar(255) NOT NULL,
    `description`   text         DEFAULT NULL,
    `incident_id`   varchar(255) DEFAULT NULL,
    `source_server` varchar(255) NOT NULL,
    `source_uuid`   varchar(255) NOT NULL,
    `target_uuid`   varchar(255) DEFAULT NULL,
    `timestamp`     bigint(20)   NOT NULL,
    `title`         varchar(255) DEFAULT NULL,
    `type`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_messenger`
(
    `id`   int(11)   NOT NULL AUTO_INCREMENT,
    `time` timestamp NOT NULL,
    `msg`  text      NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_mute_history`
(
    `id`                   bigint(20)   NOT NULL,
    `old_author`           varchar(36)  NOT NULL,
    `old_reason`           text         NOT NULL,
    `old_server`           varchar(255) NOT NULL,
    `old_timestamp`        datetime     NOT NULL,
    `old_expiry`           datetime   DEFAULT NULL,
    `original_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `fk_mute_history_original_incident_id` (`original_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_mutes`
(
    `incident_id`        varchar(8)   NOT NULL,
    `author`             varchar(36)  NOT NULL,
    `reason`             text         NOT NULL,
    `server`             varchar(255) NOT NULL,
    `target`             varchar(36)  NOT NULL,
    `timestamp`          datetime     NOT NULL,
    `expiry`             datetime   DEFAULT NULL,
    `unmute_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_mutes_incident_id` (`incident_id`),
    KEY `fk_mutes_unmute_incident_id` (`unmute_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_player_data`
(
    `id`                 bigint(20)   NOT NULL AUTO_INCREMENT,
    `active_chat_marker` varchar(255) DEFAULT NULL,
    `active_prefix`      varchar(255) DEFAULT NULL,
    `balance`            double       NOT NULL,
    `join_message`       varchar(255) DEFAULT NULL,
    `last_seen`          datetime     DEFAULT NULL,
    `leave_message`      varchar(255) DEFAULT NULL,
    `server`             varchar(255) NOT NULL,
    `unique_id`          varchar(36)  NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_prefix`
(
    `name`        varchar(255) NOT NULL,
    `description` text         NOT NULL,
    `display`     text         NOT NULL,
    PRIMARY KEY (`name`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_restricted_items`
(
    `id`                  bigint(20)   NOT NULL,
    `access_control_type` varchar(255) NOT NULL,
    `reason`              text DEFAULT NULL,
    `server`              varchar(255) NOT NULL,
    `identifier`          varchar(255) NOT NULL,
    `persistentData`      text DEFAULT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_restricted_mods`
(
    `id`                  bigint(20)   NOT NULL,
    `access_control_type` varchar(255) NOT NULL,
    `reason`              text DEFAULT NULL,
    `server`              varchar(255) NOT NULL,
    `identifier`          varchar(255) NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_restriction_actions`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `action`      varchar(255) NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_restriction_alternatives`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier`     varchar(255) NOT NULL,
    `persistentData` text       DEFAULT NULL,
    `original_id`    bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_restriction_worlds`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier`  varchar(255) NOT NULL,
    `original_id` bigint(20) DEFAULT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_staff_prefix`
(
    `name`          varchar(255) NOT NULL,
    `full_display`  text         NOT NULL,
    `full_name`     text         NOT NULL,
    `short_display` text         NOT NULL,
    PRIMARY KEY (`name`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_unbans`
(
    `incident_id`          varchar(8)   NOT NULL,
    `author`               varchar(36)  NOT NULL,
    `reason`               text         NOT NULL,
    `server`               varchar(255) NOT NULL,
    `timestamp`            datetime     NOT NULL,
    `original_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_unbans_incident_id` (`incident_id`),
    KEY `fk_unbans_original_incident_id` (`original_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_unlocked_chat_markers`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `chat_marker_name` varchar(255) NOT NULL,
    `unique_id`        varchar(36)  NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_unlocked_prefixes`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT,
    `prefix_name` varchar(255) NOT NULL,
    `unique_id`   varchar(36)  NOT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_unmutes`
(
    `incident_id`          varchar(8)   NOT NULL,
    `author`               varchar(36)  NOT NULL,
    `reason`               text         NOT NULL,
    `server`               varchar(255) NOT NULL,
    `timestamp`            datetime     NOT NULL,
    `original_incident_id` varchar(8) DEFAULT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_unmutes_incident_id` (`incident_id`),
    KEY `fk_unmutes_original_incident_id` (`original_incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_user_ip_history`
(
    `id`         bigint(20)  NOT NULL AUTO_INCREMENT,
    `first_seen` datetime     DEFAULT NULL,
    `ip_address` varchar(255) DEFAULT NULL,
    `last_seen`  datetime     DEFAULT NULL,
    `target`     varchar(36) NOT NULL,
    `times_seen` bigint(20)   DEFAULT NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_user_settings`
(
    `unique_id`         varchar(36)  NOT NULL,
    `read_global`       bit(1)       NOT NULL,
    `read_local`        bit(1)       NOT NULL,
    `read_staff_global` bit(1)       NOT NULL,
    `read_staff_local`  bit(1)       NOT NULL,
    `social_spy`        bit(1)       NOT NULL,
    `write_channel`     varchar(255) NOT NULL,
    PRIMARY KEY (`unique_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_users`
(
    `unique_id` varchar(36) NOT NULL,
    `last_seen` datetime DEFAULT NULL,
    `username`  varchar(36) NOT NULL,
    PRIMARY KEY (`unique_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_warns`
(
    `incident_id` varchar(8)   NOT NULL,
    `author`      varchar(36)  NOT NULL,
    `reason`      text         NOT NULL,
    `server`      varchar(255) NOT NULL,
    `target`      varchar(36)  NOT NULL,
    `timestamp`   datetime     NOT NULL,
    PRIMARY KEY (`incident_id`),
    UNIQUE KEY `uk_warns_incident_id` (`incident_id`)
) DEFAULT CHARSET = utf8mb4;

CREATE TABLE `dirtcore_worth_items`
(
    `id`              bigint(20)   NOT NULL AUTO_INCREMENT,
    `identifier`      varchar(255) NOT NULL,
    `persistent_data` text DEFAULT NULL,
    `server`          varchar(255) NOT NULL,
    `worth`           double       NULL,
    PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4;

ALTER TABLE `dirtcore_ban_history`
    ADD
        CONSTRAINT `fk_ban_history_original_incident_id` FOREIGN KEY (`original_incident_id`) REFERENCES `dirtcore_bans` (`incident_id`);

ALTER TABLE `dirtcore_bans`
    ADD
        CONSTRAINT `fk_bans_unban_incident_id` FOREIGN KEY (`unban_incident_id`) REFERENCES `dirtcore_unbans` (`incident_id`);

ALTER TABLE `dirtcore_crate_content_commands`
    ADD
        CONSTRAINT `fk_crate_content_commands_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_crate_contents` (`id`);

ALTER TABLE `dirtcore_crate_content_items`
    ADD
        CONSTRAINT `fk_crate_content_items_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_crate_contents` (`id`);

ALTER TABLE `dirtcore_crate_contents`
    ADD
        CONSTRAINT `fk_crate_contents_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_crates` (`id`);

ALTER TABLE `dirtcore_crate_keys`
    ADD
        CONSTRAINT `fk_crate_keys_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_crates` (`id`);

ALTER TABLE `dirtcore_crate_locations`
    ADD
        CONSTRAINT `fk_crate_locations_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_crates` (`id`);

ALTER TABLE `dirtcore_crates`
    ADD
        CONSTRAINT `fk_crates_key_id` FOREIGN KEY (`key_id`) REFERENCES `dirtcore_crate_keys` (`id`);

ALTER TABLE `dirtcore_kit_claim_entries`
    ADD
        CONSTRAINT `fk_kit_claim_entries_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_kits` (`id`);

ALTER TABLE `dirtcore_kit_items`
    ADD
        CONSTRAINT `fk_kit_items_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_kits` (`id`);

ALTER TABLE `dirtcore_limited_block_entries`
    ADD
        CONSTRAINT `fk_limited_block_entries_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_limited_blocks` (`id`);

ALTER TABLE `dirtcore_limited_block_rules`
    ADD
        CONSTRAINT `fk_limited_block_rules_original_id` FOREIGN KEY (`original_id`) REFERENCES `dirtcore_limited_blocks` (`id`);

ALTER TABLE `dirtcore_mute_history`
    ADD
        CONSTRAINT `fk_mute_history_original_incident_id` FOREIGN KEY (`original_incident_id`) REFERENCES `dirtcore_mutes` (`incident_id`);

ALTER TABLE `dirtcore_mutes`
    ADD
        CONSTRAINT `fk_mutes_unmute_incident_id` FOREIGN KEY (`unmute_incident_id`) REFERENCES `dirtcore_unmutes` (`incident_id`);

ALTER TABLE `dirtcore_unbans`
    ADD
        CONSTRAINT `fk_unbans_original_incident_id` FOREIGN KEY (`original_incident_id`) REFERENCES `dirtcore_bans` (`incident_id`);

ALTER TABLE `dirtcore_unmutes`
    ADD
        CONSTRAINT `fk_unmutes_original_incident_id` FOREIGN KEY (`original_incident_id`) REFERENCES `dirtcore_mutes` (`incident_id`);
