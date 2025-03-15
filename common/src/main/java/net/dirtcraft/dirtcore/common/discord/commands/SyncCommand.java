/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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

public class SyncCommand extends AbstractCommand {

    public SyncCommand(final @NonNull DiscordBotClient client) {
        super(client, "sync", "Synchronizes the slash commands.", true);
    }

    @NonNull
    @Override
    protected RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.SYNC)
                .then(Commands.option("type", "The type of synchronization.", OptionType.STRING)
                        .required().addChoice("ALL", this::syncAll)
                        .addChoice("GLOBAL", this::syncGlobal).addChoice("GUILD", this::syncGuild)
                        .addChoice("FIX_GUILD", this::fixGuild));
    }

    @NonNull
    private WebhookMessageCreateAction<Message> syncAll(
            @NonNull final InteractionContext interactionContext) {
        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                        this.client.getDiscordManager().syncAllCommands() ?
                                DiscordEmbeds.SUCCESS.build(
                                "All commands have been synchronized.") :
                                DiscordEmbeds.WARNING.build(
                                "Could not find guild. Only global commands have been " +
                                        "synchronized."))
                .setEphemeral(true);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> syncGlobal(
            @NonNull final InteractionContext interactionContext) {
        this.client.getDiscordManager().syncGlobalCommands();

        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                        DiscordEmbeds.SUCCESS.build("Global commands have been synchronized."))
                .setEphemeral(true);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> syncGuild(
            @NonNull final InteractionContext interactionContext) {
        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                this.client.getDiscordManager().syncGuildCommands() ? DiscordEmbeds.SUCCESS.build(
                        "Guild commands have been synchronized.")
                        : DiscordEmbeds.FAILURE.build("Could not find guild.")).setEphemeral(true);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> fixGuild(
            @NonNull final InteractionContext interactionContext) {
        return interactionContext.getEvent().getHook().sendMessageEmbeds(
                this.client.getDiscordManager().fixGuildCommands() ? DiscordEmbeds.SUCCESS.build(
                        "Global commands for guilds have been fixed.")
                        : DiscordEmbeds.FAILURE.build("Could not find guild.")).setEphemeral(true);
    }
}
