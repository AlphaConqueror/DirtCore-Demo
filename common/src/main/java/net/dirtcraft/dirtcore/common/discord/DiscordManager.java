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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.discord.commands.ListCommand;
import net.dirtcraft.dirtcore.common.discord.commands.ReloadCommand;
import net.dirtcraft.dirtcore.common.discord.commands.RestartCommand;
import net.dirtcraft.dirtcore.common.discord.commands.ShutdownCommand;
import net.dirtcraft.dirtcore.common.discord.commands.SyncCommand;
import net.dirtcraft.dirtcore.common.discord.commands.UnsyncCommand;
import net.dirtcraft.dirtcore.common.discord.commands.chat.ChatMarkerCommand;
import net.dirtcraft.dirtcore.common.discord.commands.chat.PrefixCommand;
import net.dirtcraft.dirtcore.common.discord.commands.chat.StaffPrefixCommand;
import net.dirtcraft.dirtcore.common.discord.event.DiscordEventListener;
import net.dirtcraft.dirtcore.common.discord.event.DiscordPlatformListener;
import net.dirtcraft.dirtcore.common.discord.exception.JDANotReadyException;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class DiscordManager {

    @NonNull
    protected final DirtCorePlugin plugin;
    @NonNull
    protected final DiscordBotClient client;
    @Nullable
    protected final JDA jda;
    @NonNull
    protected final Set<Class<? extends AbstractCommand>> commandClasses;
    @NonNull
    protected final Map<AbstractCommand, CommandData> globalCommands;
    @NonNull
    protected final Map<AbstractCommand, CommandData> guildCommands;
    // commands that are exempt from unsync
    @NonNull
    protected final Map<AbstractCommand, CommandData> keep;
    private boolean starting = false;

    public DiscordManager(@NonNull final DirtCorePlugin plugin,
            @NonNull final DiscordBotClient client) throws InterruptedException {
        this.plugin = plugin;
        this.client = client;

        final JDABuilder builder = JDABuilder.createDefault(client.getConfig().getToken())
                .enableIntents(client.getConfig().getIntents());
        final String status = client.getConfig().getStatus();

        if (!FormatUtils.isBlank(status)) {
            builder.setActivity(Activity.customStatus(client.getConfig().getStatus()));
        }

        this.jda = builder.build().awaitReady();
        this.commandClasses = ImmutableSet.copyOf(this.constructCommandClasses());

        final Map<AbstractCommand, CommandData> commands = new HashMap<>();

        this.commandClasses.forEach(c -> {
            try {
                final AbstractCommand abstractCommand =
                        c.getConstructor(DiscordBotClient.class).newInstance(this.client);

                commands.put(abstractCommand, abstractCommand.createData());
            } catch (final InstantiationException | IllegalAccessException |
                           InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        final Map<AbstractCommand, CommandData> globalCommands = new HashMap<>();
        final Map<AbstractCommand, CommandData> guildCommands = new HashMap<>();
        final Map<AbstractCommand, CommandData> keep = new HashMap<>();

        // filter commands by global/guild
        commands.forEach((key, value) -> {
            if (value.isGuildOnly()) {
                guildCommands.put(key, value);
            } else {
                globalCommands.put(key, value);
            }

            if (key.keep()) {
                keep.put(key, value);
            }
        });

        this.globalCommands = ImmutableMap.copyOf(globalCommands);
        this.guildCommands = ImmutableMap.copyOf(guildCommands);
        this.keep = ImmutableMap.copyOf(keep);

        this.syncAllCommands();
        this.onStarting();
    }

    public void registerListeners() {
        if (this.jda == null) {
            return;
        }

        // register discord listeners
        this.jda.addEventListener(new DiscordEventListener(this.plugin));
        this.plugin.getApiProvider().getEventBus()
                .subscribe(new DiscordPlatformListener(this.plugin, this));
    }

    public boolean isJDAReady() {
        return this.jda != null;
    }

    @NonNull
    public JDA getJda() throws JDANotReadyException {
        if (this.jda == null) {
            throw new JDANotReadyException();
        }

        return this.jda;
    }

    public Optional<Guild> getGuild() {
        return Optional.ofNullable(
                this.getJda().getGuildById(this.client.getConfig().getGuildId()));
    }

    @NonNull
    public String getUserName(final long id) {
        return this.plugin.getDiscordBotClient().flatMap(discordBotClient -> {
            try {
                final User discordUser =
                        discordBotClient.getDiscordManager().getJda().retrieveUserById(id)
                                .complete();
                return Optional.of(discordUser.getName());
            } catch (final ErrorResponseException ignored) {
                // user not found
                return Optional.empty();
            }
        }).orElse(String.valueOf(id));
    }

    public boolean syncAllCommands() {
        this.syncGlobalCommands();
        return this.syncGuildCommands();
    }

    public boolean unsyncAllCommands() {
        this.unsyncGlobalCommands();
        return this.unsyncGuildCommands();
    }

    public void syncGlobalCommands() {
        this.getJda().updateCommands().addCommands(this.globalCommands.values()).queue();
        this.registerListeners(this.globalCommands.keySet());
        this.client.getLogger().info("Synchronized global commands.");
    }

    public void unsyncGlobalCommands() {
        this.getJda().updateCommands().addCommands(
                this.keep.values().stream().filter(command -> !command.isGuildOnly())
                        .collect(Collectors.toList())).queue();

        final Set<AbstractCommand> unregister = new HashSet<>(this.globalCommands.keySet());
        unregister.removeAll(this.keep.keySet());

        this.unregisterListeners(unregister);
        this.client.getLogger().info("Unsynchronized global commands.");
    }

    public boolean syncGuildCommands() {
        final Optional<Guild> guild = this.getGuild();

        if (!guild.isPresent()) {
            this.client.getLogger().info("Guild not found, could not synchronize guild commands.");
            return false;
        }

        guild.get().updateCommands().addCommands(this.guildCommands.values()).queue();
        this.registerListeners(this.guildCommands.keySet());
        this.client.getLogger().info("Synchronized guild commands.");

        return true;
    }

    public boolean unsyncGuildCommands() {
        final Optional<Guild> guild = this.getGuild();

        if (!guild.isPresent()) {
            this.client.getLogger()
                    .info("Guild not found, could not unsynchronize guild commands.");
            return false;
        }

        guild.get().updateCommands().addCommands(
                this.keep.values().stream().filter(CommandData::isGuildOnly)
                        .collect(Collectors.toList())).queue();

        final Set<AbstractCommand> unregister = new HashSet<>(this.guildCommands.keySet());
        unregister.removeAll(this.keep.keySet());

        this.unregisterListeners(unregister);
        this.client.getLogger().info("Unsynchronized guild commands.");

        return true;
    }

    public boolean fixGuildCommands() {
        final Optional<Guild> guild = this.getGuild();

        if (!guild.isPresent()) {
            this.client.getLogger().info("Guild not found, could not synchronize guild commands.");
            return false;
        }

        guild.get().updateCommands().addCommands(this.globalCommands.values()).queue();
        this.client.getLogger().info("Global commands for guilds have been fixed.");

        return true;
    }

    public void unregisterListeners(final Collection<AbstractCommand> abstractCommands) {
        this.getJda().removeEventListener(abstractCommands.toArray());
        this.client.getLogger().info("Unregistered listeners for commands: {}",
                abstractCommands.stream().map(AbstractCommand::getName)
                        .collect(Collectors.toList()));
    }

    public Optional<TextChannel> getGameChannel() {
        if (this.jda == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.jda.getTextChannelById(
                this.plugin.getConfiguration().get(ConfigKeys.DISCORD_GAME_CHANNEL_ID)));
    }

    public Optional<TextChannel> getAdminLogChannel() {
        if (this.jda == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.jda.getTextChannelById(
                this.plugin.getConfiguration().get(ConfigKeys.DISCORD_ADMIN_LOG_CHANNEL_ID)));
    }

    public Optional<TextChannel> getStaffLogChannel() {
        if (this.jda == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.jda.getTextChannelById(
                this.plugin.getConfiguration().get(ConfigKeys.DISCORD_STAFF_LOG_CHANNEL_ID)));
    }

    @NonNull
    public DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @NonNull
    public DiscordBotClient getClient() {
        return this.client;
    }

    protected void registerListeners(final Collection<AbstractCommand> abstractCommands) {
        final JDA jda = this.getJda();

        for (final AbstractCommand abstractCommand : abstractCommands) {
            if (!jda.getRegisteredListeners().contains(abstractCommand)) {
                jda.addEventListener(abstractCommand);
            }
        }

        this.client.getLogger().info("Registered listeners for commands: {}",
                abstractCommands.stream().map(AbstractCommand::getName)
                        .collect(Collectors.toList()));
    }

    @NonNull
    protected Set<Class<? extends AbstractCommand>> constructCommandClasses() {
        return new HashSet<>(
                Arrays.asList(ChatMarkerCommand.class, ListCommand.class, PrefixCommand.class,
                        ReloadCommand.class, RestartCommand.class, ShutdownCommand.class,
                        StaffPrefixCommand.class, SyncCommand.class, UnsyncCommand.class));
    }

    private void onStarting() {
        // sometimes this event may be fired twice
        if (this.starting) {
            return;
        }

        this.getGameChannel().ifPresent(channel -> channel.sendMessageEmbeds(
                DiscordEmbeds.SERVER_STARTING.build(this.plugin)).queue());
        this.starting = true;
    }
}
