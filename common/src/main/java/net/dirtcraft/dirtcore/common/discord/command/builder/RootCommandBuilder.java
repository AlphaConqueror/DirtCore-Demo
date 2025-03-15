/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.OptionNode;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.RootCommandNode;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.SubcommandGroupNode;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.SubcommandNode;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;

public class RootCommandBuilder extends AbstractCommandBuilder<RootCommandNode,
        RootCommandBuilder> {

    @NonNull
    private final Map<String, SubcommandGroupNode> subGroupCommands = new LinkedHashMap<>();
    @NonNull
    private final Map<String, SubcommandNode> subCommands = new LinkedHashMap<>();
    @NonNull
    private final Map<String, OptionNode> options = new LinkedHashMap<>();
    @NonNull
    private DefaultMemberPermissions defaultPermissions = DefaultMemberPermissions.ENABLED;
    private boolean guildOnly;
    @Nullable
    private CommandFunction function;
    @Nullable
    private Runnable executeAfter;

    protected RootCommandBuilder(@NonNull final String name, @NonNull final String description) {
        super(name, description);
    }

    @Override
    @NonNull
    public RootCommandNode build() {
        return new RootCommandNode(this.name, this.description, this.defaultPermissions,
                this.guildOnly, this.permission, this.subGroupCommands, this.subCommands,
                this.options, this.function, this.executeAfter);
    }

    @Override
    protected RootCommandBuilder getThis() {
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder then(final SubcommandGroupBuilder subcommandGroup) {
        final SubcommandGroupNode node = subcommandGroup.build();

        if (this.subGroupCommands.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has sub command group with name '" + node.getName() + "'.");
        }

        this.subGroupCommands.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder then(final SubcommandBuilder subcommand) {
        final SubcommandNode node = subcommand.build();

        if (this.subCommands.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has sub command with name '" + node.getName() + "'.");
        }

        this.subCommands.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder then(final OptionBuilder option) {
        final OptionNode node = option.build();

        if (this.options.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has option with name '" + node.getName() + "'.");
        }

        this.options.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder showFor(@NonNull final DefaultMemberPermissions permissions) {
        this.defaultPermissions = permissions;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder guildOnly() {
        this.guildOnly = true;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder executes(@NonNull final CommandFunction function) {
        this.function = function;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public RootCommandBuilder executesAfter(@NonNull final Runnable run) {
        this.executeAfter = run;
        return this;
    }
}
