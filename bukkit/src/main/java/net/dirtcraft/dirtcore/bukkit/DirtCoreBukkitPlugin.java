/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit;

import com.google.common.collect.ImmutableList;
import java.util.Set;
import net.dirtcraft.dirtcore.bukkit.command.DirtBukkitCommand;
import net.dirtcraft.dirtcore.bukkit.listeners.BukkitConnectionListener;
import net.dirtcraft.dirtcore.bukkit.listeners.BukkitPlatformListener;
import net.dirtcraft.dirtcore.bukkit.permission.BukkitLPPermissionHandler;
import net.dirtcraft.dirtcore.bukkit.permission.BukkitPermissionHandler;
import net.dirtcraft.dirtcore.bukkit.platform.BukkitPlatformFactory;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.dependencies.Dependency;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.messaging.MessagingFactory;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.model.manager.messaging.StandardMessagingManager;
import net.dirtcraft.dirtcore.common.model.manager.punishment.PunishmentManager;
import net.dirtcraft.dirtcore.common.model.manager.punishment.StandardPunishmentManager;
import net.dirtcraft.dirtcore.common.model.manager.user.StandardUserManager;
import net.dirtcraft.dirtcore.common.model.manager.user.UserManager;
import net.dirtcraft.dirtcore.common.model.manager.vote.StandardVoteManager;
import net.dirtcraft.dirtcore.common.model.manager.vote.VoteManager;
import net.dirtcraft.dirtcore.common.permission.PermissionHandler;
import net.dirtcraft.dirtcore.common.platform.sender.Sender;
import net.dirtcraft.dirtcore.common.plugin.AbstractDirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreBukkitPlugin extends AbstractDirtCorePlugin {

    private final DirtCoreBukkitBootstrap bootstrap;
    private PermissionHandler<CommandSender> permissionHandler;
    private BukkitSenderFactory senderFactory;
    private BukkitPlatformFactory platformFactory;
    private BukkitConnectionListener connectionListener;
    private StandardMessagingManager messagingManager;
    private StandardUserManager userManager;
    private StandardPunishmentManager punishmentManager;
    private StandardVoteManager voteManager;

    public DirtCoreBukkitPlugin(final DirtCoreBukkitBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public DirtCoreBukkitBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public AbstractConnectionListener<AsyncPlayerPreLoginEvent> getConnectionListener() {
        return this.connectionListener;
    }

    @Override
    public @NonNull BukkitSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public MessagingManager getMessagingManager() {
        return this.messagingManager;
    }

    @Override
    public UserManager<? extends User> getUserManager() {
        return this.userManager;
    }

    @Override
    public @NonNull BukkitPlatformFactory getPlatformFactory() {
        return this.platformFactory;
    }

    @Override
    public PermissionHandler<CommandSender> getPermissionHandler() {
        if (this.permissionHandler == null) {
            this.permissionHandler = this.determinePermissionHandler();
        }

        return this.permissionHandler;
    }

    @Override
    public PunishmentManager<PunishmentEntity> getPunishmentManager() {
        return this.punishmentManager;
    }

    @Override
    public VoteManager getVoteManager() {
        return this.voteManager;
    }

    @Override
    protected void registerEarlyListeners() {
        // Too early for Bukkit, registered in #registerPlatformListeners
    }

    @Override
    protected void registerCommands() {
        final CommandMap commandMap = this.bootstrap.getServer().getCommandMap();
        final ImmutableList.Builder<Command> commandListBuilder = ImmutableList.builder();

        for (final CommandNode<Sender> commandNode : this.getCommandManager().getDispatcher()
                .getRoot().getChildren()) {
            commandListBuilder.add(new DirtBukkitCommand(this, commandNode));
        }

        commandMap.registerAll(DirtCorePlugin.MOD_ID, commandListBuilder.build());
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new BukkitSenderFactory(this);
    }

    @Override
    protected void setupPlatformFactory() {
        this.platformFactory = new BukkitPlatformFactory(this);
    }

    @Override
    protected void registerPlatformListeners() {
        this.connectionListener = new BukkitConnectionListener(this);
        this.bootstrap.getServer().getPluginManager()
                .registerEvents(this.connectionListener, this.bootstrap.getLoader());

        final BukkitPlatformListener platformListener = new BukkitPlatformListener(this);
        this.bootstrap.getServer().getPluginManager()
                .registerEvents(platformListener, this.bootstrap.getLoader());
    }

    @Override
    protected void performFinalSetup() {
        this.getApiProvider().getEventBus().subscribe(this.messagingManager);
    }

    @Override
    protected MessagingFactory<?> provideMessagingFactory() {
        return new MessagingFactory<DirtCorePlugin>(this);
    }

    @Override
    protected void setupManagers() {
        this.messagingManager = new StandardMessagingManager(this);
        this.userManager = new StandardUserManager(this);
        this.punishmentManager = new StandardPunishmentManager(this);
        this.voteManager = new StandardVoteManager(this);
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        final Set<Dependency> dependencies = super.getGlobalDependencies();

        dependencies.add(Dependency.ADVENTURE_PLATFORM_API);
        dependencies.add(Dependency.ADVENTURE_TEXT_SERIALIZER_BUNGEECORD);
        dependencies.add(Dependency.ADVENTURE_TEXT_SERIALIZER_LEGACY);
        dependencies.add(Dependency.ADVENTURE_TEXT_SERIALIZER_GSON_LEGACY);
        dependencies.add(Dependency.CONFIGURATE_CORE);
        dependencies.add(Dependency.CONFIGURATE_HOCON);
        dependencies.add(Dependency.HOCON_CONFIG);

        return dependencies;
    }

    @Override
    protected AbstractEventBus<?> provideEventBus(final DirtCoreApiProvider provider) {
        return new BukkitEventBus(this, provider);
    }

    private PermissionHandler<CommandSender> determinePermissionHandler() {
        if (Bukkit.getPluginManager().isPluginEnabled("LuckPerms")) {
            this.getLogger().info("Using LuckPerms permission handler.");
            return new BukkitLPPermissionHandler(this);
        } else {
            this.getLogger().info("Using Bukkit permission handler.");
            return new BukkitPermissionHandler(this);
        }
    }
}
