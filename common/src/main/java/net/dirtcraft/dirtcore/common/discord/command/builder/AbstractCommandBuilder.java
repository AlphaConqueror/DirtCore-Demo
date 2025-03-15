/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.builder;

import net.dirtcraft.dirtcore.common.discord.command.builder.node.AbstractCommandNode;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.CheckReturnValue;

public abstract class AbstractCommandBuilder<T extends AbstractCommandNode<?>,
        B extends AbstractCommandBuilder<T, B>> {

    @NonNull
    protected final String name;
    @NonNull
    protected final String description;
    @NonNull
    protected Permission permission = DiscordPermission.NONE;

    protected AbstractCommandBuilder(@NonNull final String name,
            @NonNull final String description) {
        this.name = name;
        this.description = description;
    }

    @NonNull
    public abstract T build();

    protected abstract B getThis();

    @NonNull
    @CheckReturnValue
    public B requires(@NonNull final Permission permission) {
        this.permission = permission;
        return this.getThis();
    }
}
