/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform;

import com.mojang.authlib.properties.PropertyMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.item.Material;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.AbstractItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.ItemStackFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeBlock;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class NeoForgeItemStackFactory extends ItemStackFactory<DirtCoreNeoForgePlugin, ItemStack> {

    public NeoForgeItemStackFactory(final DirtCoreNeoForgePlugin plugin) {
        super(plugin);
    }

    @Override
    public @NonNull Optional<ItemStack> createItemStack(@NonNull final String identifier,
            final int count, @Nullable final String persistentData) {
        final ResourceLocation location = ResourceLocation.parse(identifier);
        final Item item = BuiltInRegistries.ITEM.get(location);
        final ItemStack itemStack = new ItemStack(item, count);

        if (persistentData != null) {
            this.applyPersistentData(itemStack, persistentData);
        }

        return Optional.of(itemStack);
    }

    @Override
    protected @NonNull Component getDisplayName(@NonNull final ItemStack itemStack) {
        return this.getPlugin().getPlatformFactory().transformComponent(itemStack.getDisplayName(),
                NeoForgeUtils.holderLookupProvider(this.getPlugin()));
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

        this.applyMaterial(stack, itemStack.getMaterial());
        itemStack.getDisplayName()
                .ifPresent(displayName -> this.setDisplayName(stack, displayName));
        this.setLore(stack, itemStack.getLore());

        return this.wrap(stack);
    }

    @Override
    protected void setDisplayName(@NonNull final ItemStack itemStack,
            @NonNull final Component displayName) {
        itemStack.set(DataComponents.CUSTOM_NAME,
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
        final ItemLore itemLore = itemStack.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        final NeoForgePlatformFactory platformFactory = this.getPlugin().getPlatformFactory();
        final List<net.minecraft.network.chat.Component> lines = new ArrayList<>(itemLore.lines());

        loreCollection.stream().map(platformFactory::transformComponent).forEach(lines::add);
        itemStack.set(DataComponents.LORE, new ItemLore(lines));
    }

    @Override
    protected void setLore(@NonNull final ItemStack itemStack,
            @NonNull final List<Component> loreList) {
        if (loreList.isEmpty()) {
            return;
        }

        final NeoForgePlatformFactory platformFactory = this.getPlugin().getPlatformFactory();
        final ItemLore itemlore = new ItemLore(
                loreList.stream().map(platformFactory::transformComponent)
                        .collect(Collectors.toList()));

        itemStack.set(DataComponents.LORE, itemlore);
    }

    @Override
    protected boolean hasPersistentData(@NonNull final ItemStack itemStack) {
        return !itemStack.isComponentsPatchEmpty();
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return null;
        }

        return NeoForgeUtils.patchedDataToString(this.getPlugin(), itemStack.getComponentsPatch());
    }

    @Override
    protected boolean isSameItemSamePersistentData(@NonNull final ItemStack itemStack,
            final net.dirtcraft.dirtcore.common.model.minecraft.item.@NonNull ItemStack other) {
        final ItemStack otherItemStack = this.transform(other);
        return ItemStack.isSameItemSameComponents(itemStack, otherItemStack);
    }

    @Override
    protected boolean persistentDataPartiallyMatches(@NonNull final ItemStack itemStack,
            @NonNull final String s) {
        final DataComponentPatch otherComponentPatch =
                NeoForgeUtils.stringToDataComponentPatch(this.getPlugin(),
                        NeoForgeUtils.holderLookupProvider(this.getPlugin()), s);

        if (otherComponentPatch == null) {
            // there was an error
            return false;
        }

        if (otherComponentPatch.isEmpty()) {
            // contains all tags, since there are none
            return true;
        }

        final DataComponentPatch ownComponents = itemStack.getComponentsPatch();

        if (ownComponents.isEmpty()) {
            return false;
        }

        for (final Map.Entry<DataComponentType<?>, Optional<?>> entry :
                otherComponentPatch.entrySet()) {
            if (!Objects.equals(ownComponents.get(entry.getKey()), entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final ItemStack itemStack,
            @Nullable final String s) {
        DataComponentPatch otherComponents = s == null ? DataComponentPatch.EMPTY
                : NeoForgeUtils.stringToDataComponentPatch(this.getPlugin(),
                        NeoForgeUtils.holderLookupProvider(this.getPlugin()), s);

        if (otherComponents == null) {
            otherComponents = DataComponentPatch.EMPTY;
        }

        return Objects.equals(itemStack.getComponentsPatch(), otherComponents);
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
        if (itemStack.getItem() instanceof final BlockItem blockItem) {
            final net.minecraft.world.level.block.Block block = blockItem.getBlock();

            return Optional.of(
                    this.getPlugin().getPlatformFactory().wrapBlock(NeoForgeBlock.of(block)));
        }

        return Optional.empty();
    }

    @Override
    protected @NonNull ItemStack copy(@NonNull final ItemStack itemStack) {
        return itemStack.copy();
    }

    @NotNull
    protected Item parseItem(final @NonNull Material material) {
        return switch (material) {
            case CHANNEL -> Items.OAK_SIGN;
            case DISABLED -> Items.RED_WOOL;
            case ENABLED -> Items.LIME_WOOL;
            case KIT_CLAIM -> Items.NETHER_STAR;
            case MENU_EMPTY -> Items.GRAY_STAINED_GLASS_PANE;
            case PAGE_CURRENT, READ -> Items.BOOK;
            case PAGE_NEXT, PAGE_PREVIOUS -> Items.PLAYER_HEAD;
            case REWARD_INDICATOR -> Items.CHAIN;
            case WRITE -> Items.INK_SAC;
        };
    }

    protected void applyMaterial(@NonNull final ItemStack itemStack,
            @NonNull final Material material) {
        switch (material) {
            case PAGE_NEXT -> itemStack.set(DataComponents.PROFILE,
                    this.createResolvableProfileForSkull("MHF_ArrowRight"));
            case PAGE_PREVIOUS -> itemStack.set(DataComponents.PROFILE,
                    this.createResolvableProfileForSkull("MHF_ArrowLeft"));
        }
    }

    private void applyPersistentData(@NonNull final ItemStack itemStack,
            @NonNull final String persistentData) {
        final DataComponentPatch dataComponentPatch =
                NeoForgeUtils.stringToDataComponentPatch(this.getPlugin(),
                        NeoForgeUtils.holderLookupProvider(this.getPlugin()), persistentData);

        if (dataComponentPatch != null) {
            itemStack.applyComponents(dataComponentPatch);
        }
    }

    @NonNull
    private ResolvableProfile createResolvableProfileForSkull(@NonNull final String name) {
        return new ResolvableProfile(Optional.of(name), Optional.empty(), new PropertyMap());
    }
}
