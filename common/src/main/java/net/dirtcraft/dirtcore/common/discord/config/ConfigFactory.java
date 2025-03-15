/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.requests.GatewayIntent;

public interface ConfigFactory {

    Map<Long, Set<Permission>> getPermissions();

    String getToken();

    Collection<GatewayIntent> getIntents();

    String getStatus();

    long getGuildId();

    void reload();
}
