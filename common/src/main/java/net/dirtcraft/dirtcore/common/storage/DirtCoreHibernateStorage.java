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
