/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.kit;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.util.ItemStackEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "kit_items")
public class KitItemEntity extends ItemStackEntity {

    @Getter
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected KitEntity original;

    protected KitItemEntity() {}

    public KitItemEntity(@NonNull final KitEntity original, @NonNull final ItemStack itemStack) {
        super(itemStack, itemStack.getStackSize());
        this.original = original;
    }
}
