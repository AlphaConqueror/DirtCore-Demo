/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.block;

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
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeBlockArgument extends AbstractBlockArgument<DirtCoreForgePlugin> {

    private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick");
    private final HolderLookup<Block> blocks;

    private ForgeBlockArgument(@NonNull final CommandBuildContext context) {
        this.blocks = context.holderLookup(Registries.BLOCK);
    }

    public static ForgeBlockArgument block(@NonNull final CommandBuildContext context) {
        return new ForgeBlockArgument(context);
    }

    @Override
    public @NonNull BlockResult parse(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return ForgeBlockParser.parseForBlock(this.blocks, reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreForgePlugin plugin, final CommandContext<DirtCoreForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return ForgeBlockParser.fillSuggestions(this.blocks, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
