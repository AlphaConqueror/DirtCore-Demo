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
