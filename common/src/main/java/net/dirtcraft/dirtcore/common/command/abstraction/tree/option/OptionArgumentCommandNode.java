/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.tree.option;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionRequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.ParsedArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class OptionArgumentCommandNode<P extends DirtCorePlugin, S extends Sender, T> extends OptionCommandNode<P, S> {

    @NonNull
    private final ArgumentType<P, T> type;
    @Nullable
    private final SuggestionProvider<P, S> customSuggestions;

    public OptionArgumentCommandNode(@NonNull final String name,
            @NonNull final Predicate<S> requirement, @NonNull final Permission requiredPermission,
            final boolean overridesConsoleUsage, @NonNull final ArgumentType<P, T> type,
            @Nullable final SuggestionProvider<P, S> customSuggestions) {
        super(name, requirement, requiredPermission, overridesConsoleUsage);
        this.type = type;
        this.customSuggestions = customSuggestions;
    }

    @Override
    public void parse(final P plugin, final StringReader reader,
            final CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int end = this.parse(reader);

        if (end == -1) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.optionIncorrect()
                    .createWithContext(reader, this.name);
        }

        // make sure there actually is something
        if (!reader.canRead(2) || reader.peek() != CommandDispatcher.ARGUMENT_SEPARATOR_CHAR
                || reader.peek(1) == ' ') {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.optionIncomplete()
                    .createWithContext(reader);
        }

        reader.skipWhitespace();

        final T result = this.type.parse(plugin, reader);
        final ParsedArgument<T> parsed = new ParsedArgument<>(start, reader.getCursor(), result);

        contextBuilder.withOption(this.getName(), this.overridesConsoleUsage, parsed);
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        final String remainingLowerCase = builder.getRemainingLowerCase();

        if (this.inputLowerCase.startsWith(remainingLowerCase)) {
            return builder.suggest(this.input).buildFuture();
        }

        if (!remainingLowerCase.startsWith(
                this.inputLowerCase + CommandDispatcher.ARGUMENT_SEPARATOR)) {
            return Suggestions.empty();
        }

        final int typeStart = this.input.length() + 1;
        final SuggestionsBuilder typeBuilder = builder.createOffset(builder.getStart() + typeStart);
        final CompletableFuture<Suggestions> suggestions =
                this.customSuggestions == null ? this.type.listSuggestions(plugin, context,
                        typeBuilder) : this.customSuggestions.getSuggestions(context, typeBuilder);

        suggestions.whenComplete((s, ignored) -> s.getList().forEach(suggestion -> builder.suggest(
                this.input + CommandDispatcher.ARGUMENT_SEPARATOR + suggestion.getText())));
        return builder.buildFuture();
    }

    @Override
    public String getUsageText() {
        return super.getUsageText() + CommandDispatcher.ARGUMENT_SEPARATOR
                + CommandDispatcher.USAGE_REQUIRED_OPEN + this.type.getName()
                + CommandDispatcher.USAGE_REQUIRED_CLOSE;
    }

    @Override
    public OptionRequiredArgumentBuilder<P, S, ?> createBuilder() {
        final OptionRequiredArgumentBuilder<P, S, ?> builder =
                OptionRequiredArgumentBuilder.option(this.name, this.type);

        builder.suggests(this.customSuggestions);
        return builder;
    }

    @Override
    public Collection<String> getExamples() {
        return this.type.getExamples().stream().map(s -> this.name + ' ' + s)
                .collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.type.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "<option " + this.input + ":" + this.type + ">";
    }

    @NonNull
    public ArgumentType<?, T> getType() {
        return this.type;
    }

    @Nullable
    public SuggestionProvider<P, S> getCustomSuggestions() {
        return this.customSuggestions;
    }
}
