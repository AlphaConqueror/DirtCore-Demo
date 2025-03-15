/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.platform;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.platform.minecraft.item.ItemStackFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class BukkitItemStackFactory extends ItemStackFactory<DirtCoreBukkitPlugin, ItemStack,
        Material> {

    public BukkitItemStackFactory(final DirtCoreBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NonNull ItemStack transform(
            final net.dirtcraft.dirtcore.common.platform.minecraft.item.@NonNull ItemStack itemStack) {
        final Material material = this.parseMaterial(itemStack.getMaterial());
        final ItemStack stack = new ItemStack(material, itemStack.getStackSize());
        final ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(stack.getType());

        itemStack.getDisplayName().ifPresent(displayName -> itemMeta.setDisplayName(
                this.getPlugin().getPlatformFactory().transformToString(displayName)));
        stack.setLore(itemStack.getLore().stream()
                .map(lore -> this.getPlugin().getPlatformFactory().transformToString(lore))
                .collect(Collectors.toList()));
        stack.setItemMeta(itemMeta);

        return stack;
    }

    @NotNull
    @Override
    protected Material parseMaterial(
            final net.dirtcraft.dirtcore.common.platform.minecraft.item.@NonNull Material material) {
        switch (material) {
            case READ:
                return Material.BOOK;
            case WRITE:
                return Material.INK_SAC;
            case CHANNEL:
                return Material.OAK_SIGN;
            case ENABLED:
                return Material.GREEN_DYE;
            case DISABLED:
                return Material.RED_DYE;
            default:
                throw new AssertionError();
        }
    }

    @Override
    protected @Nullable Component getDisplayName(@NonNull final ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                return Component.text(itemMeta.getDisplayName());
            }
        }

        return null;
    }

    @Override
    protected void setDisplayName(@NonNull final ItemStack itemStack,
            @NonNull final Component displayName) {
        this.getOrSetItemMeta(itemStack).setDisplayName(
                this.getPlugin().getPlatformFactory().transformToString(displayName));
    }

    @Override
    protected int getStackSize(@NonNull final ItemStack itemStack) {
        return itemStack.getAmount();
    }

    @Override
    protected void setStackSize(@NonNull final ItemStack itemStack, final int stackSize) {
        itemStack.setAmount(stackSize);
    }

    @Override
    protected void appendLore(@NonNull final ItemStack itemStack, @NonNull final Component lore) {
        final ItemMeta itemMeta = this.getOrSetItemMeta(itemStack);
        final List<String> itemLore;

        if (itemMeta.hasLore()) {
            itemLore = itemMeta.getLore();
            assert itemLore != null;
        } else {
            itemLore = new ArrayList<>();
            itemMeta.setLore(itemLore);
        }

        itemLore.add(this.getPlugin().getPlatformFactory().transformToString(lore));
    }

    @NonNull
    private ItemMeta getOrSetItemMeta(@NonNull final ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            return itemStack.getItemMeta();
        }

        final ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());

        itemStack.setItemMeta(itemMeta);
        return itemMeta;
    }
}
