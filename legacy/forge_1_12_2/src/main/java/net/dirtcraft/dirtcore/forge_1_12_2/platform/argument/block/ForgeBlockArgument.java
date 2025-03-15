/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.block;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeBlockArgument extends AbstractBlockArgument<DirtCoreForgePlugin> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick");

    private ForgeBlockArgument() {}

    public static ForgeBlockArgument block() {
        return new ForgeBlockArgument();
    }

    @Override
    public @NonNull BlockResult parse(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return ForgeBlockParser.parseForBlock(reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreForgePlugin plugin, final CommandContext<DirtCoreForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return ForgeBlockParser.fillSuggestions(builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
