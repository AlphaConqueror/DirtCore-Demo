/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.loader.LoaderBootstrap;
import net.dirtcraft.dirtcore.common.logging.Log4jLogger;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.BootstrappedWithLoader;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import net.dirtcraft.dirtcore.common.plugin.classpath.ClassPathAppender;
import net.dirtcraft.dirtcore.common.scheduler.SchedulerAdapter;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeEventBusFacade;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreForgeBootstrap implements DirtCoreBootstrap, LoaderBootstrap<ForgeBlock,
        ItemStack, World>, BootstrappedWithLoader {

    public static final String ID = "@mod_id@";

    /**
     * The plugin loader
     */
    private final Supplier<ModContainer> loader;
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
     * A facade for the forge event bus, compatible with DirtCore's jar-in-jar packaging
     */
    private final ForgeEventBusFacade forgeEventBus;
    private final Path minecraftConfigDirectory;

    @NonNull
    private final DirtCoreForgePlugin plugin;
    // load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);
    /**
     * The time when the plugin was enabled
     */
    private Instant startTime;
    /**
     * The Minecraft server instance
     */
    private MinecraftServer server;
    private Thread serverThread;

    public DirtCoreForgeBootstrap(final Supplier<ModContainer> loader,
            final Path minecraftConfigDirectory) {
        this.loader = loader;
        this.minecraftConfigDirectory = minecraftConfigDirectory;
        this.logger = new Log4jLogger(LogManager.getLogger(DirtCoreForgeBootstrap.ID));
        this.schedulerAdapter = new ForgeSchedulerAdapter(this);
        this.classPathAppender = file -> {};
        this.forgeEventBus = new ForgeEventBusFacade();
        this.plugin = new DirtCoreForgePlugin(this);
    }

    @Override
    public Object getLoader() {
        return this.loader;
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

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public Thread getServerThread() {
        return this.serverThread;
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
        return Optional.ofNullable(Loader.instance().getIndexedModList().get("Forge"))
                .map(ModContainer::getName).orElse("null");
    }

    @Override
    public String getServerVersion() {
        return this.getServer().map(MinecraftServer::getMinecraftVersion).orElse("null") + "-"
                + ForgeVersion.getVersion();
    }

    @Override
    public Path getDataDirectory() {
        return this.minecraftConfigDirectory.resolve(DirtCoreForgeBootstrap.ID).toAbsolutePath();
    }

    @Override
    public Path getConfigDirectory() {
        return this.getDataDirectory().resolve("dirtcore.conf");
    }

    public void registerListeners(final Object target) {
        this.forgeEventBus.register(target);
    }

    public void onServerAboutToStart(final FMLServerAboutToStartEvent event) {
        this.server = event.getServer();

        try {
            this.plugin.enable();
        } finally {
            this.enableLatch.countDown();
        }

        this.serverThread = Thread.currentThread();
        this.plugin.getEventDispatcher().dispatchServerStarting();
        this.plugin.onRegisterCommands();
    }

    public void onServerStarted(final FMLServerStartedEvent ignored) {
        this.plugin.getEventDispatcher().dispatchServerStarted();
    }

    public void onServerStopping(final FMLServerStoppingEvent ignored) {
        this.plugin.getEventDispatcher().dispatchServerStopping();
        this.plugin.disable();
        this.forgeEventBus.unregisterAll();
        this.server = null;
    }

    @Override
    public void onLoad() {
        this.startTime = Instant.now();

        try {
            if (this.plugin.load()) {
                // failure during load
                this.getLogger()
                        .warn("Encountered a severe error during load. " + DirtCorePlugin.MOD_NAME
                                + " will not be enabled.");
                return;
            }
        } finally {
            this.loadLatch.countDown();
        }

        this.forgeEventBus.register(this.schedulerAdapter);
        this.plugin.registerEarlyListeners();
    }

    public ForgeEventDispatcher getEventDispatcher() {
        return this.plugin.getEventDispatcher();
    }

    public Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(this.server);
    }
}
