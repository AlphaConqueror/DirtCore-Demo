/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.builder.node;

import java.util.Locale;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandResult;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.FunctionHandler;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ChoiceNode extends AbstractCommandNode<Command.Choice> implements FunctionHandler {

    @NonNull
    private final String value;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;

    public ChoiceNode(@NonNull final String name, @NonNull final Permission permission,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, "A choice node.", permission);
        this.value = name.toLowerCase(Locale.ROOT);
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public Command.Choice create() {
        return new Command.Choice(this.name, this.value);
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext interactionContext) {
        return new CommandResult(this.getFunction().apply(interactionContext), this.executeAfter);
    }

    @NotNull
    public String getValue() {
        return this.value;
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
