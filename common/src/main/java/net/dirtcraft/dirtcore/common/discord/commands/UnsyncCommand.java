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

package net.dirtcraft.dirtcore.common.discord.commands;

import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.discord.command.builder.Commands;
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class UnsyncCommand extends AbstractCommand {

    public UnsyncCommand(final @NonNull DiscordBotClient client) {
        super(client, "unsync", "Unsynchronizes the slash commands.");
    }

    @NonNull
    @Override
    protected RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.UNSYNC)
                .then(Commands.option("type", "The type of synchronization.", OptionType.STRING)
                        .required().addChoice("ALL", this::unsyncAll)
                        .addChoice("GLOBAL", this::unsyncGlobal)
                        .addChoice("GUILD", this::unsyncGuild));
    }

    @NonNull
    private WebhookMessageCreateAction<Message> unsyncAll(
            @NonNull final InteractionContext interactionContext) {
        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                this.client.getDiscordManager().unsyncAllCommands() ? DiscordEmbeds.SUCCESS.build(
                        "All commands have been unsynchronized.") : DiscordEmbeds.WARNING.build(
                        "Could not find guild. Only global commands have been "
                                + "unsynchronized.")).setEphemeral(true);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> unsyncGlobal(
            @NonNull final InteractionContext interactionContext) {
        this.client.getDiscordManager().unsyncGlobalCommands();

        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                        DiscordEmbeds.SUCCESS.build("Global commands have been unsynchronized."))
                .setEphemeral(true);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> unsyncGuild(
            @NonNull final InteractionContext interactionContext) {
        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                this.client.getDiscordManager().unsyncGuildCommands() ? DiscordEmbeds.SUCCESS.build(
                        "Guild commands have been unsynchronized.")
                        : DiscordEmbeds.FAILURE.build("Could not find guild.")).setEphemeral(true);
    }
}
