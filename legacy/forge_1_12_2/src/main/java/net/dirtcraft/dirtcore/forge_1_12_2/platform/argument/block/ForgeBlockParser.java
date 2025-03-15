/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockParser;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeBlockParser extends AbstractBlockParser {

    private final StringReader reader;
    private ResourceLocation id = new ResourceLocation("");
    private int metadata = 0;
    private Block block = null;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions =
            SuggestionsBuilder.SUGGEST_NOTHING;

    private ForgeBlockParser(final StringReader pReader) {
        this.reader = pReader;
    }

    public static ForgeBlockResult parseForBlock(final String input) throws CommandSyntaxException {
        return parseForBlock(new StringReader(input));
    }

    public static ForgeBlockResult parseForBlock(
            final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        try {
            final ForgeBlockParser parser = new ForgeBlockParser(reader);
            parser.parse();
            return new ForgeBlockResult(parser.block, parser.id, parser.metadata);
        } catch (final CommandSyntaxException commandsyntaxexception) {
            reader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());

        reader.setCursor(builder.getStart());

        final ForgeBlockParser parser = new ForgeBlockParser(reader);

        try {
            parser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return parser.suggestions.apply(builder.createOffset(reader.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestBlock;
        this.readBlock();
    }

    private CompletableFuture<Suggestions> suggestBlock(final SuggestionsBuilder builder) {
        return ForgeSharedSuggestionProvider.suggestResource(Block.REGISTRY.getKeys(), builder);
    }

    private void readBlock() throws CommandSyntaxException {
        final int start = this.reader.getCursor();

        try {
            final ResourceLocation resourceLocation = ForgeUtils.read(this.reader);

            if (Block.REGISTRY.containsKey(resourceLocation)) {
                this.block = Block.REGISTRY.getObject(resourceLocation);
                this.id = resourceLocation;
            }
        } catch (final CommandSyntaxException ignored) {}

        if (this.block == null) {
            this.reader.setCursor(start);

            try {
                final int tag = ForgeUtils.readTag(this.reader);
                this.block = Block.getBlockById(tag);
            } catch (final CommandSyntaxException ignored) {}

            if (this.block == null) {
                this.reader.setCursor(start);
                throw ERROR_UNKNOWN_BLOCK_NO_CONTEXT.createWithContext(this.reader);
            }

            this.id = this.block.delegate.name();
        }

        this.metadata = ForgeUtils.readMetadata(this.reader);
    }

    public static class ForgeBlockResult implements BlockResult {

        @NonNull
        private final Block block;
        @NonNull
        private final ResourceLocation id;
        private final int metadata;

        public ForgeBlockResult(@NonNull final Block block, @NonNull final ResourceLocation id,
                final int metadata) {
            this.block = block;
            this.id = id;
            this.metadata = metadata;
        }

        @Override
        public @NonNull String getIdentifier() {
            final StringBuilder builder = new StringBuilder(this.id.toString());

            if (this.metadata > 0) {
                builder.append(ForgeUtils.IDENTIFIER_SEPARATOR)
                        .append(this.metadata);
            }

            return builder.toString();
        }

        @Override
        public boolean isEmpty() {
            return this.block == Blocks.AIR
                    || this.block.getBlockState().getBaseState().getMaterial() == Material.AIR;
        }
    }
}
