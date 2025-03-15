/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit;

import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import net.dirtcraft.dirtcore.bukkit.util.NullSafeConsoleCommandSender;
import net.dirtcraft.dirtcore.common.loader.LoaderBootstrap;
import net.dirtcraft.dirtcore.common.logging.JavaPluginLogger;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.BootstrappedWithLoader;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import net.dirtcraft.dirtcore.common.plugin.classpath.ClassPathAppender;
import net.dirtcraft.dirtcore.common.plugin.classpath.JarInJarClassPathAppender;
import net.dirtcraft.dirtcore.common.scheduler.SchedulerAdapter;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreBukkitBootstrap implements DirtCoreBootstrap, LoaderBootstrap,
        BootstrappedWithLoader {

    /**
     * The plugin loader
     */
    private final JavaPlugin loader;
    /**
     * The plugin logger
     */
    private final Logger logger;

    /**
     * A scheduler adapter for the platform
     */
    private final SchedulerAdapter schedulerAdapter;

    /**
     * The plugin class path appender
     */
    private final ClassPathAppender classPathAppender;

    /**
     * A null-safe console instance which delegates to the server logger
     * if {@link Server#getConsoleSender()} returns null.
     */
    private final ConsoleCommandSender console;

    @NonNull
    private final DirtCoreBukkitPlugin plugin;
    // load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    private boolean serverStarting = true;
    private boolean serverStopping = false;
    /**
     * The time when the plugin was enabled
     */
    private Instant startTime;

    public DirtCoreBukkitBootstrap(final JavaPlugin loader) {
        this.loader = loader;
        this.logger = new JavaPluginLogger(loader.getLogger());
        this.schedulerAdapter = new BukkitSchedulerAdapter(this);
        this.classPathAppender = new JarInJarClassPathAppender(this.getClass().getClassLoader());
        this.console = new NullSafeConsoleCommandSender(this.getServer());
        this.plugin = new DirtCoreBukkitPlugin(this);
    }

    @Override
    public JavaPlugin getLoader() {
        return this.loader;
    }

    public Server getServer() {
        return this.loader.getServer();
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
    }

    public ConsoleCommandSender getConsole() {
        return this.console;
    }

    @Override
    public void onLoad() {
        this.startTime = Instant.now();

        try {
            this.plugin.load();
        } finally {
            this.loadLatch.countDown();
        }

        this.plugin.registerEarlyListeners();
    }

    @Override
    public void onEnable() {
        this.serverStarting = true;
        this.serverStopping = false;
        this.startTime = Instant.now();
        try {
            this.plugin.enable();

            // schedule a task to update the 'serverStarting' flag
            this.getServer().getScheduler().runTask(this.loader, () -> {
                this.serverStarting = false;
                this.plugin.getEventDispatcher().dispatchServerStarted();
            });
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Override
    public void onDisable() {
        this.serverStopping = true;
        this.plugin.getEventDispatcher().dispatchServerStopping();
        this.plugin.disable();
    }

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public String getVersion() {
        return "@version@";
    }

    @Override
    public Instant getStartupTime() {
        return this.startTime;
    }

    @Override
    public String getServerBrand() {
        return this.getServer().getName();
    }

    @Override
    public String getServerVersion() {
        return this.getServer().getVersion() + " - " + this.getServer().getBukkitVersion();
    }

    @Override
    public Path getDataDirectory() {
        return this.loader.getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public Path getConfigDirectory() {
        return this.getDataDirectory().resolve("dirtcore.conf");
    }
}
