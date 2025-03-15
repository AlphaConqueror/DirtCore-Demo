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

package net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.item;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Objects;
import java.util.Set;
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
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeUtils;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeItemParser extends AbstractItemParser {

    private static final char SYNTAX_START_NBT = '{';
    private final StringReader reader;
    private Item item = null;
    private GameRegistry.UniqueIdentifier uniqueIdentifier = null;
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
            return new ForgeItemResult(plugin, parser.item, parser.uniqueIdentifier,
                    parser.metadata, parser.nbt);
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
            this.item = (Item) Item.itemRegistry.getObject(ForgeUtils.readIdentifier(this.reader));
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
        }

        this.uniqueIdentifier = GameRegistry.findUniqueIdentifierFor(this.item);
        this.metadata = ForgeUtils.readMetadata(this.reader);
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = ForgeUtils.parseForNBTTagCompound(this.reader);
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
        //noinspection unchecked
        return ForgeSharedSuggestionProvider.suggest(
                (Set<String>) GameData.getItemRegistry().getKeys(), builder);
    }

    public static class ForgeItemResult implements ItemResult {

        @NonNull
        private final DirtCoreForgePlugin plugin;
        @NonNull
        private final Item item;
        private final GameRegistry.@NonNull UniqueIdentifier uniqueIdentifier;
        private final int metadata;
        @Nullable
        private NBTTagCompound tag;

        public ForgeItemResult(@NonNull final DirtCoreForgePlugin plugin, @NonNull final Item item,
                final GameRegistry.@NonNull UniqueIdentifier uniqueIdentifier, final int metadata,
                @Nullable final NBTTagCompound tag) {
            this.plugin = plugin;
            this.item = item;
            this.uniqueIdentifier = uniqueIdentifier;
            this.metadata = metadata;
            this.tag = tag;
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
        public @NonNull String getMod() {
            return this.uniqueIdentifier.modId;
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
                    final NBTBase tag = JsonToNBT.func_150315_a(s);

                    if (!(tag instanceof NBTTagCompound)) {
                        // tag is not a compound tag
                        this.plugin.getLogger().warn("Tag is not a compound tag: '{}'", s);
                        return false;
                    }

                    otherTag = (NBTTagCompound) tag;
                } catch (final Exception ignored) {
                    // tag could not be parsed
                    this.plugin.getLogger().warn("Could not parse tag: '{}'", s);
                    return false;
                }
            }

            if (this.tag == null) {
                // can only be equal when own tag is null or empty
                return otherTag == null || otherTag.hasNoTags();
            }

            if (otherTag == null) {
                // can only be equal when own tag is empty
                return this.tag.hasNoTags();
            }

            return otherTag.equals(this.tag);
        }

        @Override
        public boolean persistentDataPartiallyMatches(@NonNull final String s) {
            final NBTTagCompound otherTag;

            try {
                final NBTBase tag = JsonToNBT.func_150315_a(s);

                if (!(tag instanceof NBTTagCompound)) {
                    // tag is not a compound tag
                    this.plugin.getLogger().warn("Tag is not a compound tag: '{}'", s);
                    return false;
                }

                otherTag = (NBTTagCompound) tag;
            } catch (final Exception ignored) {
                // tag could not be parsed
                this.plugin.getLogger().warn("Could not parse tag: '{}'", s);
                return false;
            }

            if (otherTag.hasNoTags()) {
                // contains all tags, since there are none
                return true;
            }

            if (this.tag == null) {
                return false;
            }

            //noinspection unchecked
            for (final String key : (Set<String>) otherTag.getKeySet()) {
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
            this.tag = s == null ? null : ForgeUtils.parseForNBTTagCompound(new StringReader(s));
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
