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

package net.dirtcraft.dirtcore.forge_1_12_2.platform;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.item.Material;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.AbstractItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.ItemStackFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ForgeItemStackFactory extends ItemStackFactory<DirtCoreForgePlugin, ItemStack> {

    public ForgeItemStackFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    public @NonNull Optional<ItemStack> createItemStack(@NonNull final String identifier,
            final int count, @Nullable final String persistentData) {
        final String[] split = identifier.split(String.valueOf(ForgeUtils.IDENTIFIER_SEPARATOR));
        final int length = split.length;

        if (length < 2 || length > 3) {
            this.getPlugin().getLogger()
                    .warn("Identifier '{}' does follow the pattern 'modid:name(:metadata)?'.",
                            identifier);
            return Optional.empty();
        }

        final int metadata;

        if (length == 3) {
            final String metadataAsString = split[2];

            try {
                metadata = Integer.parseInt(metadataAsString);
            } catch (final NumberFormatException e) {
                this.getPlugin().getLogger()
                        .warn("Metadata '{}' is not a number.", metadataAsString);
                return Optional.empty();
            }
        } else {
            metadata = 0;
        }

        try {
            return Optional.of(
                    GameRegistry.makeItemStack(identifier, metadata, count, persistentData));
        } catch (final Exception e) {
            this.getPlugin().getLogger()
                    .warn("There was an issue creating an item stack. {}", e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    protected @NonNull Component getDisplayName(@NonNull final ItemStack itemStack) {
        final NBTTagCompound tag = new NBTTagCompound();

        itemStack.writeToNBT(tag);

        return this.getPlugin().getPlatformFactory().transformComponent(
                new TextComponentString(itemStack.getDisplayName()).setStyle(
                        new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM,
                                new TextComponentString(tag.toString())))));
    }

    @Override
    protected @NonNull ItemStack transform(
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack itemStack) {
        if (itemStack instanceof AbstractItemStack) {
            //noinspection unchecked
            return ((AbstractItemStack<ItemStack>) itemStack).getItemStack();
        }

        throw new AssertionError();
    }

    @Override
    protected net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack transform(
            @NonNull final SimpleItemStack itemStack) {
        final ItemStack stack = this.parseForItemStack(itemStack.getMaterial())
                .createItemStack(itemStack.getStackSize());
        final NBTTagCompound tag = this.parseTag(itemStack.getMaterial());

        if (tag != null) {
            stack.setTagCompound(tag);
        }

        itemStack.getDisplayName()
                .ifPresent(displayName -> this.setDisplayName(stack, displayName));
        this.setLore(stack, itemStack.getLore());

        return this.wrap(stack);
    }

    @Override
    protected void setDisplayName(@NonNull final ItemStack itemStack,
            @NonNull final Component displayName) {
        itemStack.setStackDisplayName(
                this.getPlugin().getPlatformFactory().componentToJson(displayName));
    }

    @Override
    protected int getStackSize(@NonNull final ItemStack itemStack) {
        return itemStack.getCount();
    }

    @Override
    protected void setStackSize(@NonNull final ItemStack itemStack, final int stackSize) {
        itemStack.setCount(stackSize);
    }

    @Override
    protected @NonNull String getIdentifier(@NonNull final ItemStack itemStack) {
        final StringBuilder builder =
                new StringBuilder(itemStack.getItem().delegate.name().toString());
        final int metadata = itemStack.getMetadata();

        if (metadata > 0) {
            builder.append(ForgeUtils.IDENTIFIER_SEPARATOR)
                    .append(metadata);
        }

        return builder.toString();
    }

    @Override
    protected @NonNull String getMod(@NonNull final ItemStack itemStack) {
        return itemStack.getItem().delegate.name().getNamespace();
    }

    @Override
    protected void appendLore(@NonNull final ItemStack itemStack,
            @NonNull final Collection<Component> loreCollection) {

        final NBTTagCompound display = itemStack.getOrCreateSubCompound("display");
        final NBTTagList tag = display.getTagList("Lore", 8);
        final ForgePlatformFactory platformFactory = this.getPlugin().getPlatformFactory();

        loreCollection.forEach(
                lore -> tag.appendTag(new NBTTagString(platformFactory.componentToJson(lore))));
        display.setTag("Lore", tag);
    }

    @Override
    protected void setLore(@NonNull final ItemStack itemStack,
            @NonNull final List<Component> loreList) {
        if (loreList.isEmpty()) {
            return;
        }

        final NBTTagList tag = new NBTTagList();
        final ForgePlatformFactory platformFactory = this.getPlugin().getPlatformFactory();

        loreList.forEach(
                lore -> tag.appendTag(new NBTTagString(platformFactory.componentToJson(lore))));
        itemStack.getOrCreateSubCompound("display").setTag("Lore", tag);
    }

    @Override
    protected boolean hasPersistentData(@NonNull final ItemStack itemStack) {
        final NBTTagCompound tag = itemStack.getTagCompound();
        return tag != null && !tag.isEmpty();
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final ItemStack itemStack) {
        final NBTTagCompound tag = itemStack.getTagCompound();
        return tag == null ? null : tag.toString();
    }

    @Override
    protected boolean isSameItemSamePersistentData(@NonNull final ItemStack itemStack,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack other) {
        final ItemStack otherItemStack = this.transform(other);
        return itemStack.isItemEqual(otherItemStack) && ItemStack.areItemStackTagsEqual(itemStack,
                otherItemStack);
    }

    @Override
    protected boolean persistentDataPartiallyMatches(@NonNull final ItemStack itemStack,
            @NonNull final String s) {
        final NBTTagCompound otherTag;

        try {
            otherTag = JsonToNBT.getTagFromJson(s);
        } catch (final Exception ignored) {
            // tag could not be parsed
            this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
            return false;
        }

        if (otherTag.isEmpty()) {
            // contains all tags, since there are none
            return true;
        }

        final NBTTagCompound ownTag = itemStack.getTagCompound();

        if (ownTag == null) {
            return false;
        }

        for (final String key : otherTag.getKeySet()) {
            final NBTBase t1 = ownTag.getTag(key);
            final NBTBase t2 = otherTag.getTag(key);

            if (!Objects.equals(t1, t2)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final ItemStack itemStack,
            @Nullable final String s) {
        NBTTagCompound otherTag = null;

        if (s != null) {
            try {
                otherTag = JsonToNBT.getTagFromJson(s);
            } catch (final Exception ignored) {
                // tag could not be parsed
                this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
                return false;
            }
        }

        final NBTTagCompound ownTag = itemStack.getTagCompound();

        if (ownTag == null) {
            // can only be equal when own tag is null or empty
            return otherTag == null || otherTag.isEmpty();
        }

        if (otherTag == null) {
            // can only be equal when own tag is empty
            return ownTag.isEmpty();
        }

        return otherTag.equals(ownTag);
    }

    @Override
    protected boolean isBlock(@NonNull final ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock;
    }

    @Override
    protected boolean isEmpty(@NonNull final ItemStack itemStack) {
        // In 1.7.10, items are empty if they are null. This item is never null.
        return false;
    }

    @Override
    protected Optional<net.dirtcraft.dirtcore.common.model.minecraft.Block> getAsBlock(
            @NonNull final ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            final ItemBlock blockItem = (ItemBlock) itemStack.getItem();
            return Optional.of(this.getPlugin().getPlatformFactory().wrapBlock(
                    ForgeBlock.of(blockItem.getBlock(),
                            blockItem.getMetadata(itemStack.getMetadata()))));
        }

        return Optional.empty();
    }

    @Override
    protected @NonNull ItemStack copy(@NonNull final ItemStack itemStack) {
        return itemStack.copy();
    }

    @NotNull
    protected ItemStackContainer parseForItemStack(final @NonNull Material material) {
        switch (material) {
            case CHANNEL:
                return new ItemStackContainer$Item(Items.SIGN);
            case DISABLED:
                return new ItemStackContainer$Block(Blocks.WOOL, 14);
            case ENABLED:
                return new ItemStackContainer$Block(Blocks.WOOL, 5);
            case KIT_CLAIM:
            case REWARD_INDICATOR:
                return new ItemStackContainer$Item(Items.NETHER_STAR);
            case MENU_EMPTY:
                return new ItemStackContainer$Block(Blocks.STAINED_GLASS_PANE, 7);
            case PAGE_CURRENT:
            case READ:
                return new ItemStackContainer$Item(Items.BOOK);
            case PAGE_NEXT:
            case PAGE_PREVIOUS:
                return new ItemStackContainer$Item(Items.SKULL, 3);
            case WRITE:
                return new ItemStackContainer$Item(Items.DYE);
            default:
                throw new AssertionError();
        }
    }

    @Nullable
    protected NBTTagCompound parseTag(final @NonNull Material material) {
        final String tagString;

        switch (material) {
            case PAGE_NEXT:
                tagString = "{SkullOwner:\"MHF_ArrowRight\"}";
                break;
            case PAGE_PREVIOUS:
                tagString = "{SkullOwner:\"MHF_ArrowLeft\"}";
                break;
            default:
                tagString = null;
                break;
        }

        if (tagString != null) {
            try {
                return JsonToNBT.getTagFromJson(tagString);
            } catch (final NBTException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    protected abstract static class ItemStackContainer {

        protected final int metadata;

        private ItemStackContainer() {
            this(0);
        }

        private ItemStackContainer(final int metadata) {
            this.metadata = metadata;
        }

        @NonNull
        public abstract ItemStack createItemStack(int stackSize);
    }

    private class ItemStackContainer$Block extends ItemStackContainer {

        private final net.minecraft.block.@NonNull Block block;

        private ItemStackContainer$Block(final net.minecraft.block.@NonNull Block block) {
            this.block = block;
        }

        private ItemStackContainer$Block(final net.minecraft.block.@NonNull Block block,
                final int metadata) {
            super(metadata);
            this.block = block;
        }

        @Override
        public @NonNull ItemStack createItemStack(final int stackSize) {
            return new ItemStack(this.block, stackSize, this.metadata);
        }
    }

    private class ItemStackContainer$Item extends ItemStackContainer {

        private final net.minecraft.item.@NonNull Item item;

        private ItemStackContainer$Item(final net.minecraft.item.@NonNull Item item) {
            this.item = item;
        }

        private ItemStackContainer$Item(final net.minecraft.item.@NonNull Item item,
                final int metadata) {
            super(metadata);
            this.item = item;
        }

        @Override
        public @NonNull ItemStack createItemStack(final int stackSize) {
            return new ItemStack(this.item, stackSize, this.metadata);
        }
    }
}
