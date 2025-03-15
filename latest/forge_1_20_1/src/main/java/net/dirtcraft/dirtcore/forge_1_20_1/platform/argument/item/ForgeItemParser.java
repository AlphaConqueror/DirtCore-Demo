/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.item;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.AbstractItemParser;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.nbt.TagParser;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeItemParser extends AbstractItemParser {

    private static final char SYNTAX_START_NBT = '{';
    private final HolderLookup<Item> items;
    private final StringReader reader;
    private Holder<Item> result;
    private ResourceLocation resourceLocation;
    @Nullable
    private CompoundTag nbt;
    /**
     * Builder to be used when creating a list of suggestions
     */
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions =
            SuggestionsBuilder.SUGGEST_NOTHING;

    private ForgeItemParser(final HolderLookup<Item> pItems, final StringReader pReader) {
        this.items = pItems;
        this.reader = pReader;
    }

    public static ForgeItemResult parseForItem(final DirtCoreForgePlugin plugin,
            final HolderLookup<Item> lookup,
            final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        try {
            final ForgeItemParser parser = new ForgeItemParser(lookup, reader);
            parser.parse();
            return new ForgeItemResult(plugin, parser.result, parser.resourceLocation, parser.nbt);
        } catch (final CommandSyntaxException e) {
            reader.setCursor(i);
            throw e;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(final HolderLookup<Item> lookup,
            final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        final ForgeItemParser parser = new ForgeItemParser(lookup, reader);

        try {
            parser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return parser.suggestions.apply(builder.createOffset(reader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        final int i = this.reader.getCursor();

        this.resourceLocation = ForgeUtils.read(this.reader);

        final Optional<? extends Holder<Item>> optional =
                this.items.get(ResourceKey.create(Registries.ITEM, this.resourceLocation));

        this.result = optional.orElseThrow(() -> {
            this.reader.setCursor(i);
            return ERROR_UNKNOWN_ITEM.createWithContext(this.reader, this.resourceLocation);
        });
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = (new TagParser(this.reader)).readStruct();
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this::suggestItem;
        this.readItem();
        this.suggestions = this::suggestOpenNbt;

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_START_NBT) {
            this.suggestions = SuggestionsBuilder.SUGGEST_NOTHING;
            this.readNbt();
        }
    }

    private CompletableFuture<Suggestions> suggestOpenNbt(final SuggestionsBuilder builder) {
        if (builder.getRemaining().isEmpty()) {
            builder.suggest(String.valueOf(SYNTAX_START_NBT));
        }

        return builder.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestItem(final SuggestionsBuilder builder) {
        return ForgeSharedSuggestionProvider.suggestResource(
                this.items.listElementIds().map(ResourceKey::location), builder);
    }

    public static class ForgeItemResult implements ItemResult {

        @NonNull
        private final DirtCoreForgePlugin plugin;
        @NonNull
        private final Holder<Item> item;
        @NonNull
        private final ResourceLocation resourceLocation;
        @Nullable
        private CompoundTag tag;

        public ForgeItemResult(@NonNull final DirtCoreForgePlugin plugin,
                @NonNull final Holder<Item> item, @NonNull final ResourceLocation resourceLocation,
                @Nullable final CompoundTag tag) {
            this.plugin = plugin;
            this.item = item;
            this.resourceLocation = resourceLocation;
            this.tag = tag;
        }

        @Override
        public @NonNull String getIdentifier() {
            return this.resourceLocation.toString();
        }

        @Override
        public @NonNull String getMod() {
            return this.resourceLocation.getNamespace();
        }

        @Override
        public @Nullable String getPersistentDataAsString() {
            return this.tag == null ? null : this.tag.toString();
        }

        @Override
        public boolean persistentDataMatches(@Nullable final String s) {
            CompoundTag otherTag = null;

            if (s != null) {
                try {
                    otherTag = net.minecraft.nbt.TagParser.parseTag(s);
                } catch (final Exception ignored) {
                    // tag could not be parsed
                    this.plugin.getLogger().warn("Could not parse tag: '{}'", s);
                    return false;
                }
            }

            if (this.tag == null) {
                // can only be equal when own tag is null or empty
                return otherTag == null || otherTag.isEmpty();
            }

            if (otherTag == null) {
                // can only be equal when own tag is empty
                return this.tag.isEmpty();
            }

            return otherTag.equals(this.tag);
        }

        @Override
        public boolean persistentDataPartiallyMatches(@NonNull final String s) {
            final CompoundTag otherTag;

            try {
                otherTag = net.minecraft.nbt.TagParser.parseTag(s);
            } catch (final Exception ignored) {
                // tag could not be parsed
                this.plugin.getLogger().warn("Could not parse tag: '{}'", s);
                return false;
            }

            if (otherTag.isEmpty()) {
                // contains all tags, since there are none
                return true;
            }

            if (this.tag == null) {
                return false;
            }

            for (final String key : otherTag.getAllKeys()) {
                final Tag t1 = this.tag.get(key);
                final Tag t2 = otherTag.get(key);

                if (!Objects.equals(t1, t2)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void setPersistentDataAsString(
                @Nullable final String s) throws CommandSyntaxException {
            this.tag = s == null ? null : TagParser.parseTag(s);
        }

        @Override
        public @NonNull ItemStack build(@NonNull final DirtCorePlugin ignored) {
            final net.minecraft.world.item.ItemStack itemStack =
                    new net.minecraft.world.item.ItemStack(this.item, 1);

            if (this.tag != null) {
                itemStack.setTag(this.tag);
            }

            return this.plugin.getPlatformFactory().wrapItemStack(itemStack);
        }

        @Override
        public boolean isEmpty() {
            return this.item.value() == Items.AIR;
        }
    }
}
