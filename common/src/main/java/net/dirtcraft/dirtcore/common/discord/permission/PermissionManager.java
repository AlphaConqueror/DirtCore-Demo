/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.permission;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PermissionManager {

    private final DiscordBotClient client;

    public PermissionManager(final DiscordBotClient client) {this.client = client;}

    public boolean hasPermission(@NonNull final User user, @NonNull final Permission permission) {
        return this.client.getDiscordManager().getGuild().map(guild -> {
            try {
                final Member member = guild.retrieveMember(user).complete();
                return this.hasPermission(guild, member, permission);
            } catch (final ErrorResponseException ignored) {
                // user not found or not member, no perms
                return false;
            }
        }).orElse(false);
    }

    public boolean hasPermission(@NonNull final Guild guild, @Nullable final Member member,
            @NonNull final Permission permission) {
        // no permission needed
        if (permission == DiscordPermission.NONE) {
            return true;
        }

        // member must be part of guild
        if (member == null) {
            return false;
        }

        final Map<Long, Set<Permission>> permissionMap = this.client.getConfig().getPermissions();
        // check for default permissions (permissions of @everyone)
        final Set<Permission> defaultPermissions = permissionMap.get(guild.getIdLong());

        if (this.permissionMatch(defaultPermissions, permission)) {
            return true;
        }

        // check for user specific permissions
        final Set<Permission> userPermissions = permissionMap.get(member.getIdLong());

        if (this.permissionMatch(userPermissions, permission)) {
            return true;
        }

        // iterate through each role and check for permission
        return member.getRoles().stream().anyMatch(
                role -> this.permissionMatch(permissionMap.get(role.getIdLong()), permission));
    }

    private boolean permissionMatch(@Nullable final Collection<Permission> permissions,
            @NonNull final Permission permission) {
        // obtainedPermissions might be null when there is no config entry for this role id
        if (permissions == null) {
            return false;
        }

        // check if role contains missing permission
        return permissions.stream()
                .anyMatch(p -> p == DiscordPermission.ALL || permission.equals(p));
    }
}
