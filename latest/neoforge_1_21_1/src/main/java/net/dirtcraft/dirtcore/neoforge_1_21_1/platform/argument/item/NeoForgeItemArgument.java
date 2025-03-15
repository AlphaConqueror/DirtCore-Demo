/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.item;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.AbstractItemArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.minecraft.core.HolderLookup;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeItemArgument extends AbstractItemArgument<DirtCoreNeoForgePlugin> {

    private static final Collection<String> EXAMPLES =
            Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
    private final NeoForgeItemParser parser;

    private NeoForgeItemArgument(final HolderLookup.@NonNull Provider provider) {
        this.parser = new NeoForgeItemParser(provider);
    }

    public static NeoForgeItemArgument item(final HolderLookup.@NonNull Provider provider) {
        return new NeoForgeItemArgument(provider);
    }

    @Override
    public @NonNull ItemResult parse(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return this.parser.parse(plugin, reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreNeoForgePlugin plugin,
            final CommandContext<DirtCoreNeoForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return this.parser.fillSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
