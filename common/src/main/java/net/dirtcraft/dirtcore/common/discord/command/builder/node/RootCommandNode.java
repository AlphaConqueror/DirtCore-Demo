/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.builder.node;

import java.util.Map;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.discord.command.CommandErrorException;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandResult;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.OptionHandler;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class RootCommandNode extends AbstractCommandNode<SlashCommandData> implements OptionHandler {

    @NonNull
    private final DefaultMemberPermissions defaultPermissions;
    private final boolean guildOnly;
    @NonNull
    private final Map<String, SubcommandGroupNode> subcommandGroups;
    @NonNull
    private final Map<String, SubcommandNode> subcommands;
    @NonNull
    private final Map<String, net.dirtcraft.dirtcore.common.discord.command.builder.node.OptionNode>
            options;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public RootCommandNode(@NonNull final String name, @NonNull final String description,
            @NonNull final DefaultMemberPermissions defaultPermissions, final boolean guildOnly,
            @NonNull final Permission requiredPermission,
            @NonNull final Map<String, SubcommandGroupNode> subcommandGroups,
            @NonNull final Map<String, SubcommandNode> subcommands,
            @NonNull final Map<String, OptionNode> options,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, description, requiredPermission);
        this.defaultPermissions = defaultPermissions;
        this.guildOnly = guildOnly;
        this.subcommands = subcommands;
        this.subcommandGroups = subcommandGroups;
        this.options = options;
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public SlashCommandData create() {
        return Commands.slash(this.name, this.description)
                .setDefaultPermissions(this.defaultPermissions).setGuildOnly(this.guildOnly)
                .addSubcommandGroups(
                        this.subcommandGroups.values().stream().map(SubcommandGroupNode::create)
                                .collect(Collectors.toList())).addSubcommands(
                        this.subcommands.values().stream().map(SubcommandNode::create)
                                .collect(Collectors.toList())).addOptions(
                        this.options.values().stream().map(OptionNode::create)
                                .collect(Collectors.toList()));
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext interactionContext) {
        final SlashCommandInteractionEvent event = interactionContext.getEvent();

        if (event.getSubcommandGroup() != null) {
            final SubcommandGroupNode node = this.subcommandGroups.get(event.getSubcommandGroup());

            if (node == null) {
                throw new CommandErrorException("Could not find subcommand group.");
            }

            return node.interact(interactionContext);
        } else if (event.getSubcommandName() != null) {
            final SubcommandNode node = this.subcommands.get(event.getSubcommandName());

            if (node == null) {
                throw new CommandErrorException("Could not find subcommand.");
            }

            return node.interact(interactionContext);
        }

        return this.interactOptions(interactionContext);
    }

    @Override
    public boolean hasFunction() {
        return this.function != null;
    }

    @Override
    public @NonNull CommandFunction getFunction() {
        return this.function == null ? this.getAlternativeFunction() : this.function;
    }

    @Override
    public @Nullable Runnable getExecuteAfter() {
        return this.executeAfter;
    }

    @Override
    public @NonNull Map<String, OptionNode> getOptions() {
        return this.options;
    }
}
