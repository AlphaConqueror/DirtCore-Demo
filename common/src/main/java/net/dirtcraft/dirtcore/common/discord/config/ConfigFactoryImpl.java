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
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.config.DirtCoreConfiguration;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfigFactoryImpl implements ConfigFactory {

    @NonNull
    private final DirtCoreConfiguration config;
    @NonNull
    private final String token;

    public ConfigFactoryImpl(@NonNull final DirtCoreConfiguration config, @NonNull String token) {
        this.config = config;
        this.token = token;
    }

    @Override
    public Map<Long, Set<Permission>> getPermissions() {
        return this.config.get(ConfigKeys.DISCORD_PERMISSIONS);
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Collection<GatewayIntent> getIntents() {
        return this.config.get(ConfigKeys.DISCORD_INTENTS);
    }

    @Override
    public String getStatus() {
        return this.config.get(ConfigKeys.DISCORD_STATUS);
    }

    @Override
    public long getGuildId() {
        return this.config.get(ConfigKeys.DISCORD_GUILD_ID);
    }

    @Override
    public void reload() {
        this.config.reload();
    }
}
