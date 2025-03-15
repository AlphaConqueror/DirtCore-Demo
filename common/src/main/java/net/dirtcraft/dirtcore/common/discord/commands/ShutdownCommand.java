/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.commands;

import java.awt.Color;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class ShutdownCommand extends AbstractCommand {

    public ShutdownCommand(final @NonNull DiscordBotClient client) {
        super(client, "shutdown", "Shuts down the bot.");
    }

    @Override
    protected @NonNull RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.SHUTDOWN)
                .executes(interactionContext -> interactionContext.getEvent().getHook()
                        .sendMessageEmbeds(new EmbedBuilder().setDescription("Shutting down...")
                                .setColor(Color.ORANGE).build()).setEphemeral(true))
                .executesAfter(this.client::shutdown);
    }
}
