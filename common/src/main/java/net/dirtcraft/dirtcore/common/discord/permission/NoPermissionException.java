/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.permission;

public class NoPermissionException extends RuntimeException {

    private final Permission permission;

    public NoPermissionException(final Permission permission) {this.permission = permission;}

    public Permission getPermission() {
        return this.permission;
    }
}
