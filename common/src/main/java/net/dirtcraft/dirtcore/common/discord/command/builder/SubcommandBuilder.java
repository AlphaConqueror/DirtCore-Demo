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
import net.dirtcraft.dirtcore.common.discord.command.builder.node.SubcommandNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

public class SubcommandBuilder extends AbstractCommandBuilder<SubcommandNode, SubcommandBuilder> {

    private final Map<String, OptionNode> options = new LinkedHashMap<>();
    @Nullable
    private CommandFunction function;
    @Nullable
    private Runnable executeAfter;

    protected SubcommandBuilder(@NonNull final String name, @NonNull final String description) {
        super(name, description);
    }

    @Override
    public @NotNull SubcommandNode build() {
        return new SubcommandNode(this.name, this.description, this.permission, this.options,
                this.function, this.executeAfter);
    }

    @Override
    protected SubcommandBuilder getThis() {
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder then(final OptionBuilder child) {
        final OptionNode node = child.build();

        if (this.options.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has child with name '" + node.getName() + "'.");
        }

        this.options.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder executes(@NonNull final CommandFunction function) {
        this.function = function;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandBuilder executesAfter(@NonNull final Runnable run) {
        this.executeAfter = run;
        return this;
    }
}
