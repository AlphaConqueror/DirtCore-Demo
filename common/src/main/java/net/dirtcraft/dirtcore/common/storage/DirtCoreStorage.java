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

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.DirtCoreTaskContext;
import net.dirtcraft.dirtcore.common.util.Throwing;
import net.dirtcraft.storageutils.storage.HibernateStorage;
import net.dirtcraft.storageutils.storage.implementation.HibernateStorageImplementation;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.CheckReturnValue;

public class DirtCoreStorage extends HibernateStorage<DirtCoreTaskContext> {

    private final DirtCorePlugin plugin;

    public DirtCoreStorage(final DirtCorePlugin plugin,
            final HibernateStorageImplementation<DirtCoreTaskContext> implementation) {
        super(plugin.getLogger(), implementation);
        this.plugin = plugin;
    }

    /**
     * Performs a task on the database asynchronously.
     *
     * @param task the task
     */
    public void performTaskAsync(@NonNull final Task<DirtCoreTaskContext> task) {
        this.plugin.getBootstrap().getScheduler()
                .executeAsync(() -> this.implementation.performTask(task));
    }

    /**
     * Performs a result task on the database asynchronously.
     *
     * @param task the result task
     * @return a completable future of the task
     */
    @CheckReturnValue
    public <R> @NonNull CompletableFuture<R> performTaskAsync(
            @NonNull final ResultTask<DirtCoreTaskContext, R> task) {
        return this.future(() -> this.implementation.performTask(task));
    }

    private <T> CompletableFuture<T> future(final Callable<T> supplier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (final Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }

                throw new CompletionException(e);
            }
        }, this.plugin.getBootstrap().getScheduler().async());
    }

    private CompletableFuture<Void> future(final Throwing.Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (final Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }

                throw new CompletionException(e);
            }
        }, this.plugin.getBootstrap().getScheduler().async());
    }
}
