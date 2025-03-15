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
import net.dirtcraft.dirtcore.common.discord.command.builder.node.ChoiceNode;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.OptionNode;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;

public class OptionBuilder extends AbstractCommandBuilder<OptionNode, OptionBuilder> {

    @NonNull
    private final OptionType type;
    private final Map<String, ChoiceNode> choices = new LinkedHashMap<>();
    private boolean isRequired;
    private boolean isAutoComplete;
    @Nullable
    private Number minValue;
    @Nullable
    private Number maxValue;
    @Nullable
    private CommandFunction function;
    @Nullable
    private Runnable executeAfter;

    protected OptionBuilder(@NonNull final String name, @NonNull final String description,
            @NonNull final OptionType type) {
        super(name, description);
        this.type = type;
    }


    @Override
    @NonNull
    public OptionNode build() {
        return new OptionNode(this.name, this.description, this.permission, this.type,
                this.isRequired, this.isAutoComplete, this.minValue, this.maxValue, this.choices,
                this.function, this.executeAfter);
    }

    @Override
    protected OptionBuilder getThis() {
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder required() {
        this.isRequired = true;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder autoCompletable() {
        this.isAutoComplete = true;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder addChoice(@NonNull final String name, @NonNull final Permission permission,
            @NonNull final CommandFunction function) {
        if (this.choices.containsKey(name)) {
            throw new IllegalArgumentException("Node already has choice with name '" + name + "'.");
        }

        final ChoiceNode choice = new ChoiceNode(name, permission, function, this.executeAfter);

        this.choices.put(choice.getValue(), choice);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder addChoice(@NonNull final String name,
            @NonNull final CommandFunction function) {
        return this.addChoice(name, DiscordPermission.NONE, function);
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder setMinValue(@NonNull final Number value) {
        if (this.type != OptionType.INTEGER && this.type != OptionType.NUMBER) {
            throw new IllegalArgumentException(
                    "Can only set min and max value for options of type INTEGER or NUMBER");
        }

        this.minValue = value;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder setMaxValue(@NonNull final Number value) {
        if (this.type != OptionType.INTEGER && this.type != OptionType.NUMBER) {
            throw new IllegalArgumentException(
                    "Can only set min and max value for options of type INTEGER or NUMBER");
        }

        this.maxValue = value;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder executes(@NonNull final CommandFunction function) {
        this.function = function;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public OptionBuilder executesAfter(@NonNull final Runnable run) {
        this.executeAfter = run;
        return this;
    }
}
