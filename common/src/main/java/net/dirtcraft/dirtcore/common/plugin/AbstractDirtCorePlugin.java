/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.dirtcraft.dirtcore.common.actionlog.LogDispatcher;
import net.dirtcraft.dirtcore.common.api.ApiRegistrationUtil;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.command.CommandManager;
import net.dirtcraft.dirtcore.common.config.ConfigAdapter;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.config.DirtCoreConfiguration;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;
import net.dirtcraft.dirtcore.common.dependencies.Dependency;
import net.dirtcraft.dirtcore.common.dependencies.DependencyManager;
import net.dirtcraft.dirtcore.common.dependencies.DependencyManagerImpl;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.event.gen.GeneratedEventClass;
import net.dirtcraft.dirtcore.common.event.listener.GeneralEventListener;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.messaging.InternalMessagingService;
import net.dirtcraft.dirtcore.common.messaging.MessagingFactory;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.storage.DirtCoreStorage;
import net.dirtcraft.dirtcore.common.storage.DirtCoreStorageFactory;
import net.dirtcraft.dirtcore.common.tasks.SyncTask;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dirtcraft.dirtcore.common.util.filter.JavaFilter;
import net.dirtcraft.dirtcore.common.util.filter.Log4jFilter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.service.spi.ServiceException;

public abstract class AbstractDirtCorePlugin implements DirtCorePlugin {

    private DependencyManager dependencyManager;
    private CommandManager commandManager;
    @Nullable
    private DiscordBotClient discordBotClient;
    private DirtCoreConfiguration configuration;
    private LogDispatcher logDispatcher;
    private DirtCoreStorage storage;
    private InternalMessagingService messagingService = null;
    private SyncTask.Buffer syncTaskBuffer;
    private GeneralEventListener generalEventListener;
    private DirtCoreApiProvider apiProvider;
    private Instant startupTime;

    protected abstract void registerCommands();

    protected abstract void setupSenderFactory();

    protected abstract void setupPlatformFactory();

    protected abstract void registerPlatformListeners();

    protected abstract MessagingFactory<?> provideMessagingFactory();

    protected abstract void setupManagers();

    protected abstract void setupEventDispatcher(@NonNull DirtCoreApiProvider provider);

    protected abstract void loadEntitySelectorOptions();

    /**
     * Loads the plugin.
     *
     * @return true, if the load failed
     */
    public boolean load() {
        this.startupTime = Instant.now();

        this.getLogger().info("Loading {}...", MOD_NAME);

        // load dependencies
        this.dependencyManager = this.createDependencyManager();
        this.dependencyManager.loadDependencies(this.getGlobalDependencies());

        JavaFilter.applyFilter();
        Log4jFilter.applyFilter();

        this.getLogger().info("Loading configuration...");

        try {
            final ConfigurationAdapter configFileAdapter =
                    new ConfigAdapter(this, this.resolveConfig());
            this.configuration = new DirtCoreConfiguration(this.getLogger(), configFileAdapter);
        } catch (final Exception e) {
            this.getLogger().severe("Malformed config. {}", e.getMessage());
            return true;
        }

        if (FormatUtils.isBlank(this.configuration.get(ConfigKeys.SERVER_IDENTIFIER))) {
            this.getLogger().severe("The server identifier can not be blank.");
            return true;
        }

        // now the configuration is loaded,
        // we can create a storage factory and load initial dependencies
        final DirtCoreStorageFactory storageFactory = new DirtCoreStorageFactory(this);
        this.loadStorageDependencies(storageFactory);

        // initialise storage
        try {
            this.storage = storageFactory.getInstance();
        } catch (final Exception e) {
            if (e instanceof ServiceException) {
                this.getLogger().severe("Unable to connect to the database. Make sure the address, "
                        + "port, database and credentials are correct. The connection to the "
                        + "database is essential.");
            } else {
                // in case we messed up somewhere
                this.getLogger().severe("Caught unexpected error.", e);
            }

            return true;
        }

        // load entity selector options
        this.loadEntitySelectorOptions();
        // init command manager
        this.commandManager = new CommandManager(this);

        this.getLogger().info("Loading Discord bot...");

        final String token = this.configuration.get(ConfigKeys.DISCORD_TOKEN);

        if (FormatUtils.isBlank(token)) {
            this.getLogger().warn("Could not load discord bot, token is blank.");
        } else {
            try {
                this.discordBotClient = new DiscordBotClient(this, token);
                this.discordBotClient.enable();
            } catch (final RuntimeException | InterruptedException e) {
                this.getLogger()
                        .warn("Could not start discord bot. Make sure the token is correct.");
            }
        }

        return false;
    }

    public final void enable() {
        this.setupSenderFactory();
        this.setupPlatformFactory();

        // register listeners
        this.registerPlatformListeners();

        this.logDispatcher = new LogDispatcher(this);
        this.messagingService = this.provideMessagingFactory().getInstance();

        // set up the update task buffer
        this.syncTaskBuffer = new SyncTask.Buffer(this);

        // register commands
        this.registerCommands();

        // setup managers
        this.setupManagers();


        // register with the DirtCore API
        this.apiProvider = new DirtCoreApiProvider(this);
        this.apiProvider.ensureApiWasLoadedByPlugin();
        this.setupEventDispatcher(this.apiProvider);
        this.getBootstrap().getScheduler().executeAsync(GeneratedEventClass::preGenerate);
        ApiRegistrationUtil.registerProvider(this, this.apiProvider);

        // run an update instantly
        this.getLogger().info("Performing initial data load...");

        try {
            new SyncTask(this).run();
        } catch (final Exception e) {
            this.getLogger().severe("Caught exception during initial data load.", e);
        }

        if (this.discordBotClient != null) {
            this.discordBotClient.getDiscordManager().registerListeners();
        }

        // create the console user, if it does not exist
        this.createConsoleUser();

        // register common listeners
        this.generalEventListener = new GeneralEventListener(this);
        this.apiProvider.getEventBus().subscribe(this.generalEventListener);

        // perform any platform-specific final setup tasks
        this.performFinalSetup();

        final Duration timeTaken = Duration.between(this.startupTime, Instant.now());
        this.getLogger().info("Successfully enabled. (took " + timeTaken.toMillis() + "ms)");
    }

    public final void disable() {
        this.getLogger().info("Starting shutdown process...");

        // cancel delayed/repeating tasks
        this.getBootstrap().getScheduler().shutdownScheduler();

        if (this.discordBotClient != null) {
            this.getLogger().info("Shutting down Discord bot...");
            this.discordBotClient.disable();
        }

        // close messaging service
        if (this.messagingService != null) {
            this.getLogger().info("Closing messaging service...");
            this.messagingService.close();
        }

        // close storage
        this.getLogger().info("Closing storage...");
        this.storage.shutdown();

        // unregister api
        ApiRegistrationUtil.unregisterProvider(this);
        // shutdown async executor pool
        this.getBootstrap().getScheduler().shutdownExecutor();
        // close isolated loaders for non-relocated dependencies
        this.getDependencyManager().close();
        // close classpath appender
        this.getBootstrap().getClassPathAppender().close();

        this.getLogger().info("Goodbye!");
    }

    public void buildCommands(
            @NonNull final ArgumentFactory<? extends DirtCorePlugin> argumentFactory) {
        this.getLogger().info("Building commands...");
        this.getEntitySelectorOptions().init();
        this.commandManager.buildCommands(argumentFactory);
    }

    @Override
    public @NonNull CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public DependencyManager getDependencyManager() {
        return this.dependencyManager;
    }

    @Override
    public DirtCoreApiProvider getApiProvider() {
        return this.apiProvider;
    }

    @Override
    public Optional<DiscordBotClient> getDiscordBotClient() {
        return Optional.ofNullable(this.discordBotClient);
    }

    @Override
    public @NonNull DirtCoreConfiguration getConfiguration() {
        return this.configuration;
    }

    @Override
    public DirtCoreStorage getStorage() {
        return this.storage;
    }

    @Override
    public Optional<InternalMessagingService> getMessagingService() {
        return Optional.ofNullable(this.messagingService);
    }

    @Override
    public LogDispatcher getLogDispatcher() {
        return this.logDispatcher;
    }

    @Override
    public @NonNull Logger getLogger() {
        return this.getBootstrap().getLogger();
    }

    @Override
    public String getNetworkName() {
        return this.configuration.get(ConfigKeys.NETWORK_NAME);
    }

    @Override
    public String getNetworkIcon() {
        return this.configuration.get(ConfigKeys.NETWORK_ICON);
    }

    @Override
    public String getServerName() {
        return this.configuration.get(ConfigKeys.SERVER_NAME);
    }

    @Override
    public String getServerIdentifier() {
        return this.configuration.get(ConfigKeys.SERVER_IDENTIFIER);
    }

    @Override
    public String getServerIcon() {
        return this.configuration.get(ConfigKeys.SERVER_ICON);
    }

    @Override
    public SyncTask.Buffer getSyncTaskBuffer() {
        return this.syncTaskBuffer;
    }

    @Override
    public GeneralEventListener getGeneralEventListener() {
        return this.generalEventListener;
    }

    protected DependencyManager createDependencyManager() {
        return new DependencyManagerImpl(this);
    }

    protected Set<Dependency> getGlobalDependencies() {
        return EnumSet.of(Dependency.ADVENTURE_API, Dependency.ADVENTURE_KEY,
                Dependency.ADVENTURE_TEXT_MINIMESSAGE, Dependency.ADVENTURE_TEXT_SERIALIZER_GSON,
                Dependency.ADVENTURE_TEXT_SERIALIZER_JSON, Dependency.EXAMINATION_API,
                Dependency.EXAMINATION_STRING, Dependency.OPTION, Dependency.GSON,
                Dependency.CAFFEINE, Dependency.JDA, Dependency.COMMONS_COLLECTIONS4,
                Dependency.OKIO, Dependency.OKIO_JVM, Dependency.OKHTTP, Dependency.KOTLIN,
                Dependency.NEOVISIONARIES, Dependency.TROVE4J, Dependency.JACKSON_CORE,
                Dependency.JACKSON_DATABIND, Dependency.JACKSON_ANNOTATIONS, Dependency.BYTEBUDDY,
                Dependency.EVENT, Dependency.PERSISTENCE_API);
    }

    protected void loadStorageDependencies(@NonNull final DirtCoreStorageFactory storageFactory) {
        this.dependencyManager.loadStorageDependencies(storageFactory.getRequiredTypes());
    }

    protected void createConsoleUser() {
        this.storage.performTask(context -> this.getUserManager()
                .createOrUpdateUser(context, Sender.CONSOLE_UUID, Sender.CONSOLE_NAME));
    }

    protected void registerEarlyListeners() {}

    protected void performFinalSetup() {}

    private Path resolveConfig() {
        final Path configFile = this.getBootstrap().getConfigDirectory();

        // if the config doesn't exist, create it based on the template in the resources dir
        if (!Files.exists(configFile)) {
            if (configFile.getParent() != null) {
                try {
                    Files.createDirectories(configFile.getParent());
                } catch (final IOException ignored) {}
            }

            try (final InputStream is = this.getBootstrap().getResourceStream("config.conf")) {
                if (is == null) {
                    this.getLogger()
                            .warn("Could not find file '{}' in the resources.", "config.conf");
                } else {
                    Files.copy(is, configFile);
                }
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configFile;
    }
}
