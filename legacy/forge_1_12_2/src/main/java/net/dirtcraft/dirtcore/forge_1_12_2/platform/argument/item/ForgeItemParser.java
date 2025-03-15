/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.item;

import java.util.Objects;
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
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeItemParser extends AbstractItemParser {

    private static final char SYNTAX_START_NBT = '{';
    private final StringReader reader;
    private Item item = null;
    private ResourceLocation id = new ResourceLocation("");
    private int metadata = 0;
    @Nullable
    private NBTTagCompound nbt;
    /**
     * Builder to be used when creating a list of suggestions
     */
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions =
            SuggestionsBuilder.SUGGEST_NOTHING;

    private ForgeItemParser(final StringReader reader) {
        this.reader = reader;
    }

    public static ForgeItemResult parseForItem(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        try {
            final ForgeItemParser parser = new ForgeItemParser(reader);
            parser.parse();
            return new ForgeItemResult(plugin, parser.item, parser.id, parser.metadata, parser.nbt);
        } catch (final CommandSyntaxException e) {
            reader.setCursor(i);
            throw e;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());

        reader.setCursor(builder.getStart());

        final ForgeItemParser parser = new ForgeItemParser(reader);

        try {
            parser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return parser.suggestions.apply(builder.createOffset(reader.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        final int start = this.reader.getCursor();

        try {
            final ResourceLocation resourceLocation = ForgeUtils.read(this.reader);

            if (Block.REGISTRY.containsKey(resourceLocation)) {
                this.item = Item.REGISTRY.getObject(resourceLocation);
                this.id = resourceLocation;
            }
        } catch (final CommandSyntaxException ignored) {}

        if (this.item == null) {
            this.reader.setCursor(start);

            try {
                final int tag = ForgeUtils.readTag(this.reader);
                this.item = Item.getItemById(tag);
            } catch (final CommandSyntaxException ignored) {}

            if (this.item == null) {
                this.reader.setCursor(start);
                throw ERROR_UNKNOWN_ITEM_NO_CONTEXT.createWithContext(this.reader);
            }

            this.id = this.item.delegate.name();
        }

        this.metadata = ForgeUtils.readMetadata(this.reader);
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = ForgeUtils.parseForTag(this.reader);
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
        return ForgeSharedSuggestionProvider.suggestResource(Item.REGISTRY.getKeys(), builder);
    }

    public static class ForgeItemResult implements ItemResult {

        @NonNull
        private final DirtCoreForgePlugin plugin;
        @NonNull
        private final Item item;
        @NonNull
        private final ResourceLocation id;
        private final int metadata;
        @Nullable
        private NBTTagCompound tag;

        public ForgeItemResult(@NonNull final DirtCoreForgePlugin plugin, @NonNull final Item item,
                @NonNull final ResourceLocation id, final int metadata,
                @Nullable final NBTTagCompound tag) {
            this.plugin = plugin;
            this.item = item;
            this.id = id;
            this.metadata = metadata;
            this.tag = tag;
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
        public @NonNull String getMod() {
            return this.id.getNamespace();
        }

        @Override
        public @Nullable String getPersistentDataAsString() {
            return this.tag == null ? null : this.tag.toString();
        }

        @Override
        public boolean persistentDataMatches(@Nullable final String s) {
            NBTTagCompound otherTag = null;

            if (s != null) {
                try {
                    otherTag = JsonToNBT.getTagFromJson(s);
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
            final NBTTagCompound otherTag;

            try {
                otherTag = JsonToNBT.getTagFromJson(s);
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

            for (final String key : otherTag.getKeySet()) {
                final NBTBase t1 = this.tag.getTag(key);
                final NBTBase t2 = otherTag.getTag(key);

                if (!Objects.equals(t1, t2)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public void setPersistentDataAsString(
                @Nullable final String s) throws CommandSyntaxException {
            this.tag = s == null ? null : ForgeUtils.parseForTag(new StringReader(s));
        }

        @Override
        public @NonNull ItemStack build(@NonNull final DirtCorePlugin ignored) {
            final net.minecraft.item.ItemStack itemStack =
                    new net.minecraft.item.ItemStack(this.item, 1, this.metadata);

            if (this.tag != null) {
                itemStack.setTagCompound(this.tag);
            }

            return this.plugin.getPlatformFactory().wrapItemStack(itemStack);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
