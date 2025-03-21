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

package net.dirtcraft.dirtcore.common.discord.command.abstraction;

import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.CommandErrorException;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.builder.Commands;
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.RootCommandNode;
import net.dirtcraft.dirtcore.common.discord.permission.NoPermissionException;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand extends ListenerAdapter {

    @NonNull
    protected final DiscordBotClient client;
    @NonNull
    protected final String name;
    @NonNull
    protected final String description;
    @NonNull
    protected final RootCommandNode rootCommandNode;
    protected final boolean keep;

    public AbstractCommand(final @NonNull DiscordBotClient client, @NonNull final String name,
            @NonNull final String description) {
        this(client, name, description, false);
    }

    public AbstractCommand(final @NonNull DiscordBotClient client, @NonNull final String name,
            @NonNull final String description, final boolean keep) {
        this.client = client;
        this.name = name;
        this.description = description;
        this.rootCommandNode = this.build(Commands.slash(this.name, this.description)).build();
        this.keep = keep;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull final SlashCommandInteractionEvent event) {
        this.getPlugin().getBootstrap().getScheduler().executeAsync(() -> {
            if (event.getName().equals(this.name)) {
                this.getPlugin().getLogger()
                        .info("{} issued Discord command: {}", event.getUser().getName(),
                                event.getCommandString());

                // acknowledge interaction
                event.deferReply(true).submit();

                try {
                    final CommandResult result = this.rootCommandNode.interact(
                            new InteractionContext(this.client, event));

                    result.getMessage().queue();
                    result.executeAfter();
                } catch (final CommandErrorException e) {
                    event.getHook().sendMessageEmbeds(DiscordEmbeds.AN_ERROR_OCCURRED.build())
                            .setEphemeral(true).queue();
                    this.client.getLogger()
                            .severe("Caught an exception during command execution. ", e);
                } catch (final NoPermissionException e) {
                    event.getHook()
                            .sendMessageEmbeds(DiscordEmbeds.NO_PERMISSION.build(e.getPermission()))
                            .setEphemeral(true).queue();
                } catch (final PermissionException e) {
                    event.getHook().sendMessageEmbeds(
                                    DiscordEmbeds.BOT_NO_PERMISSION.build(e.getPermission()))
                            .setEphemeral(true).queue();
                }
            }
        });
    }

    @NonNull
    public DiscordBotClient getClient() {
        return this.client;
    }

    @NonNull
    public DirtCorePlugin getPlugin() {
        return this.client.getPlugin();
    }

    @NonNull
    public CommandData createData() {
        return this.rootCommandNode.create();
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public boolean keep() {
        return this.keep;
    }

    @NonNull
    protected RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data;
    }
}
