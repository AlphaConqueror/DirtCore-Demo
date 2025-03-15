/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.item;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleItemStack implements ItemLike {

    @NonNull
    private final Material material;
    private final int stackSize;
    @Nullable
    private final Component displayName;
    @NonNull
    private final List<Component> lore;

    private SimpleItemStack(@NonNull final Material material, final int stackSize,
            @Nullable final Component displayName, @NonNull final List<Component> lore) {
        this.material = material;
        this.stackSize = stackSize;
        this.displayName = displayName;
        this.lore = lore;
    }

    @NonNull
    public Material getMaterial() {
        return this.material;
    }

    public int getStackSize() {
        return this.stackSize;
    }

    public @NonNull Optional<Component> getDisplayName() {
        return Optional.ofNullable(this.displayName);
    }

    public @NonNull List<Component> getLore() {
        return this.lore;
    }

    @Override
    public @NonNull ItemStack build(@NonNull final DirtCorePlugin plugin) {
        return plugin.getPlatformFactory().transformItemStack(this);
    }

    @Override
    public boolean isEmpty() {
        // change if AIR material is ever introduced
        return false;
    }

    public static class Builder implements ItemStack.Builder {

        @NonNull
        private final Material material;
        private final int stackSize;
        private final ImmutableList.@NonNull Builder<Component> loreBuilder =
                new ImmutableList.Builder<>();
        @Nullable
        private Component displayName = null;

        public Builder(@NonNull final Material material) {
            this(material, 1);
        }

        public Builder(@NonNull final Material material, final int stackSize) {
            this.material = material;
            this.stackSize = stackSize;
        }

        @Override
        public @NonNull ItemLike build() {
            return new SimpleItemStack(this.material, this.stackSize, this.displayName,
                    this.loreBuilder.build());
        }

        @Override
        public ItemStack.@NonNull Builder displayName(@NonNull final Component displayName) {
            this.displayName = displayName;
            return this;
        }

        @Override
        public ItemStack.@NonNull Builder appendLore(@NonNull final Component lore) {
            this.loreBuilder.add(lore);
            return this;
        }
    }
}
