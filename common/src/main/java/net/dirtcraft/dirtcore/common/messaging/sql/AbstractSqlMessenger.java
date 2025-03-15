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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.dirtcraft.dirtcore.api.messenger.IncomingMessageConsumer;
import net.dirtcraft.dirtcore.api.messenger.Messenger;
import net.dirtcraft.dirtcore.api.messenger.message.OutgoingMessage;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An implementation of {@link Messenger} using SQL.
 */
public abstract class AbstractSqlMessenger implements Messenger {

    private final IncomingMessageConsumer consumer;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private long lastId = -1;
    private boolean closed = false;

    protected AbstractSqlMessenger(final IncomingMessageConsumer consumer) {
        this.consumer = consumer;
    }

    protected abstract Connection getConnection() throws SQLException;

    protected abstract String getTableName();

    public void init() throws SQLException {
        try (final Connection c = this.getConnection()) {
            // init table
            final String createStatement = "CREATE TABLE IF NOT EXISTS `" + this.getTableName()
                    + "` (`id` INT AUTO_INCREMENT NOT NULL, `time` TIMESTAMP NOT NULL, `msg` TEXT"
                    + " NOT NULL, PRIMARY KEY (`id`)) DEFAULT CHARSET = utf8mb4";
            try (final Statement s = c.createStatement()) {
                try {
                    s.execute(createStatement);
                } catch (final SQLException e) {
                    if (e.getMessage().contains("Unknown character set")) {
                        // try again
                        s.execute(createStatement.replace("utf8mb4", "utf8"));
                    } else {
                        throw e;
                    }
                }
            }

            // pull last id
            try (final PreparedStatement ps = c.prepareStatement(
                    "SELECT MAX(`id`) as `latest` FROM `" + this.getTableName() + "`")) {
                try (final ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        this.lastId = rs.getLong("latest");
                    }
                }
            }
        }
    }

    @Override
    public void sendOutgoingMessage(@NonNull final OutgoingMessage outgoingMessage) {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (final Connection c = this.getConnection()) {
            try (final PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO `" + this.getTableName() + "` (`time`, `msg`) VALUES(NOW(), ?)")) {
                ps.setString(1, outgoingMessage.asEncodedString());
                ps.execute();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    @Override
    public void close() {
        this.lock.writeLock().lock();
        try {
            this.closed = true;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public void pollMessages() {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (final Connection c = this.getConnection()) {
            try (final PreparedStatement ps = c.prepareStatement(
                    "SELECT `id`, `msg` FROM `" + this.getTableName()
                            + "` WHERE `id` > ? AND (NOW() - `time` < 30)")) {
                ps.setLong(1, this.lastId);
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final long id = rs.getLong("id");
                        this.lastId = Math.max(this.lastId, id);

                        final String message = rs.getString("msg");
                        this.consumer.consumeIncomingMessageAsString(message);
                    }
                }
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void runHousekeeping() {
        this.lock.readLock().lock();
        if (this.closed) {
            this.lock.readLock().unlock();
            return;
        }

        try (final Connection c = this.getConnection()) {
            try (final PreparedStatement ps = c.prepareStatement(
                    "DELETE FROM `" + this.getTableName() + "` WHERE (NOW() - `time` > 60)")) {
                ps.execute();
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        } finally {
            this.lock.readLock().unlock();
        }
    }
}
