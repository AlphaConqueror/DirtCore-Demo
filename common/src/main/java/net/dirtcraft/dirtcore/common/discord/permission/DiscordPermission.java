/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.permission;

import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public enum DiscordPermission implements Permission {

    ALL("all"),
    CHAT_MARKER("chatmarker"),
    LIST("list"),
    NONE(null),
    PREFIX("prefix"),
    RELOAD("reload"),
    RESTART("restart"),
    SHUTDOWN("shutdown"),
    STAFF_PREFIX("staffprefix"),
    SYNC("sync"),
    UNSYNC("unsync");

    @Nullable
    private final String permission;

    DiscordPermission(@Nullable final String permission) {
        this.permission = permission;
    }

    @NonNull
    public static DiscordPermission fromString(@NonNull final String permission) {
        return Arrays.stream(DiscordPermission.values())
                .filter(p -> p.permission != null && p.permission.equals(permission)).findAny()
                .orElse(NONE);
    }

    @Override
    @Nullable
    public String getPermission() {
        return this.permission;
    }
}
