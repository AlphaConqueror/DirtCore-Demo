/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.commands;

import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class RestartCommand extends AbstractCommand {

    public RestartCommand(final @NonNull DiscordBotClient client) {
        super(client, "restart", "Restarts the bot.");
    }

    @Override
    protected @NonNull RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.RESTART)
                .executes(interactionContext -> {
                    this.client.restart();
                    return interactionContext.getEvent().getHook().sendMessageEmbeds(
                                    DiscordEmbeds.SUCCESS.build("Bot has been restarted."))
                            .setEphemeral(true);
                });
    }
}
