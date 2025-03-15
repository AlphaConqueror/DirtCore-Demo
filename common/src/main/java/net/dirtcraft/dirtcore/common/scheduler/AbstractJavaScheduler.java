/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.scheduler;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Abstract implementation of {@link SchedulerAdapter} using a {@link ScheduledExecutorService}.
 */
public abstract class AbstractJavaScheduler implements SchedulerAdapter {

    private static final int PARALLELISM = 16;
    private final List<Task<?>> taskList = new CopyOnWriteArrayList<>();
    private final DirtCoreBootstrap bootstrap;
    private final ScheduledThreadPoolExecutor scheduler;
    private final ForkJoinPool worker;

    public AbstractJavaScheduler(final DirtCoreBootstrap bootstrap) {
        this.bootstrap = bootstrap;
        this.scheduler = new ScheduledThreadPoolExecutor(1, r -> {
            final Thread thread = Executors.defaultThreadFactory().newThread(r);
            thread.setName("dirtcore-scheduler");
            return thread;
        });
        this.scheduler.setRemoveOnCancelPolicy(true);
        this.scheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.worker =
                new ForkJoinPool(PARALLELISM, new WorkerThreadFactory(), new ExceptionHandler(),
                        false);
    }

    @Override
    public void executeSync(@NonNull final SimpleResultTask<?> simpleResultTask) {
        this.executeSync((ResultTask<?>) simpleResultTask);
    }

    @Override
    public <T> T executeSyncBlocking(@NonNull final SimpleResultTask<T> simpleResultTask) {
        return this.executeSyncBlocking((ResultTask<T>) simpleResultTask);
    }

    @Override
    public void executeSync(@NonNull final ResultVoidTask resultVoidTask) {
        this.executeSync((ResultTask<Void>) resultVoidTask);
    }

    @Override
    public void executeSyncBlocking(@NonNull final ResultVoidTask resultVoidTask) {
        this.executeSyncBlocking((ResultTask<Void>) resultVoidTask);
    }

    @Override
    public void scheduleSyncTask(@NonNull final ResultVoidTask resultVoidTask) {
        this.addTask(new Task<>(resultVoidTask, 0));
    }

    @Override
    public void scheduleSyncDelayedTask(@NonNull final ResultVoidTask resultVoidTask,
            final long delay, @NonNull final TimeUnit timeUnit) {
        if (delay <= 0) {
            this.scheduleSyncTask(resultVoidTask);
        } else {
            this.addTask(new Task<>(resultVoidTask,
                    System.currentTimeMillis() + timeUnit.toMillis(delay)));
        }
    }

    @Override
    public void scheduleSyncRepeatingTask(@NonNull final RepeatingVoidTask repeatingVoidTask) {
        this.addTask(new Task<>(repeatingVoidTask, 0));
    }

    @Override
    public void scheduleSyncRepeatingDelayedTask(@NonNull final RepeatingVoidTask repeatingVoidTask,
            final long delay, @NonNull final TimeUnit timeUnit) {
        if (delay <= 0) {
            this.scheduleSyncRepeatingTask(repeatingVoidTask);
        } else {
            this.addTask(new Task<>(repeatingVoidTask,
                    System.currentTimeMillis() + timeUnit.toMillis(delay)));
        }
    }

    @Override
    public void executeRemainingSyncTasks() {
        synchronized (this.taskList) {
            for (final Task<?> task : this.taskList) {
                if (!task.shouldRun()) {
                    continue;
                }

                task.run();

                if (task.isCompleted()) {
                    this.taskList.remove(task);
                }
            }
        }
    }

    @Override
    public Executor async() {
        return this.worker;
    }

    @Override
    public SchedulerTask asyncLater(final Runnable task, final long delay, final TimeUnit unit) {
        final ScheduledFuture<?> future =
                this.scheduler.schedule(() -> this.worker.execute(task), delay, unit);
        return () -> future.cancel(false);
    }

    @Override
    public SchedulerTask asyncRepeating(final Runnable task, final long interval,
            final TimeUnit unit) {
        final ScheduledFuture<?> future =
                this.scheduler.scheduleAtFixedRate(() -> this.worker.execute(task), interval,
                        interval, unit);
        return () -> future.cancel(false);
    }

    @Override
    public void shutdownScheduler() {
        this.scheduler.shutdown();

        try {
            if (!this.scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                this.bootstrap.getLogger()
                        .severe("Timed out waiting for the DirtCore scheduler to terminate");
                this.reportRunningTasks(thread -> thread.getName().equals("dirtcore-scheduler"));
            }
        } catch (final InterruptedException e) {
            this.bootstrap.getLogger()
                    .severe("Caught exception while trying to shut down scheduler.", e);
        }
    }

    @Override
    public void shutdownExecutor() {
        this.worker.shutdown();

        try {
            if (!this.worker.awaitTermination(1, TimeUnit.MINUTES)) {
                this.bootstrap.getLogger()
                        .severe("Timed out waiting for the DirtCore worker thread pool to "
                                + "terminate");
                this.reportRunningTasks(thread -> thread.getName().startsWith("dirtcore-worker-"));
            }
        } catch (final InterruptedException e) {
            this.bootstrap.getLogger()
                    .severe("Caught exception while trying to shut down executor.", e);
        }
    }

    private void executeSync(@NonNull final ResultTask<?> resultTask) {
        if (this.bootstrap.getEnableLatch().getCount() != 0) {
            throw new IllegalStateException("Server not ready.");
        }

        final Task<?> task = new Task<>(resultTask, 0);

        if (Thread.currentThread() == this.bootstrap.getServerThread()) {
            task.run();
        } else {
            this.addTask(task);
        }
    }

    private <T> T executeSyncBlocking(@NonNull final ResultTask<T> resultTask) {
        if (this.bootstrap.getEnableLatch().getCount() != 0) {
            throw new IllegalStateException("Server not ready.");
        }

        if (Thread.currentThread() == this.bootstrap.getServerThread()) {
            final Task<T> task = new Task<>(resultTask, 0);
            return task.run();
        }

        final CountDownLatch latch = new CountDownLatch(1);
        final BlockingTask<T> blockingTask =
                new BlockingTask<>(this.bootstrap, resultTask, 0, latch);

        this.addTask(blockingTask);

        try {
            latch.await();
        } catch (final InterruptedException e) {
            this.bootstrap.getLogger()
                    .severe("Caught exception during awaiting of task completion.", e);
        }

        blockingTask.propagateException();
        return blockingTask.getResult();
    }

    private void addTask(final Task<?> task) {
        synchronized (this.taskList) {
            this.taskList.add(task);
            this.taskList.sort(Task.COMPARATOR);
        }
    }

    private void reportRunningTasks(final Predicate<Thread> predicate) {
        Thread.getAllStackTraces().forEach((thread, stack) -> {
            if (predicate.test(thread)) {
                this.bootstrap.getLogger().warn("Thread " + thread.getName()
                        + " is blocked, and may be the reason for the slow shutdown!\n"
                        + Arrays.stream(stack).map(el -> "  " + el)
                        .collect(Collectors.joining("\n")));
            }
        });
    }

    private static final class WorkerThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {

        private static final AtomicInteger COUNT = new AtomicInteger(0);

        @Override
        public ForkJoinWorkerThread newThread(final ForkJoinPool pool) {
            final ForkJoinWorkerThread thread =
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            thread.setDaemon(true);
            thread.setName("dirtcore-worker-" + COUNT.getAndIncrement());
            return thread;
        }
    }

    private final class ExceptionHandler implements UncaughtExceptionHandler {

        @Override
        public void uncaughtException(final Thread t, final Throwable e) {
            AbstractJavaScheduler.this.bootstrap.getLogger()
                    .warn("Thread " + t.getName() + " threw an uncaught exception", e);
        }
    }
}
