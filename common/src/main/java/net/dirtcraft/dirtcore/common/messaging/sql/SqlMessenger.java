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

package net.dirtcraft.dirtcore.common.messaging.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import net.dirtcraft.dirtcore.api.messenger.IncomingMessageConsumer;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.scheduler.SchedulerAdapter;
import net.dirtcraft.dirtcore.common.scheduler.SchedulerTask;
import net.dirtcraft.dirtcore.common.storage.connection.MariaDbConnectionFactory;
import net.dirtcraft.storageutils.sql.SqlStorage;

public class SqlMessenger extends AbstractSqlMessenger {

    private final DirtCorePlugin plugin;
    private final SqlStorage sqlStorage;

    private SchedulerTask pollTask;
    private SchedulerTask housekeepingTask;

    public SqlMessenger(final DirtCorePlugin plugin, final IncomingMessageConsumer consumer) {
        super(consumer);
        this.plugin = plugin;
        this.sqlStorage = new SqlStorage(plugin.getLogger(), new MariaDbConnectionFactory(plugin,
                plugin.getConfiguration().get(ConfigKeys.DATABASE_VALUES)),
                plugin.getConfiguration().get(ConfigKeys.SQL_TABLE_PREFIX));
    }

    @Override
    protected Connection getConnection() throws SQLException {
        return this.sqlStorage.getConnectionFactory().getConnection();
    }

    @Override
    protected String getTableName() {
        return this.sqlStorage.getStatementProcessor().apply("{prefix}messenger");
    }

    @Override
    public void init() {
        try {
            this.sqlStorage.init();
            super.init();
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

        // schedule poll tasks
        final SchedulerAdapter scheduler = this.plugin.getBootstrap().getScheduler();
        this.pollTask = scheduler.asyncRepeating(this::pollMessages, 1, TimeUnit.SECONDS);
        this.housekeepingTask =
                scheduler.asyncRepeating(this::runHousekeeping, 30, TimeUnit.SECONDS);
    }

    @Override
    public void close() {
        this.sqlStorage.shutdown();

        SchedulerTask task = this.pollTask;
        if (task != null) {
            task.cancel();
        }
        task = this.housekeepingTask;
        if (task != null) {
            task.cancel();
        }

        this.pollTask = null;
        this.housekeepingTask = null;

        super.close();
    }
}
