/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1;

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
import net.dirtcraft.dirtcore.common.plugin.classpath.JarInJarClassPathAppender;
import net.dirtcraft.dirtcore.common.scheduler.SchedulerAdapter;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeEventBusFacade;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreForgeBootstrap implements DirtCoreBootstrap, LoaderBootstrap<BlockState,
        ItemStack, Level>, BootstrappedWithLoader {

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

    public DirtCoreForgeBootstrap(final Supplier<ModContainer> loader) {
        this.loader = loader;
        this.logger = new Log4jLogger(LogManager.getLogger(DirtCoreForgeBootstrap.ID));
        this.schedulerAdapter = new ForgeSchedulerAdapter(this);
        this.classPathAppender =
                new JarInJarClassPathAppender(this.logger, this.getClass().getClassLoader());
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
        return ModList.get().getModContainerById("forge").map(ModContainer::getModInfo)
                .map(IModInfo::getDisplayName).orElse("null");
    }

    @Override
    public String getServerVersion() {
        final String forgeVersion =
                ModList.get().getModContainerById("forge").map(ModContainer::getModInfo)
                        .map(IModInfo::getVersion).map(ArtifactVersion::toString).orElse("null");

        return this.getServer().map(MinecraftServer::getServerVersion).orElse("null") + "-"
                + forgeVersion;
    }

    @Override
    public Path getDataDirectory() {
        return FMLPaths.CONFIGDIR.get().resolve(DirtCoreForgeBootstrap.ID).toAbsolutePath();
    }

    @Override
    public Path getConfigDirectory() {
        return this.getDataDirectory().resolve("dirtcore.conf");
    }

    public void registerListeners(final Object target) {
        this.forgeEventBus.register(target);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onServerAboutToStart(final ServerAboutToStartEvent event) {
        this.server = event.getServer();

        try {
            this.plugin.enable();
        } finally {
            this.enableLatch.countDown();
        }

        this.serverThread = Thread.currentThread();
        this.plugin.getEventDispatcher().dispatchServerStarting();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStarted(final ServerStartedEvent ignored) {
        this.plugin.getEventDispatcher().dispatchServerStarted();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onServerStopping(final ServerStoppingEvent ignored) {
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

        this.forgeEventBus.register(this);
        this.forgeEventBus.register(this.schedulerAdapter);
        this.plugin.registerEarlyListeners();
    }

    @Override
    public ForgeEventDispatcher getEventDispatcher() {
        return this.plugin.getEventDispatcher();
    }

    public Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(this.server);
    }
}
