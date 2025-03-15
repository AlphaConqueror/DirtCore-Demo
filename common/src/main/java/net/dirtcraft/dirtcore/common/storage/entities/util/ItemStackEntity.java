/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.util;

import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
public abstract class ItemStackEntity extends ItemEntity {

    @Column(name = "stack_size", nullable = false)
    @Getter
    @Setter
    protected int stackSize;

    protected ItemStackEntity() {}

    protected ItemStackEntity(@NonNull final ItemStack itemStack, final int stackSize) {
        super(itemStack);
        this.stackSize = stackSize;
    }

    @Override
    public @NonNull Optional<ItemStack> asItemStack(@NonNull final DirtCorePlugin plugin) {
        return super.asItemStack(plugin, this.stackSize);
    }
}
