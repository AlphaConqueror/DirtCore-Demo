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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.item;

import com.mojang.serialization.DataResult;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.AbstractItemParser;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeDataComponentParser;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.mutable.MutableObject;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NeoForgeItemParser extends AbstractItemParser {

    // TODO: persistent data suggestions need to be fixed, they sometimes overwrite the actual
    //  items when using space -> delete

    private static final DynamicCommandExceptionType ERROR_MALFORMED_ITEM =
            new DynamicCommandExceptionType(o -> new LiteralMessage("Malformed item: '%s'", o));
    private final HolderLookup.Provider provider;
    private final HolderLookup.RegistryLookup<Item> items;
    private final NeoForgeDataComponentParser parser;

    protected NeoForgeItemParser(final HolderLookup.Provider provider) {
        this.provider = provider;
        this.items = provider.lookupOrThrow(Registries.ITEM);
        this.parser = new NeoForgeDataComponentParser(provider);
    }

    private static void validateComponents(final StringReader reader, final Holder<Item> holder,
            final DataComponentPatch dataComponentPatch) throws CommandSyntaxException {
        final DataComponentMap datacomponentmap =
                PatchedDataComponentMap.fromPatch(holder.value().components(), dataComponentPatch);
        final DataResult<Unit> dataresult =
                net.minecraft.world.item.ItemStack.validateComponents(datacomponentmap);

        dataresult.getOrThrow(s -> ERROR_MALFORMED_ITEM.createWithContext(reader, s));
    }

    public NeoForgeItemResult parse(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final MutableObject<Holder<Item>> mutableObject = new MutableObject<>();
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();

        this.parse(reader, new Visitor() {
            @Override
            public void visitItem(@NonNull final Holder<Item> holder) {
                mutableObject.setValue(holder);
            }

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

        final Holder<Item> holder =
                Objects.requireNonNull(mutableObject.getValue(), "Parser gave no item");
        final DataComponentPatch dataComponentPatch = builder.build();

        validateComponents(reader, holder, dataComponentPatch);
        return new NeoForgeItemResult(plugin, this.provider, holder, dataComponentPatch);
    }

    public void parse(final StringReader reader,
            final Visitor visitor) throws CommandSyntaxException {
        final int i = reader.getCursor();

        try {
            new ItemState(reader, visitor).parse();
        } catch (final CommandSyntaxException commandsyntaxexception) {
            reader.setCursor(i);
            throw commandsyntaxexception;
        }
    }

    public CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());

        reader.setCursor(builder.getStart());

        final SuggestionsVisitor visitor = new SuggestionsVisitor();
        final ItemState state = new ItemState(reader, visitor);

        try {
            state.parse();
        } catch (final CommandSyntaxException ignored) {}

        return visitor.resolveSuggestions(builder, reader);
    }

    public static class NeoForgeItemResult implements ItemResult {

        final HolderLookup.@NonNull Provider provider;
        @NonNull
        private final DirtCoreNeoForgePlugin plugin;
        @NonNull
        private final Holder<Item> item;
        @NonNull
        private DataComponentPatch components;

        public NeoForgeItemResult(@NonNull final DirtCoreNeoForgePlugin plugin,
                final HolderLookup.@NonNull Provider provider, @NonNull final Holder<Item> item,
                @NonNull final DataComponentPatch components) {
            this.plugin = plugin;
            this.provider = provider;
            this.item = item;
            this.components = components;
        }

        @Override
        public @NonNull String getIdentifier() {
            return this.item.getRegisteredName();
        }

        @Override
        public @NonNull String getMod() {
            return this.item.unwrapKey().map(key -> key.location().getNamespace())
                    .orElse("[unregistered]");
        }

        @Override
        public @Nullable String getPersistentDataAsString() {
            return NeoForgeUtils.patchedDataToString(this.plugin, this.components);
        }

        @Override
        public boolean persistentDataMatches(@Nullable final String s) {
            DataComponentPatch otherComponents = s == null ? DataComponentPatch.EMPTY
                    : NeoForgeUtils.stringToDataComponentPatch(this.plugin, this.provider, s);

            if (otherComponents == null) {
                otherComponents = DataComponentPatch.EMPTY;
            }

            return Objects.equals(this.components, otherComponents);
        }

        @Override
        public boolean persistentDataPartiallyMatches(@NonNull final String s) {
            final DataComponentPatch otherComponents =
                    NeoForgeUtils.stringToDataComponentPatch(this.plugin, this.provider, s);

            if (otherComponents == null || otherComponents.isEmpty()) {
                // contains all components, since there are none
                return true;
            }

            if (this.components.isEmpty()) {
                return false;
            }

            for (final Map.Entry<DataComponentType<?>, Optional<?>> entry :
                    otherComponents.entrySet()) {
                if (!Objects.equals(this.components.get(entry.getKey()), entry.getValue())) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void setPersistentDataAsString(
                @Nullable final String s) throws CommandSyntaxException {
            if (s == null) {
                this.components = DataComponentPatch.EMPTY;
                return;
            }

            final DataComponentPatch dataComponentPatch =
                    NeoForgeUtils.stringToDataComponentPatch(this.plugin, this.provider, s);

            if (dataComponentPatch == null) {
                return;
            }

            this.components = dataComponentPatch;
        }

        @Override
        public @NonNull ItemStack build(@NonNull final DirtCorePlugin ignored) {
            final net.minecraft.world.item.ItemStack itemStack =
                    new net.minecraft.world.item.ItemStack(this.item, 1);

            if (!this.components.isEmpty()) {
                itemStack.applyComponents(this.components);
            }

            return this.plugin.getPlatformFactory().wrapItemStack(itemStack);
        }

        @Override
        public boolean isEmpty() {
            return this.item.value() == Items.AIR;
        }
    }

    public static class SuggestionsVisitor implements Visitor {

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

    private class ItemState {

        private final StringReader reader;
        private final Visitor visitor;

        private ItemState(final StringReader reader, final Visitor visitor) {
            this.reader = reader;
            this.visitor = visitor;
        }

        public void parse() throws CommandSyntaxException {
            this.visitor.visitSuggestions(this::suggestItem);
            this.readItem();
            this.visitor.visitSuggestions(NeoForgeDataComponentParser::suggestStartComponents);

            if (this.reader.canRead()
                    && this.reader.peek() == NeoForgeDataComponentParser.SYNTAX_START_COMPONENTS) {
                this.visitor.visitSuggestions(SuggestionsBuilder.SUGGEST_NOTHING);
                NeoForgeItemParser.this.parser.parse(this.reader, this.visitor);
            }
        }

        private void readItem() throws CommandSyntaxException {
            final int i = this.reader.getCursor();
            final ResourceLocation resourcelocation = NeoForgeUtils.read(this.reader);

            this.visitor.visitItem(NeoForgeItemParser.this.items.get(
                    ResourceKey.create(Registries.ITEM, resourcelocation)).orElseThrow(() -> {
                this.reader.setCursor(i);
                return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, resourcelocation);
            }));
        }

        private CompletableFuture<Suggestions> suggestItem(final SuggestionsBuilder builder) {
            return NeoForgeSharedSuggestionProvider.suggestResource(
                    NeoForgeItemParser.this.items.listElementIds().map(ResourceKey::location),
                    builder);
        }
    }

    public interface Visitor extends NeoForgeDataComponentParser.Visitor {

        default void visitItem(@NonNull final Holder<Item> holder) {}
    }
}
