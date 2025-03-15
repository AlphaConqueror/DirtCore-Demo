/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage;

import java.io.InputStream;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.connection.DirtCoreHibernateConnectionFactory;
import net.dirtcraft.dirtcore.common.storage.context.DirtCoreTaskContext;
import net.dirtcraft.storageutils.hibernate.AbstractHibernateStorage;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;

public class DirtCoreHibernateStorage extends AbstractHibernateStorage<DirtCoreTaskContext> {

    private final DirtCorePlugin plugin;

    public DirtCoreHibernateStorage(final DirtCorePlugin plugin,
            final DirtCoreHibernateConnectionFactory connectionFactory) {
        super(plugin.getLogger(), connectionFactory);
        this.plugin = plugin;
    }

    @Override
    protected int getRetriesUponConnectionLoss() {
        return this.plugin.getConfiguration()
                .get(ConfigKeys.CONNECTION_RETRIES_UPON_CONNECTION_LOSS);
    }

    @Override
    protected int getRetriesUponException() {
        return this.plugin.getConfiguration().get(ConfigKeys.CONNECTION_RETRIES_UPON_DEADLOCK);
    }

    @Override
    protected @NonNull DirtCoreTaskContext createTaskContext(@NonNull final Session session) {
        return new DirtCoreTaskContext(this.plugin, session);
    }

    @Override
    protected @Nullable InputStream getSchema() {
        return this.plugin.getBootstrap().getResourceStream("schemas/mysql.sql");
    }
}
