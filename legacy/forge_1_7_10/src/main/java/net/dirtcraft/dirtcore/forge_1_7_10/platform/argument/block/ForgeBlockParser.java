/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.block;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockParser;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeBlockParser extends AbstractBlockParser {

    private final StringReader reader;
    private GameRegistry.UniqueIdentifier uniqueIdentifier = null;
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
            return new ForgeBlockResult(parser.block, parser.uniqueIdentifier, parser.metadata);
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
        //noinspection unchecked
        return ForgeSharedSuggestionProvider.suggest(
                (Set<String>) GameData.getBlockRegistry().getKeys(), builder);
    }

    private void readBlock() throws CommandSyntaxException {
        final int start = this.reader.getCursor();

        try {
            this.block =
                    (Block) Block.blockRegistry.getObject(ForgeUtils.readIdentifier(this.reader));
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
        }

        this.uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(this.block);
        this.metadata = ForgeUtils.readMetadata(this.reader);
    }

    public static class ForgeBlockResult implements BlockResult {

        @NonNull
        private final Block block;
        private final GameRegistry.@NonNull UniqueIdentifier uniqueIdentifier;
        private final int metadata;

        public ForgeBlockResult(@NonNull final Block block,
                final GameRegistry.@NonNull UniqueIdentifier uniqueIdentifier, final int metadata) {
            this.block = block;
            this.uniqueIdentifier = uniqueIdentifier;
            this.metadata = metadata;
        }

        @Override
        public @NonNull String getIdentifier() {
            final StringBuilder builder = new StringBuilder(this.uniqueIdentifier.toString());

            if (this.metadata > 0) {
                builder.append(ForgeUtils.IDENTIFIER_SEPARATOR)
                        .append(this.metadata);
            }

            return builder.toString();
        }

        @Override
        public boolean isEmpty() {
            return this.block == Blocks.air || this.block.getMaterial() == Material.air;
        }
    }
}
