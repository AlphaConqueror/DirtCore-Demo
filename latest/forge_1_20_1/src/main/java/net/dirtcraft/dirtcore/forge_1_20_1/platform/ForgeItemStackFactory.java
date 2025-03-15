/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.item.Material;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.AbstractItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.ItemStackFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.kyori.adventure.text.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
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
        final ResourceLocation location = new ResourceLocation(identifier);
        final Item item = ForgeRegistries.ITEMS.getValue(location);

        if (item == null) {
            return Optional.empty();
        }

        final ItemStack itemStack = new ItemStack(item, count);

        if (persistentData != null) {
            try {
                itemStack.setTag(TagParser.parseTag(persistentData));
            } catch (final CommandSyntaxException e) {
                this.getPlugin().getLogger()
                        .warn("NBT string '{}' could not be parsed.", persistentData, e);
            }
        }

        return Optional.of(itemStack);
    }

    @Override
    protected @NonNull Component getDisplayName(@NonNull final ItemStack itemStack) {
        return this.getPlugin().getPlatformFactory().transformComponent(itemStack.getDisplayName());
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
        final Item item = this.parseItem(itemStack.getMaterial());
        final ItemStack stack = new ItemStack(item, itemStack.getStackSize());
        final CompoundTag tag = this.parseTag(itemStack.getMaterial());

        if (tag != null) {
            stack.setTag(tag);
        }

        itemStack.getDisplayName()
                .ifPresent(displayName -> this.setDisplayName(stack, displayName));
        this.setLore(stack, itemStack.getLore());

        return this.wrap(stack);
    }

    @Override
    protected void setDisplayName(@NonNull final ItemStack itemStack,
            @NonNull final Component displayName) {
        itemStack.setHoverName(
                this.getPlugin().getPlatformFactory().transformComponent(displayName));
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
        //noinspection deprecation
        return itemStack.getItem().builtInRegistryHolder().key().location().toString();
    }

    @Override
    protected @NonNull String getMod(@NonNull final ItemStack itemStack) {
        //noinspection deprecation
        return itemStack.getItem().builtInRegistryHolder().key().registry().getNamespace();
    }

    @Override
    protected void appendLore(@NonNull final ItemStack itemStack,
            @NonNull final Collection<Component> loreCollection) {
        final CompoundTag display = itemStack.getOrCreateTagElement("display");
        final ListTag tag = display.getList(ItemStack.TAG_LORE, 8);

        loreCollection.forEach(lore -> tag.add(
                StringTag.valueOf(this.getPlugin().getPlatformFactory().componentToJson(lore))));
        display.put(ItemStack.TAG_LORE, tag);
    }

    @Override
    protected void setLore(@NonNull final ItemStack itemStack,
            @NonNull final List<Component> loreList) {
        if (loreList.isEmpty()) {
            return;
        }

        final ListTag tag = new ListTag();
        final ForgePlatformFactory platformFactory = this.getPlugin().getPlatformFactory();

        for (final Component lore : loreList) {
            tag.add(StringTag.valueOf(platformFactory.componentToJson(lore)));
        }

        itemStack.getOrCreateTagElement("display").put(ItemStack.TAG_LORE, tag);
    }

    @Override
    protected boolean hasPersistentData(@NonNull final ItemStack itemStack) {
        return itemStack.hasTag();
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final ItemStack itemStack) {
        final CompoundTag tag = itemStack.getTag();
        return tag == null ? null : tag.toString();
    }

    @Override
    protected boolean isSameItemSamePersistentData(@NonNull final ItemStack itemStack,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack other) {
        final ItemStack otherItemStack = this.transform(other);
        return ItemStack.isSameItemSameTags(itemStack, otherItemStack);
    }

    @Override
    protected boolean persistentDataPartiallyMatches(@NonNull final ItemStack itemStack,
            @NonNull final String s) {
        final CompoundTag otherTag;

        try {
            otherTag = TagParser.parseTag(s);
        } catch (final Exception ignored) {
            // tag could not be parsed
            this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
            return false;
        }

        if (otherTag.isEmpty()) {
            // contains all tags, since there are none
            return true;
        }

        final CompoundTag ownTag = itemStack.getTag();

        if (ownTag == null) {
            return false;
        }

        for (final String key : otherTag.getAllKeys()) {
            final Tag t1 = ownTag.get(key);
            final Tag t2 = otherTag.get(key);

            if (!Objects.equals(t1, t2)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final ItemStack itemStack,
            @Nullable final String s) {
        CompoundTag otherTag = null;

        if (s != null) {
            try {
                otherTag = TagParser.parseTag(s);
            } catch (final Exception ignored) {
                // tag could not be parsed
                this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
                return false;
            }
        }

        final CompoundTag ownTag = itemStack.getTag();

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
        return itemStack.getItem() instanceof BlockItem;
    }

    @Override
    protected boolean isEmpty(@NonNull final ItemStack itemStack) {
        return itemStack.isEmpty();
    }

    @Override
    protected Optional<Block> getAsBlock(@NonNull final ItemStack itemStack) {
        if (itemStack.getItem() instanceof BlockItem) {
            final BlockItem blockItem = (BlockItem) itemStack.getItem();
            final net.minecraft.world.level.block.Block block = blockItem.getBlock();

            return Optional.of(
                    this.getPlugin().getPlatformFactory().wrapBlock(ForgeBlock.of(block)));
        }

        return Optional.empty();
    }

    @Override
    protected @NonNull ItemStack copy(@NonNull final ItemStack itemStack) {
        return itemStack.copy();
    }

    @NotNull
    protected Item parseItem(final @NonNull Material material) {
        switch (material) {
            case CHANNEL:
                return Items.OAK_SIGN;
            case DISABLED:
                return Items.RED_WOOL;
            case ENABLED:
                return Items.LIME_WOOL;
            case KIT_CLAIM:
                return Items.NETHER_STAR;
            case MENU_EMPTY:
                return Items.GRAY_STAINED_GLASS_PANE;
            case PAGE_CURRENT:
            case READ:
                return Items.BOOK;
            case PAGE_NEXT:
            case PAGE_PREVIOUS:
                return Items.PLAYER_HEAD;
            case REWARD_INDICATOR:
                return Items.CHAIN;
            case WRITE:
                return Items.INK_SAC;
            default:
                throw new AssertionError();
        }
    }

    @Nullable
    protected CompoundTag parseTag(final @NonNull Material material) {
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
                return TagParser.parseTag(tagString);
            } catch (final CommandSyntaxException ignored) {}
        }

        return null;
    }
}
