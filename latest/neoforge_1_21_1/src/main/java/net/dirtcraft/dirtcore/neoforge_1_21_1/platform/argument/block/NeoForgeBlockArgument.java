/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.block;

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
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeBlockArgument extends AbstractBlockArgument<DirtCoreNeoForgePlugin> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick");
    private final HolderLookup<Block> blocks;

    private NeoForgeBlockArgument(final HolderLookup.@NonNull Provider provider) {
        this.blocks = provider.lookupOrThrow(Registries.BLOCK);
    }

    public static NeoForgeBlockArgument block(final HolderLookup.@NonNull Provider provider) {
        return new NeoForgeBlockArgument(provider);
    }

    @Override
    public @NonNull BlockResult parse(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return NeoForgeBlockParser.parseForBlock(this.blocks, reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreNeoForgePlugin plugin,
            final CommandContext<DirtCoreNeoForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return NeoForgeBlockParser.fillSuggestions(this.blocks, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
