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

package net.dirtcraft.dirtcore.common.scheduler;

import java.util.Comparator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A scheduler for running tasks using the systems provided by the platform.
 */
public interface SchedulerAdapter {

    /**
     * Executes a task on the main thread of the server.
     * <p>
     * If the current thread is not the main thread,
     * add the task to a queue to be executed the next tick.
     *
     * @param simpleResultTask the task
     */
    void executeSync(@NonNull final SimpleResultTask<?> simpleResultTask);

    /**
     * Executes a task on the main thread of the server.
     * <p>
     * If the current thread is not the main thread,
     * add the task to a queue to be executed the next tick.
     * The current thread will wait until the task has been executed.
     *
     * @param <T>              the type
     * @param simpleResultTask the result task
     * @return the result
     */
    <T> T executeSyncBlocking(@NonNull final SimpleResultTask<T> simpleResultTask);

    /**
     * Executes a task on the main thread of the server.
     * <p>
     * If the current thread is not the main thread,
     * add the task to a queue to be executed the next tick.
     *
     * @param resultVoidTask the task
     */
    void executeSync(@NonNull final ResultVoidTask resultVoidTask);

    /**
     * Executes a task on the main thread of the server.
     * <p>
     * If the current thread is not the main thread,
     * add the task to a queue to be executed the next tick.
     * The current thread will wait until the task has been executed.
     *
     * @param resultVoidTask the task
     */
    void executeSyncBlocking(@NonNull final ResultVoidTask resultVoidTask);

    /**
     * Schedules a task to be executed on the main thread of the server the next tick.
     *
     * @param resultVoidTask the task
     */
    void scheduleSyncTask(@NonNull ResultVoidTask resultVoidTask);

    /**
     * Schedules a task to be executed on the main thread of the server after a delay.
     *
     * @param resultVoidTask the result task
     * @param delay          the delay
     * @param timeUnit       the time unit
     */
    void scheduleSyncDelayedTask(@NonNull ResultVoidTask resultVoidTask, long delay,
            @NonNull TimeUnit timeUnit);

    /**
     * Schedules a task to be executed on the main thread of the server
     * every tick until completion.
     *
     * @param repeatingVoidTask the repeating task
     */
    void scheduleSyncRepeatingTask(@NonNull RepeatingVoidTask repeatingVoidTask);

    /**
     * Schedules a task to be executed on the main thread of the server
     * every tick until completion, after a delay.
     *
     * @param repeatingVoidTask the repeating task
     * @param delay             the delay
     * @param timeUnit          the time unit
     */
    void scheduleSyncRepeatingDelayedTask(@NonNull RepeatingVoidTask repeatingVoidTask, long delay,
            @NonNull TimeUnit timeUnit);

    /**
     * Executes remaining sync tasks.
     */
    void executeRemainingSyncTasks();

    /**
     * Gets an async executor instance.
     *
     * @return an async executor instance
     */
    Executor async();

    /**
     * Executes the given task with a delay.
     *
     * @param task  the task
     * @param delay the delay
     * @param unit  the unit of delay
     * @return the resultant task instance
     */
    SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit);

    /**
     * Executes the given task repeatedly at a given interval.
     *
     * @param task     the task
     * @param interval the interval
     * @param unit     the unit of interval
     * @return the resultant task instance
     */
    SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit);

    /**
     * Shuts down the scheduler instance.
     *
     * <p>{@link #asyncLater(Runnable, long, TimeUnit)} and
     * {@link #asyncRepeating(Runnable, long, TimeUnit)}.</p>
     */
    void shutdownScheduler();

    /**
     * Shuts down the executor instance.
     *
     * <p>{@link #async()} and {@link #executeAsync(Runnable)}.</p>
     */
    void shutdownExecutor();

    /**
     * Executes a task async
     *
     * @param task the task
     */
    default void executeAsync(final Runnable task) {
        this.async().execute(task);
    }

    class Task<T> {
        
        public static final Comparator<Task<?>> COMPARATOR =
                Comparator.comparing(task -> task.scheduledTime);

        @NonNull
        private final ResultTask<T> resultTask;
        private final long scheduledTime;
        private boolean completed = false;

        public Task(@NonNull final ResultTask<T> resultTask, final long scheduledTime) {
            this.resultTask = resultTask;
            this.scheduledTime = scheduledTime;
        }

        public boolean shouldRun() {
            return this.scheduledTime == 0 || System.currentTimeMillis() >= this.scheduledTime;
        }

        public T run() {
            return this.resultTask.execute(this);
        }

        public boolean isCompleted() {
            return this.completed;
        }

        public void complete() {
            this.completed = true;
        }
    }

    class BlockingTask<T> extends Task<T> {

        @NonNull
        private final DirtCoreBootstrap bootstrap;
        @NonNull
        private final CountDownLatch latch;
        @Nullable
        private RuntimeException exception = null;
        @Nullable
        private T result = null;

        public BlockingTask(@NonNull final DirtCoreBootstrap bootstrap,
                @NonNull final ResultTask<T> resultTask, final long scheduledTime,
                @NonNull final CountDownLatch latch) {
            super(resultTask, scheduledTime);
            this.bootstrap = bootstrap;
            this.latch = latch;
        }

        @Override
        public T run() {
            try {
                this.result = super.run();
            } catch (final Throwable e) {
                if (e instanceof RuntimeException) {
                    this.exception = (RuntimeException) e;
                }

                this.bootstrap.getLogger()
                        .severe("Caught exception during awaiting of task completion.", e);
            } finally {
                this.latch.countDown();
            }

            return this.result;
        }

        public void propagateException() {
            if (this.exception != null) {
                throw this.exception;
            }
        }

        @Nullable
        public T getResult() {
            return this.result;
        }
    }

    @FunctionalInterface
    interface ResultTask<R> {

        /**
         * Executes a task.
         *
         * @param task the executing task
         * @return the result
         */
        R execute(@NonNull Task<R> task);
    }

    @FunctionalInterface
    interface SimpleResultTask<R> extends ResultTask<R> {

        /**
         * Executes a task.
         */
        R execute();

        @Override
        default R execute(@NonNull final Task<R> task) {
            final R result = this.execute();
            task.complete();
            return result;
        }
    }

    @FunctionalInterface
    interface ResultVoidTask extends ResultTask<Void> {

        /**
         * Executes a task. Does not return a result.
         */
        void executeNoResult();

        @Override
        default Void execute(@NonNull final Task<Void> task) {
            this.executeNoResult();
            task.complete();
            return null;
        }
    }

    @FunctionalInterface
    interface RepeatingVoidTask extends ResultTask<Void> {

        /**
         * Executes a task.
         *
         * @return true, if the task has been completed
         */
        boolean executeRepeating();

        @Override
        default Void execute(@NonNull final Task<Void> task) {
            // execute task until completed
            if (this.executeRepeating()) {
                task.complete();
            }

            return null;
        }
    }
}
