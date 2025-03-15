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

package net.dirtcraft.dirtcore.forge_1_20_1;

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
import net.dirtcraft.dirtcore.forge_1_20_1.command.ForgeBrigadierExecutor;
import net.dirtcraft.dirtcore.forge_1_20_1.listeners.ForgeConnectionListener;
import net.dirtcraft.dirtcore.forge_1_20_1.listeners.ForgePlatformListener;
import net.dirtcraft.dirtcore.forge_1_20_1.permission.ForgeLPPermissionHandler;
import net.dirtcraft.dirtcore.forge_1_20_1.permission.ForgeVanillaPermissionHandler;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.ForgePlatformFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.entity.selector.ForgeEntitySelectorOptions;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.Connection;
import net.minecraftforge.event.entity.player.PlayerNegotiationEvent;
import net.minecraftforge.fml.ModList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreForgePlugin extends AbstractDirtCorePlugin {

    private final DirtCoreForgeBootstrap bootstrap;
    private ForgeEventDispatcher eventDispatcher;
    private PermissionHandler<CommandSourceStack> permissionHandler;
    private ForgeSenderFactory senderFactory;
    private ForgePlatformFactory platformFactory;
    private ForgeEntitySelectorOptions entitySelectorOptions;
    private ForgeConnectionListener connectionListener;

    public DirtCoreForgePlugin(final DirtCoreForgeBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public DirtCoreForgeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public AbstractConnectionListener<PlayerNegotiationEvent, Connection> getConnectionListener() {
        return this.connectionListener;
    }

    @Override
    public ForgeEventDispatcher getEventDispatcher() {
        return this.eventDispatcher;
    }

    @Override
    public @NonNull ForgeSenderFactory getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public ForgePlatformFactory getPlatformFactory() {
        return this.platformFactory;
    }

    @Override
    public ForgeEntitySelectorOptions getEntitySelectorOptions() {
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
        this.senderFactory = new ForgeSenderFactory(this);
    }

    @Override
    protected void setupPlatformFactory() {
        this.platformFactory = new ForgePlatformFactory(this);
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
        this.eventDispatcher = new ForgeEventDispatcher(new ForgeEventBus(this, provider), this);
    }

    @Override
    protected void loadEntitySelectorOptions() {
        this.entitySelectorOptions = new ForgeEntitySelectorOptions();
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
        this.connectionListener = new ForgeConnectionListener(this);
        this.bootstrap.registerListeners(this.connectionListener);

        final ForgePlatformListener platformListener = new ForgePlatformListener(this);
        this.bootstrap.registerListeners(platformListener);

        final ForgeBrigadierExecutor forgeBrigadierExecutor = new ForgeBrigadierExecutor(this);
        this.bootstrap.registerListeners(forgeBrigadierExecutor);
    }

    private PermissionHandler<CommandSourceStack> determinePermissionHandler() {
        if (ModList.get().isLoaded("luckperms")) {
            this.getLogger().info("Using LuckPerms permission handler.");
            return new ForgeLPPermissionHandler(this);
        }

        this.getLogger().info("Using Vanilla permission handler.");
        return new ForgeVanillaPermissionHandler(this);
    }
}
