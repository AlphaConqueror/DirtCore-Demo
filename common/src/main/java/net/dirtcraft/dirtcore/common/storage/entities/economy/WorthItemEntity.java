/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.economy;

import java.util.Comparator;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.util.ItemEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "worth_items")
public class WorthItemEntity extends ItemEntity {

    public static final Comparator<WorthItemEntity> PERSISTENT_DATA_COMPARATOR = (o1, o2) -> {
        // sort to consider worths without persistent data first
        if (o1.getPersistentData() != null) {
            return 1;
        }

        if (o2.getPersistentData() != null) {
            return -1;
        }

        return 0;
    };

    @Column(nullable = false)
    @Getter
    @Setter
    protected double worth;

    @Column(nullable = false)
    protected String server;

    protected WorthItemEntity() {}

    public WorthItemEntity(@NonNull final DirtCorePlugin plugin, @NonNull final String identifier,
            @Nullable final String persistentData, final double worth) {
        this.identifier = identifier;
        this.persistentData = persistentData;
        this.worth = worth;
        this.server = plugin.getServerIdentifier();
    }

    public WorthItemEntity(@NonNull final DirtCorePlugin plugin,
            @NonNull final ItemInfoProvider itemInfoProvider, final double worth) {
        this(plugin, itemInfoProvider.getIdentifier(), itemInfoProvider.getPersistentDataAsString(),
                worth);
    }
}
