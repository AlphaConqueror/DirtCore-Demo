/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
