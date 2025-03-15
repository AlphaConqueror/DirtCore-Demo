/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1;

import java.util.Set;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.dependencies.Dependency;
import net.dirtcraft.dirtcore.common.messaging.MessagingFactory;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.manager.chat.ChatManager;
import net.dirtcraft.dirtcore.common.model.manager.crate.CrateManager;
import net.dirtcraft.dirtcore.common.model.manager.economy.EconomyManager;
import net.dirtcraft.dirtcore.common.model.manager.kit.KitManager;
import net.dirtcraft.dirtcore.common.model.manager.limit.LimitManager;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.model.manager.punishment.PunishmentManager;
import net.dirtcraft.dirtcore.common.model.manager.restrict.RestrictionManager;
import net.dirtcraft.dirtcore.common.model.manager.user.UserManager;
import net.dirtcraft.dirtcore.common.model.manager.vote.VerificationManager;
import net.dirtcraft.dirtcore.common.permission.PermissionHandler;
import net.dirtcraft.dirtcore.common.plugin.AbstractDirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.neoforge_1_21_1.api.event.player.PlayerNegotiationEvent;
import net.dirtcraft.dirtcore.neoforge_1_21_1.command.NeoForgeBrigadierExecutor;
import net.dirtcraft.dirtcore.neoforge_1_21_1.listeners.NeoForgeConnectionListener;
import net.dirtcraft.dirtcore.neoforge_1_21_1.listeners.NeoForgePlatformListener;
import net.dirtcraft.dirtcore.neoforge_1_21_1.permission.NeoForgeLPPermissionHandler;
import net.dirtcraft.dirtcore.neoforge_1_21_1.permission.NeoForgeVanillaPermissionHandler;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.NeoForgePlatformFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.selector.NeoForgeEntitySelectorOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.Connection;
import net.neoforged.fml.ModList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreNeoForgePlugin extends AbstractDirtCorePlugin {

    private final DirtCoreNeoForgeBootstrap bootstrap;
    private NeoForgeEventDispatcher eventDispatcher;
    private PermissionHandler<CommandSourceStack> permissionHandler;
    private NeoForgeSenderFactory senderFactory;
    private NeoForgePlatformFactory platformFactory;
    private NeoForgeEntitySelectorOptions entitySelectorOptions;
    private NeoForgeConnectionListener connectionListener;

    public DirtCoreNeoForgePlugin(final DirtCoreNeoForgeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public DirtCoreNeoForgeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public AbstractConnectionListener<PlayerNegotiationEvent, Connection> getConnectionListener() {
        return this.connectionListener;
    }

    @Override
    public NeoForgeEventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    @Override
    public @NonNull NeoForgeSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public NeoForgePlatformFactory getPlatformFactory() {
        return this.platformFactory;
    }

    @Override
    public NeoForgeEntitySelectorOptions getEntitySelectorOptions() {
        return this.entitySelectorOptions;
    }

    @Override
    public PermissionHandler<CommandSourceStack> getPermissionHandler() {
        if (this.permissionHandler == null) {
            this.permissionHandler = this.determinePermissionHandler();
        }

        return this.permissionHandler;
    }

    @Override
    public ChatManager getChatManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CrateManager getCrateManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public EconomyManager getEconomyManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public KitManager getKitManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessagingManager getMessagingManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public LimitManager getLimitManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PunishmentManager<PunishmentEntity> getPunishmentManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public RestrictionManager getRestrictionManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserManager<User> getUserManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    public VerificationManager getVerificationManager() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void registerCommands() {
        // Too late for Forge, registered in #registerEarlyListeners
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new NeoForgeSenderFactory(this);
    }

    @Override
    protected void setupPlatformFactory() {
        this.platformFactory = new NeoForgePlatformFactory(this);
    }

    @Override
    protected void registerPlatformListeners() {
        // Too late for Forge, registered in #registerEarlyListeners
    }

    @Override
    protected MessagingFactory<?> provideMessagingFactory() {
        return new MessagingFactory<DirtCorePlugin>(this);
    }

    @Override
    protected void setupManagers() {
        // declare managers here
    }

    @Override
    protected void setupEventDispatcher(@NonNull final DirtCoreApiProvider provider) {
        this.eventDispatcher =
                new NeoForgeEventDispatcher(new NeoForgeEventBus(this, provider), this);
    }

    @Override
    protected void loadEntitySelectorOptions() {
        this.entitySelectorOptions = new NeoForgeEntitySelectorOptions();
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        final Set<Dependency> dependencies = super.getGlobalDependencies();

        dependencies.add(Dependency.CONFIGURATE_CORE);
        dependencies.add(Dependency.CONFIGURATE_HOCON);
        dependencies.add(Dependency.HOCON_CONFIG);

        return dependencies;
    }

    @Override
    protected void registerEarlyListeners() {
        this.connectionListener = new NeoForgeConnectionListener(this);
        this.bootstrap.registerListeners(this.connectionListener);

        final NeoForgePlatformListener platformListener = new NeoForgePlatformListener(this);
        this.bootstrap.registerListeners(platformListener);

        final NeoForgeBrigadierExecutor neoForgeBrigadierExecutor =
                new NeoForgeBrigadierExecutor(this);
        this.bootstrap.registerListeners(neoForgeBrigadierExecutor);
    }

    private PermissionHandler<CommandSourceStack> determinePermissionHandler() {
        if (ModList.get().isLoaded("luckperms")) {
            this.getLogger().info("Using LuckPerms permission handler.");
            return new NeoForgeLPPermissionHandler(this);
        }

        this.getLogger().info("Using Vanilla permission handler.");
        return new NeoForgeVanillaPermissionHandler(this);
    }
}
