/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.builder.option;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionArgumentCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class OptionRequiredArgumentBuilder<P extends DirtCorePlugin, S extends Sender, T> extends OptionArgumentBuilder<P, S, OptionRequiredArgumentBuilder<P, S, T>> {

    @NonNull
    private final ArgumentType<P, T> type;
    @Nullable
    private SuggestionProvider<P, S> suggestionsProvider = null;

    private OptionRequiredArgumentBuilder(@NonNull final String optionName,
            @NonNull final ArgumentType<P, T> type) {
        super(optionName);
        this.type = type;
    }

    public static <P extends DirtCorePlugin, S extends Sender, T> OptionRequiredArgumentBuilder<P
            , S, T> option(
            @NonNull final String name, @NonNull final ArgumentType<P, T> type) {
        return new OptionRequiredArgumentBuilder<>(name, type);
    }

    @Override
    public OptionArgumentCommandNode<P, S, T> build() {
        return new OptionArgumentCommandNode<>(this.getOptionName(), this.getRequirement(),
                this.getRequiredPermission(), this.isOverridingConsoleUsage(), this.getType(),
                this.getSuggestionsProvider());
    }

    @Override
    protected OptionRequiredArgumentBuilder<P, S, T> getThis() {
        return this;
    }

    public OptionRequiredArgumentBuilder<P, S, T> suggests(
            final SuggestionProvider<P, S> provider) {
        this.suggestionsProvider = provider;
        return this.getThis();
    }

    @NonNull
    public ArgumentType<P, T> getType() {
        return this.type;
    }

    @Nullable
    public SuggestionProvider<P, S> getSuggestionsProvider() {
        return this.suggestionsProvider;
    }
}
