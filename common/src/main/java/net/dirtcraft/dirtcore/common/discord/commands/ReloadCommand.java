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
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends AbstractCommand {

    public ReloadCommand(final @NonNull DiscordBotClient client) {
        super(client, "reload", "Reloads the bot.");
    }

    @NonNull
    @Override
    protected RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.RELOAD)
                .executes(this::reload);
    }

    @NonNull
    private WebhookMessageCreateAction<Message> reload(
            @NonNull final InteractionContext interactionContext) {
        try {
            this.client.getConfig().reload();
            return interactionContext.getEvent().getHook()
                    .sendMessageEmbeds(DiscordEmbeds.SUCCESS.build("Reload complete."))
                    .setEphemeral(true);
        } catch (final RuntimeException e) {
            final String message = e.toString();
            final String description =
                    "Reload failed:\n\n```" + message.substring(0, Math.min(message.length(), 4074))
                            + "```";

            return interactionContext.getEvent().getHook()
                    .sendMessageEmbeds(new EmbedBuilder().setDescription(description).build())
                    .setEphemeral(true);
        }
    }
}
