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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class ItemEntity implements Comparable<ItemEntity>, DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(nullable = false)
    @Getter
    @NonNull
    @Setter
    protected String identifier;

    // FIXME: Change to persistent_data
    @Column(name = "persistentData", columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String persistentData;

    protected ItemEntity() {}

    protected ItemEntity(@NonNull final ItemStack itemStack) {
        this.update(itemStack);
    }

    public void update(@NonNull final ItemStack itemStack) {
        this.identifier = itemStack.getIdentifier();
        this.persistentData = itemStack.getPersistentDataAsString();
    }

    @NonNull
    public Optional<ItemStack> asItemStack(@NonNull final DirtCorePlugin plugin) {
        return this.asItemStack(plugin, 1);
    }

    @Override
    public int compareTo(@NotNull final ItemEntity other) {
        return Long.compare(this.id, other.id);
    }

    @NonNull
    protected Optional<ItemStack> asItemStack(@NonNull final DirtCorePlugin plugin,
            final int stackSize) {
        return plugin.getPlatformFactory()
                .createItemStack(this.identifier, stackSize, this.persistentData);
    }
}
