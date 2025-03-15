/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.crate;

import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crate_keys")
public class CrateKeyItemEntity implements Comparable<CrateKeyItemEntity>, DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected CrateEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    @Setter
    protected String identifier;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String persistentData;

    protected CrateKeyItemEntity() {}

    public CrateKeyItemEntity(@NonNull final CrateEntity original,
            @NonNull final ItemStack itemStack) {
        this.original = original;
        this.update(itemStack);
    }

    public void update(@NonNull final ItemStack itemStack) {
        this.identifier = itemStack.getIdentifier();
        this.persistentData = itemStack.getPersistentDataAsString();
    }

    @NonNull
    public Optional<ItemStack> asItemStack(@NonNull final DirtCorePlugin plugin) {
        return plugin.getPlatformFactory().createItemStack(this.identifier, 1, this.persistentData);
    }

    @Override
    public int compareTo(@NotNull final CrateKeyItemEntity other) {
        return Long.compare(this.id, other.id);
    }
}
