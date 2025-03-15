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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.datacomponent;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeDataComponentParser;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeDataComponentPatchArgument extends AbstractPersistentDataArgument<DirtCoreNeoForgePlugin> {

    private static final DynamicCommandExceptionType ERROR_COULD_NOT_PARSE_DATA_COMPONENTS =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Count not parse data components: '%s'", o));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");
    private final NeoForgeDataComponentParser parser;

    private NeoForgeDataComponentPatchArgument(final HolderLookup.@NonNull Provider provider) {
        this.parser = new NeoForgeDataComponentParser(provider);
    }

    public static NeoForgeDataComponentPatchArgument dataComponentPatch(
            final HolderLookup.@NonNull Provider provider) {
        return new NeoForgeDataComponentPatchArgument(provider);
    }

    @Override
    public @NonNull String parse(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();

        this.parser.parse(reader, new NeoForgeDataComponentParser.Visitor() {
            @Override
            public <T> void visitComponent(@NonNull final DataComponentType<T> componentType,
                    @NonNull final T t) {
                builder.set(componentType, t);
            }

            @Override
            public <T> void visitRemovedComponent(
                    @NonNull final DataComponentType<T> componentType) {
                builder.remove(componentType);
            }
        });

        final String s = NeoForgeUtils.patchedDataToString(plugin, builder.build());

        if (s == null) {
            throw ERROR_COULD_NOT_PARSE_DATA_COMPONENTS.createWithContext(reader,
                    reader.getString());
        }

        return s;
    }

    @Override
    public @NonNull String getName() {
        return "persistentData";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreNeoForgePlugin plugin,
            final CommandContext<DirtCoreNeoForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());

        reader.setCursor(builder.getStart());

        final SuggestionsVisitor visitor = new SuggestionsVisitor();

        try {
            visitor.visitSuggestions(NeoForgeDataComponentParser::suggestStartComponents);

            if (reader.canRead()
                    && reader.peek() == NeoForgeDataComponentParser.SYNTAX_START_COMPONENTS) {
                visitor.visitSuggestions(SuggestionsBuilder.SUGGEST_NOTHING);
                this.parser.parse(reader, visitor);
            }
        } catch (final CommandSyntaxException ignored) {}

        return visitor.resolveSuggestions(builder, reader);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static class SuggestionsVisitor implements NeoForgeDataComponentParser.Visitor {

        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions =
                SuggestionsBuilder.SUGGEST_NOTHING;

        @Override
        public void visitSuggestions(
                @NonNull final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions) {
            this.suggestions = suggestions;
        }

        public CompletableFuture<Suggestions> resolveSuggestions(final SuggestionsBuilder builder,
                final StringReader reader) {
            return this.suggestions.apply(builder.createOffset(reader.getCursor()));
        }
    }
}
