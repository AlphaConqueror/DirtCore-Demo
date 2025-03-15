/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.misc;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SelectionArgumentType implements ArgumentType<DirtCorePlugin, String> {

    private static final DynamicCommandExceptionType ERROR_NOT_IN_SELECTION =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Expected value not in selection: " + o));

    private final Supplier<Collection<String>> selectionSupplier;

    private SelectionArgumentType(final Supplier<Collection<String>> selectionSupplier) {
        this.selectionSupplier = selectionSupplier;
    }

    public static SelectionArgumentType selection(
            final Supplier<Collection<String>> selectionSupplier) {
        return new SelectionArgumentType(selectionSupplier);
    }

    public static String getSelection(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public @NonNull String parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        while (reader.canRead() && reader.peek() != CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
            reader.skip();
        }

        final Collection<String> selection = this.selectionSupplier.get();
        final String s = reader.getString().substring(i, reader.getCursor());

        if (selection.contains(s)) {
            return s;
        }

        throw ERROR_NOT_IN_SELECTION.createWithContext(reader, selection);
    }

    @Override
    public @NonNull String getName() {
        return "selection";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCorePlugin plugin, final CommandContext<DirtCorePlugin, S> context,
            final SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggestFuture(this.selectionSupplier.get(), builder);
    }
}
