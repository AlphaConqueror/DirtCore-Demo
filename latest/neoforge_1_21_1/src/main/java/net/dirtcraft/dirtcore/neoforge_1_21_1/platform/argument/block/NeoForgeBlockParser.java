/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockParser;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeBlockParser extends AbstractBlockParser {

    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK =
            new DynamicCommandExceptionType(o -> new LiteralMessage("Unknown block type '%s'", o));
    private final HolderLookup<Block> blocks;
    private final StringReader reader;
    private ResourceLocation id = ResourceLocation.withDefaultNamespace("");
    private Block block = null;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions =
            SuggestionsBuilder.SUGGEST_NOTHING;

    private NeoForgeBlockParser(final HolderLookup<Block> pBlocks, final StringReader pReader) {
        this.blocks = pBlocks;
        this.reader = pReader;
    }

    public static ForgeBlockResult parseForBlock(final HolderLookup<Block> pLookup,
            final String pInput) throws CommandSyntaxException {
        return parseForBlock(pLookup, new StringReader(pInput));
    }

    public static ForgeBlockResult parseForBlock(final HolderLookup<Block> pLookup,
            final StringReader pReader) throws CommandSyntaxException {
        final int i = pReader.getCursor();

        try {
            final NeoForgeBlockParser parser = new NeoForgeBlockParser(pLookup, pReader);
            parser.parse();
            return new ForgeBlockResult(parser.id, parser.block);
        } catch (final CommandSyntaxException commandsyntaxexception) {
            pReader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(final HolderLookup<Block> pLookup,
            final SuggestionsBuilder pBuilder) {
        final StringReader stringreader = new StringReader(pBuilder.getInput());
        stringreader.setCursor(pBuilder.getStart());
        final NeoForgeBlockParser parser = new NeoForgeBlockParser(pLookup, stringreader);

        try {
            parser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return parser.suggestions.apply(pBuilder.createOffset(stringreader.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestItem;
        this.readBlock();
    }

    private CompletableFuture<Suggestions> suggestItem(final SuggestionsBuilder builder) {
        return NeoForgeSharedSuggestionProvider.suggestResource(
                this.blocks.listElementIds().map(ResourceKey::location), builder);
    }

    private void readBlock() throws CommandSyntaxException {
        final int i = this.reader.getCursor();

        this.id = NeoForgeUtils.read(this.reader);
        // check if block exists
        this.block =
                this.blocks.get(ResourceKey.create(Registries.BLOCK, this.id)).orElseThrow(() -> {
                    this.reader.setCursor(i);
                    return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
                }).value();
    }

    public static class ForgeBlockResult implements BlockResult {

        @NonNull
        private final ResourceLocation resourceLocation;
        @NonNull
        private final Block block;

        public ForgeBlockResult(@NonNull final ResourceLocation resourceLocation,
                @NonNull final Block block) {
            this.resourceLocation = resourceLocation;
            this.block = block;
        }

        @Override
        public @NonNull String getIdentifier() {
            return this.resourceLocation.toString();
        }

        @Override
        public boolean isEmpty() {
            return this.block == Blocks.AIR;
        }
    }
}
