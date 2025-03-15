/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.builder.node;

import java.util.Map;
import net.dirtcraft.dirtcore.common.discord.command.CommandErrorException;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandResult;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.FunctionHandler;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SubcommandGroupNode extends AbstractCommandNode<SubcommandGroupData> implements FunctionHandler {

    @NonNull
    private final Map<String, SubcommandNode> subcommands;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public SubcommandGroupNode(@NonNull final String name, @NonNull final String description,
            @NonNull final Permission requiredPermission,
            @NonNull final Map<String, SubcommandNode> subcommands,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, description, requiredPermission);
        this.subcommands = subcommands;
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @Override
    @NonNull
    public SubcommandGroupData create() {
        return new SubcommandGroupData(this.name, this.description);
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext interactionContext) {
        final SlashCommandInteractionEvent event = interactionContext.getEvent();

        if (event.getSubcommandName() != null) {
            final SubcommandNode node = this.subcommands.get(event.getSubcommandName());

            if (node == null) {
                throw new CommandErrorException("Could not find subcommand.");
            }

            node.interact(interactionContext);
        }

        return new CommandResult(this.getFunction().apply(interactionContext), this.executeAfter);
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
}
