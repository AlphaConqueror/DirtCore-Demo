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

package net.dirtcraft.dirtcore.neoforge_1_21_1.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.Dynamic2CommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeDataComponentParser {

    public static final char SYNTAX_START_COMPONENTS = '[';
    public static final char SYNTAX_END_COMPONENTS = ']';
    public static final char SYNTAX_COMPONENT_SEPARATOR = ',';
    public static final char SYNTAX_COMPONENT_ASSIGNMENT = '=';
    public static final char SYNTAX_REMOVED_COMPONENT = '!';
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_COMPONENT =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Unknown item component '%s'", o));
    private static final Dynamic2CommandExceptionType ERROR_MALFORMED_COMPONENT =
            new Dynamic2CommandExceptionType(
                    (o1, o2) -> new LiteralMessage("Malformed '%s' component: '%s'", o1, o2));
    private static final SimpleCommandExceptionType ERROR_EXPECTED_COMPONENT =
            new SimpleCommandExceptionType(new LiteralMessage("Expected item component"));
    private static final DynamicCommandExceptionType ERROR_REPEATED_COMPONENT =
            new DynamicCommandExceptionType(o -> new LiteralMessage(
                    "Item component '%s' was repeated, but only one value can be specified", o));
    private final DynamicOps<Tag> registryOps;

    public NeoForgeDataComponentParser(final HolderLookup.@NonNull Provider provider) {
        this.registryOps = provider.createSerializationContext(NbtOps.INSTANCE);
    }

    public static CompletableFuture<Suggestions> suggestStartComponents(
            final SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(NeoForgeDataComponentParser.SYNTAX_START_COMPONENTS));
        }

        return builder.buildFuture();
    }

    public void parse(final StringReader reader,
            final Visitor visitor) throws CommandSyntaxException {
        final int i = reader.getCursor();

        try {
            new State(reader, visitor).parse();
        } catch (final CommandSyntaxException e) {
            reader.setCursor(i);
            throw e;
        }
    }

    private class State {

        private final StringReader reader;
        private final Visitor visitor;

        private State(final StringReader reader, final Visitor visitor) {
            this.reader = reader;
            this.visitor = visitor;
        }

        public static DataComponentType<?> readComponentType(
                final StringReader reader) throws CommandSyntaxException {
            if (!reader.canRead()) {
                throw ERROR_EXPECTED_COMPONENT.createWithContext(reader);
            }

            final int i = reader.getCursor();
            final ResourceLocation resourcelocation = NeoForgeUtils.read(reader);
            final DataComponentType<?> dataComponentType =
                    BuiltInRegistries.DATA_COMPONENT_TYPE.get(resourcelocation);

            if (dataComponentType != null && !dataComponentType.isTransient()) {
                return dataComponentType;
            }
            reader.setCursor(i);
            throw ERROR_UNKNOWN_COMPONENT.createWithContext(reader, resourcelocation);
        }

        private void parse() throws CommandSyntaxException {
            this.reader.expect(SYNTAX_START_COMPONENTS);
            this.visitor.visitSuggestions(this::suggestComponentAssignmentOrRemoval);

            final Set<DataComponentType<?>> set = new ReferenceArraySet<>();

            while (this.reader.canRead() && this.reader.peek() != SYNTAX_END_COMPONENTS) {
                this.reader.skipWhitespace();

                if (this.reader.canRead() && this.reader.peek() == SYNTAX_REMOVED_COMPONENT) {
                    this.reader.skip();
                    this.visitor.visitSuggestions(this::suggestComponent);

                    final DataComponentType<?> dataComponentType = readComponentType(this.reader);

                    if (!set.add(dataComponentType)) {
                        throw ERROR_REPEATED_COMPONENT.create(dataComponentType);
                    }

                    this.visitor.visitRemovedComponent(dataComponentType);
                    this.visitor.visitSuggestions(SuggestionsBuilder.SUGGEST_NOTHING);
                    this.reader.skipWhitespace();
                } else {
                    final DataComponentType<?> datacomponenttype = readComponentType(this.reader);

                    if (!set.add(datacomponenttype)) {
                        throw ERROR_REPEATED_COMPONENT.create(datacomponenttype);
                    }

                    this.visitor.visitSuggestions(this::suggestAssignment);
                    this.reader.skipWhitespace();
                    this.reader.expect(SYNTAX_COMPONENT_ASSIGNMENT);
                    this.visitor.visitSuggestions(SuggestionsBuilder.SUGGEST_NOTHING);
                    this.reader.skipWhitespace();
                    this.readComponent(datacomponenttype);
                    this.reader.skipWhitespace();
                }

                this.visitor.visitSuggestions(this::suggestNextOrEndComponents);

                if (!this.reader.canRead() || this.reader.peek() != SYNTAX_COMPONENT_SEPARATOR) {
                    break;
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.visitor.visitSuggestions(this::suggestComponentAssignmentOrRemoval);

                if (!this.reader.canRead()) {
                    throw ERROR_EXPECTED_COMPONENT.createWithContext(this.reader);
                }
            }

            this.reader.expect(SYNTAX_END_COMPONENTS);
            this.visitor.visitSuggestions(SuggestionsBuilder.SUGGEST_NOTHING);
        }

        private <T> void readComponent(
                final DataComponentType<T> componentType) throws CommandSyntaxException {
            final int i = this.reader.getCursor();
            final Tag tag = new NeoForgeTagParser(this.reader).readValue();
            final DataResult<T> dataresult = componentType.codecOrThrow()
                    .parse(NeoForgeDataComponentParser.this.registryOps, tag);

            this.visitor.visitComponent(componentType, dataresult.getOrThrow(p_339324_ -> {
                this.reader.setCursor(i);
                return ERROR_MALFORMED_COMPONENT.createWithContext(this.reader,
                        componentType.toString(), p_339324_);
            }));
        }

        private CompletableFuture<Suggestions> suggestNextOrEndComponents(
                final SuggestionsBuilder builder) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf(SYNTAX_COMPONENT_SEPARATOR));
                builder.suggest(String.valueOf(SYNTAX_END_COMPONENTS));
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestAssignment(final SuggestionsBuilder builder) {
            if (builder.getRemaining().isEmpty()) {
                builder.suggest(String.valueOf(SYNTAX_COMPONENT_ASSIGNMENT));
            }

            return builder.buildFuture();
        }

        private CompletableFuture<Suggestions> suggestComponentAssignmentOrRemoval(
                final SuggestionsBuilder builder) {
            builder.suggest(String.valueOf(SYNTAX_REMOVED_COMPONENT));
            return this.suggestComponent(builder, String.valueOf(SYNTAX_COMPONENT_ASSIGNMENT));
        }

        private CompletableFuture<Suggestions> suggestComponent(final SuggestionsBuilder builder) {
            return this.suggestComponent(builder, "");
        }

        private CompletableFuture<Suggestions> suggestComponent(final SuggestionsBuilder builder,
                final String resourceLocationSuffix) {
            final String s = builder.getRemaining().toLowerCase(Locale.ROOT);
            SharedSuggestionProvider.filterResources(
                    BuiltInRegistries.DATA_COMPONENT_TYPE.entrySet(), s,
                    entry -> entry.getKey().location(), entry -> {
                        final DataComponentType<?> datacomponenttype = entry.getValue();
                        if (datacomponenttype.codec() != null) {
                            final ResourceLocation resourceLocation = entry.getKey().location();
                            builder.suggest(resourceLocation + resourceLocationSuffix);
                        }
                    });
            return builder.buildFuture();
        }
    }

    public interface Visitor {

        default <T> void visitComponent(@NonNull final DataComponentType<T> componentType,
                @NonNull final T t) {}

        default <T> void visitRemovedComponent(@NonNull final DataComponentType<T> componentType) {}

        default void visitSuggestions(
                @NonNull final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> function) {}
    }
}
