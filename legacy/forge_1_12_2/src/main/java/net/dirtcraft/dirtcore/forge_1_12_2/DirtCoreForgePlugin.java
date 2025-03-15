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

package net.dirtcraft.dirtcore.forge_1_12_2;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.dependencies.Dependency;
import net.dirtcraft.dirtcore.common.messaging.MessagingFactory;
import net.dirtcraft.dirtcore.common.model.Sender;
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
import net.dirtcraft.dirtcore.common.storage.DirtCoreStorageFactory;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_12_2.api.event.player.PlayerNegotiationEvent;
import net.dirtcraft.dirtcore.forge_1_12_2.command.DirtForgeCommand;
import net.dirtcraft.dirtcore.forge_1_12_2.listeners.ForgeConnectionListener;
import net.dirtcraft.dirtcore.forge_1_12_2.listeners.ForgePlatformListener;
import net.dirtcraft.dirtcore.forge_1_12_2.permission.ForgeLPPermissionHandler;
import net.dirtcraft.dirtcore.forge_1_12_2.permission.ForgeVanillaPermissionHandler;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.ForgePlatformFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.ForgeArgumentFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.entity.selector.ForgeEntitySelectorOptions;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Loader;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtCoreForgePlugin extends AbstractDirtCorePlugin {

    /**
     * {@link CommandHandler#commandMap}
     */
    private static final Field COMMAND_MAP;
    /**
     * {@link CommandHandler#commandSet}
     */
    private static final Field COMMAND_SET;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            COMMAND_MAP = CommandHandler.class.getDeclaredField("field_71562_a");
            COMMAND_MAP.setAccessible(true);

            //noinspection JavaReflectionMemberAccess
            COMMAND_SET = CommandHandler.class.getDeclaredField("field_71561_b");
            COMMAND_SET.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private final DirtCoreForgeBootstrap bootstrap;
    private ForgeEventDispatcher eventDispatcher;
    private PermissionHandler<ICommandSender> permissionHandler;
    private ForgeSenderFactory senderFactory;
    private ForgePlatformFactory platformFactory;
    private ForgeEntitySelectorOptions entitySelectorOptions;
    private ForgeConnectionListener connectionListener;

    public DirtCoreForgePlugin(final DirtCoreForgeBootstrap bootstrap) {this.bootstrap = bootstrap;}

    @Override
    public DirtCoreForgeBootstrap getBootstrap() {
        return this.bootstrap;
    }

    @Override
    public AbstractConnectionListener<PlayerNegotiationEvent, NetworkManager> getConnectionListener() {
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
    public PermissionHandler<ICommandSender> getPermissionHandler() {
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

    public void onRegisterCommands() {
        final Optional<MinecraftServer> serverOptional = this.bootstrap.getServer();

        if (!serverOptional.isPresent()) {
            this.getLogger().warn("Server not found, could not register commands.");
            return;
        }

        // build commands before adding them
        this.buildCommands(new ForgeArgumentFactory(this));

        final CommandDispatcher<DirtCorePlugin, Sender> dispatcher =
                this.getCommandManager().getDispatcher();
        final Collection<CommandNode<DirtCorePlugin, Sender>> children =
                dispatcher.getRoot().getChildren();
        final Set<String> childrenNames =
                children.stream().map(CommandNode::getName).map(s -> s.toLowerCase(Locale.ROOT))
                        .collect(ImmutableCollectors.toSet());
        final CommandHandler commandHandler =
                (CommandHandler) serverOptional.get().getCommandManager();

        // remove all commands with the same names before adding our own
        try {
            @SuppressWarnings("unchecked") final Map<String, ICommand> commandMap =
                    (Map<String, ICommand>) COMMAND_MAP.get(commandHandler);
            @SuppressWarnings("unchecked") final Set<ICommand> commandSet =
                    (Set<ICommand>) COMMAND_SET.get(commandHandler);

            for (final Map.Entry<String, ICommand> entry : new HashSet<>(commandMap.entrySet())) {
                final ICommand command = entry.getValue();

                if (childrenNames.contains(command.getName().toLowerCase(Locale.ROOT))
                        || command.getAliases().stream().anyMatch(
                        alias -> childrenNames.contains(alias.toLowerCase(Locale.ROOT)))) {

                    commandMap.remove(entry.getKey());
                    commandSet.remove(command);
                }
            }

            COMMAND_MAP.set(commandHandler, commandMap);
            COMMAND_SET.set(commandHandler, commandSet);
        } catch (final IllegalAccessException e) {
            this.getLogger()
                    .severe("Caught exception while trying to remove duplicate commands.", e);
        }

        for (final CommandNode<DirtCorePlugin, Sender> commandNode : dispatcher.getRoot()
                .getChildren()) {
            commandHandler.registerCommand(new DirtForgeCommand(this, commandNode));
        }
    }

    @Override
    protected void registerCommands() {
        // Too late for Forge, registered in #onRegisterCommands
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
        this.entitySelectorOptions = new ForgeEntitySelectorOptions(this);
    }

    @Override
    protected Set<Dependency> getGlobalDependencies() {
        // we load them using Gradle
        return Collections.emptySet();
    }

    @Override
    protected void loadStorageDependencies(@NonNull final DirtCoreStorageFactory storageFactory) {
        // no-op, since we load them manually
    }

    @Override
    protected void registerEarlyListeners() {
        this.connectionListener = new ForgeConnectionListener(this);
        this.bootstrap.registerListeners(this.connectionListener);

        final ForgePlatformListener platformListener = new ForgePlatformListener(this);
        this.bootstrap.registerListeners(platformListener);
    }

    private PermissionHandler<ICommandSender> determinePermissionHandler() {
        if (Loader.isModLoaded("luckperms")) {
            this.getLogger().info("Using LuckPerms permission handler.");
            return new ForgeLPPermissionHandler();
        }

        this.getLogger().info("Using Vanilla permission handler.");
        return new ForgeVanillaPermissionHandler(this);
    }
}
