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

package net.dirtcraft.dirtcore.common.discord.event;

import java.lang.management.ManagementFactory;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.event.EventBus;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.discord.DiscordManager;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.event.DirtCoreEventListener;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerAchievementEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerDeathEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLoginEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLogoutEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerStartedEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerStoppingEvent;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.player.PlayerDataEntity;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DiscordPlatformListener implements DirtCoreEventListener {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final DiscordManager discordManager;

    public DiscordPlatformListener(@NonNull final DirtCorePlugin plugin,
            @NonNull final DiscordManager discordManager) {
        this.plugin = plugin;
        this.discordManager = discordManager;
    }

    public void onPlayerAchievement(final PlayerAchievementEvent event) {
        this.discordManager.getGameChannel().ifPresent(
                channel -> channel.sendMessageEmbeds(DiscordEmbeds.PLAYER_ACHIEVEMENT.build(event))
                        .queue());
    }

    public void onPlayerDeath(final PlayerDeathEvent event) {
        this.discordManager.getGameChannel().ifPresent(channel -> channel.sendMessage(
                this.plugin.getConfiguration().get(ConfigKeys.DISCORD_EMOJIS_DEATH) + ' '
                        + MarkdownSanitizer.escape(event.getDeathMessage())).queue());
    }

    public void onPlayerLogin(final PlayerLoginEvent event) {
        this.discordManager.getGameChannel().ifPresent(channel -> {
            final UUID uniqueId = event.getUniqueId();

            this.plugin.getStorage().performTask(context -> {
                final PlayerDataEntity playerData =
                        this.plugin.getUserManager().getOrCreatePlayerData(context, uniqueId);
                final User user = this.plugin.getUserManager().getOrCreateUser(context, uniqueId);
                final String formattedUserDisplay = this.formatUserDisplay(context, user);

                if (playerData.getLastSeen().isPresent()) {
                    final StringBuilder builder = new StringBuilder(this.plugin.getConfiguration()
                            .get(ConfigKeys.DISCORD_EMOJIS_JOIN)).append(' ')
                            .append(MarkdownUtil.bold(
                                    MarkdownSanitizer.escape(formattedUserDisplay)))
                            .append(' ')
                            .append(MarkdownSanitizer.escape(this.plugin.getMessagingManager()
                                    .getJoinMessageUnformattedOrDefault(context, user)));
                    context.queue(() -> channel.sendMessage(builder.toString()).queue());
                } else {
                    context.queue(() -> channel.sendMessageEmbeds(
                            DiscordEmbeds.WELCOME_MESSAGE_DEFAULT.build(this.plugin,
                                    formattedUserDisplay)).queue());
                }
            });
        });
    }

    public void onPlayerLogout(final PlayerLogoutEvent event) {
        this.discordManager.getGameChannel().ifPresent(channel -> {
            final UUID uniqueId = event.getUniqueId();

            this.plugin.getStorage().performTaskAsync(context -> {
                final User user = this.plugin.getUserManager().getOrCreateUser(context, uniqueId);
                final StringBuilder builder = new StringBuilder(
                        this.plugin.getConfiguration().get(ConfigKeys.DISCORD_EMOJIS_LEAVE)).append(
                                ' ')
                        .append(MarkdownUtil.bold(
                                MarkdownSanitizer.escape(this.formatUserDisplay(context, user))))
                        .append(' ')
                                .append(MarkdownSanitizer.escape(this.plugin.getMessagingManager()
                                .getLeaveMessageUnformattedOrDefault(context, user)));
                context.queue(() -> channel.sendMessage(builder.toString()).queue());
            });
        });
    }

    public void onServerStarted(final ServerStartedEvent ignored) {
        this.discordManager.getGameChannel().ifPresent(channel -> {
            final long uptime =
                    (long) Math.floor(ManagementFactory.getRuntimeMXBean().getUptime() / 1000d);
            channel.sendMessageEmbeds(DiscordEmbeds.SERVER_STARTED.build(this.plugin,
                    FormatUtils.formatDateDiff(uptime, true, false))).queue();
        });
    }

    public void onServerStopping(final ServerStoppingEvent ignored) {
        this.discordManager.getGameChannel().ifPresent(channel -> channel.sendMessageEmbeds(
                DiscordEmbeds.SERVER_STOPPING.build(this.plugin)).queue());
        this.discordManager.getClient().shutdown();
    }

    @Override
    public void bind(final EventBus bus) {
        bus.subscribe(PlayerAchievementEvent.class, this::onPlayerAchievement);
        bus.subscribe(PlayerDeathEvent.class, this::onPlayerDeath);
        bus.subscribe(PlayerLoginEvent.class, this::onPlayerLogin);
        bus.subscribe(PlayerLogoutEvent.class, this::onPlayerLogout);
        bus.subscribe(ServerStartedEvent.class, this::onServerStarted);
        bus.subscribe(ServerStoppingEvent.class, this::onServerStopping);
    }

    @NonNull
    private String formatUserDisplay(@NonNull final TaskContext context, @NonNull final User user) {
        final PlayerDataEntity playerData =
                this.plugin.getUserManager().getOrCreatePlayerData(context, user.getUniqueId());
        final Optional<PrefixEntity> prefixOptional = playerData.getPrefix(context);
        final Optional<StaffPrefixEntity> staffPrefixOptional =
                this.plugin.getChatManager().getStaffPrefix(context, user);
        final StringBuilder builder = new StringBuilder();
        boolean hasPrefix = prefixOptional.isPresent();

        // if user has active prefix, use shortened staff prefix
        if (staffPrefixOptional.isPresent()) {
            final StaffPrefixEntity staffPrefix = staffPrefixOptional.get();

            builder.append(prefixOptional.isPresent() ? staffPrefix.getShortDisplayUnformatted()
                    : staffPrefix.getFullDisplayUnformatted());
            hasPrefix = true;
        }

        prefixOptional.ifPresent(prefix -> builder.append(prefix.getDisplayUnformatted()));

        if (hasPrefix) {
            builder.append(' ');
        }

        return builder.append(user.getName()).toString();
    }
}
