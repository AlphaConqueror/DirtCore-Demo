/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.connection.DirtCoreHibernateConnectionFactory;
import net.dirtcraft.dirtcore.common.storage.context.DirtCoreTaskContext;
import net.dirtcraft.storageutils.StorageType;
import net.dirtcraft.storageutils.storage.implementation.HibernateStorageImplementation;
import net.dirtcraft.storageutils.storagefactory.AbstractStorageFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreStorageFactory extends AbstractStorageFactory<DirtCoreStorage> {

    private final DirtCorePlugin plugin;

    public DirtCoreStorageFactory(final DirtCorePlugin plugin) {
        super(plugin.getLogger());
        this.plugin = plugin;
    }

    @Override
    public @NonNull Set<StorageType> getRequiredTypes() {
        return ImmutableSet.of(this.getStorageType());
    }

    @Override
    protected @NonNull StorageType getStorageType() {
        return this.plugin.getConfiguration().get(ConfigKeys.STORAGE_METHOD);
    }

    @Override
    protected @NonNull DirtCoreStorage createStorage(@NonNull final StorageType type) {
        return new DirtCoreStorage(this.plugin, this.createNewImplementation(type));
    }

    @NonNull
    private HibernateStorageImplementation<DirtCoreTaskContext> createNewImplementation(
            final StorageType storageType) {
        return new DirtCoreHibernateStorage(this.plugin,
                new DirtCoreHibernateConnectionFactory(this.plugin.getLogger(), storageType,
                        this.plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)));
    }
}
