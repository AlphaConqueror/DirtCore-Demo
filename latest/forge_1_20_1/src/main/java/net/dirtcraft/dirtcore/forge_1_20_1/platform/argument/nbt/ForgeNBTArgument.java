/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.nbt;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeNBTArgument extends AbstractPersistentDataArgument<DirtCoreForgePlugin> {

    private static final Collection<String> EXAMPLES =
            Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

    private ForgeNBTArgument() {}

    public static ForgeNBTArgument nbt() {
        return new ForgeNBTArgument();
    }

    @Override
    public @NonNull String parse(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return (new TagParser(reader)).readValue().getAsString();
    }

    @Override
    public @NonNull String getName() {
        return "nbt";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreForgePlugin plugin, final CommandContext<DirtCoreForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf('{'));
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
