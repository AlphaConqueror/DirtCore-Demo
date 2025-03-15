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

package net.dirtcraft.dirtcore.common.discord;

import java.time.Duration;
import java.time.Instant;
import net.dirtcraft.dirtcore.common.discord.config.ConfigFactory;
import net.dirtcraft.dirtcore.common.discord.config.ConfigFactoryImpl;
import net.dirtcraft.dirtcore.common.discord.permission.PermissionManager;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dv8tion.jda.api.JDA;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DiscordBotClient {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final Logger logger;
    @NonNull
    private final ConfigFactory config;
    @NonNull
    private final PermissionManager permissionManager;
    @NonNull
    private final DiscordManager discordManager;

    public DiscordBotClient(@NonNull final DirtCorePlugin plugin,
            @NonNull final String token) throws InterruptedException {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.config = new ConfigFactoryImpl(this.plugin.getConfiguration(), token);
        this.permissionManager = new PermissionManager(this);
        this.discordManager = new DiscordManager(this.plugin, this);
    }

    public void enable() {
        final Instant startupTime = Instant.now();
        this.logger.info("Starting discord bot...");

        final Duration timeTaken = Duration.between(startupTime, Instant.now());
        this.logger.info("Discord bot successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public void disable() {
        this.logger.info("Starting Discord bot shutdown process...");
        this.logger.info("Discord bot says goodbye!");
    }

    public void restart() {
        this.logger.info("Restarting...");
        this.disable();
        this.enable();
    }

    public void shutdown() {
        this.disable();

        if (this.discordManager.isJDAReady()) {
            this.logger.info("Shutting down JDA...");

            final JDA jda = this.discordManager.getJda();

            jda.shutdown();
            this.logger.info("Waiting for JDA to shutdown...");

            try {
                // Allow at most 10 seconds for remaining requests to finish
                if (!jda.awaitShutdown(Duration.ofSeconds(10))) {
                    this.logger.info("Forcing shutdown...");
                    jda.shutdownNow(); // Cancel all remaining requests
                }
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }

            this.logger.info("JDA says goodbye!");
        }
    }

    @NonNull
    public DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @NonNull
    public Logger getLogger() {
        return this.logger;
    }

    @NonNull
    public ConfigFactory getConfig() {
        return this.config;
    }

    @NonNull
    public PermissionManager getPermissionManager() {
        return this.permissionManager;
    }

    @NonNull
    public DiscordManager getDiscordManager() {
        return this.discordManager;
    }
}
