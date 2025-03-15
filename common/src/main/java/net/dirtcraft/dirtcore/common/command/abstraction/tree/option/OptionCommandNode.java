/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.tree.option;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContextBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.ParsedArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.AbstractCommandNodeLike;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;

public class OptionCommandNode<P extends DirtCorePlugin, S extends Sender> extends AbstractCommandNodeLike<S> {

    @NonNull
    protected final String name;
    @NonNull
    protected final String input;
    @NonNull
    protected final String inputLowerCase;
    protected final boolean overridesConsoleUsage;

    public OptionCommandNode(@NonNull final String name, @NonNull final Predicate<S> requirement,
            @NonNull final Permission requiredPermission, final boolean overridesConsoleUsage) {
        super(requirement, requiredPermission);
        this.name = name;
        this.input = CommandDispatcher.OPTION_PREFIX + name;
        this.inputLowerCase = this.input.toLowerCase(Locale.ROOT);
        this.overridesConsoleUsage = overridesConsoleUsage;
    }

    public @NonNull String getName() {
        return this.name;
    }

    public void parse(final P plugin, final StringReader reader,
            final CommandContextBuilder<P, S> contextBuilder) throws CommandSyntaxException {
        final int start = reader.getCursor();

        if (this.parse(reader) == -1) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.optionIncorrect()
                    .createWithContext(reader, this.name);
        }

        contextBuilder.withOption(this.getName(), this.overridesConsoleUsage,
                new ParsedArgument<>(start, reader.getCursor(), null));
    }

    public CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException {
        if (this.inputLowerCase.startsWith(builder.getRemainingLowerCase())) {
            return builder.suggest(this.input).buildFuture();
        }

        return Suggestions.empty();
    }

    public String getUsageText() {
        return this.input;
    }

    public OptionArgumentBuilder<P, S, ?> createBuilder() {
        return OptionArgumentBuilder.option(this.name);
    }

    public Collection<String> getExamples() {
        return Collections.singleton(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof OptionCommandNode)) {
            return false;
        }

        final OptionCommandNode<?, ?> that = (OptionCommandNode<?, ?>) o;

        if (!this.name.equals(that.name)) {
            return false;
        }

        return super.equals(o);
    }

    @Override
    public String toString() {
        return "<option " + this.input + ">";
    }

    protected String getSortedKey() {
        return this.name;
    }

    protected int parse(@NonNull final StringReader reader) {
        final int start = reader.getCursor();

        if (reader.canRead(this.input.length())) {
            final int end = start + this.input.length();

            if (reader.getString().substring(start, end).equals(this.input)) {
                reader.setCursor(end);

                if (!reader.canRead() || reader.peek() == ' ') {
                    return end;
                }

                reader.setCursor(start);
            }
        }

        return -1;
    }
}
