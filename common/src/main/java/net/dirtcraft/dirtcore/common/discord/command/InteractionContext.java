/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command;

import java.util.Optional;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class InteractionContext {

    @NonNull
    private final DiscordBotClient client;
    @NonNull
    private final SlashCommandInteractionEvent event;

    public InteractionContext(@NotNull final DiscordBotClient client,
            final @NotNull SlashCommandInteractionEvent event) {
        this.client = client;
        this.event = event;
    }

    public @NonNull DiscordBotClient getClient() {
        return this.client;
    }

    @NonNull
    public SlashCommandInteractionEvent getEvent() {
        return this.event;
    }

    @NonNull
    public Optional<OptionMapping> getOption(@NonNull final String name) {
        return Optional.ofNullable(this.event.getOption(name));
    }

    @NonNull
    public OptionMapping getOptionOrException(@NonNull final String name) {
        final OptionMapping option = this.event.getOption(name);

        if (option == null) {
            throw new CommandErrorException("Could not find option mapping.");
        }

        return option;
    }
}
