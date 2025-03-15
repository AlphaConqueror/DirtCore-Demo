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

package net.dirtcraft.dirtcore.common.plugin;

import java.util.Optional;
import net.dirtcraft.dirtcore.common.actionlog.LogDispatcher;
import net.dirtcraft.dirtcore.common.api.DirtCoreApiProvider;
import net.dirtcraft.dirtcore.common.command.CommandManager;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorOptions;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.config.DirtCoreConfiguration;
import net.dirtcraft.dirtcore.common.dependencies.DependencyManager;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.event.EventDispatcher;
import net.dirtcraft.dirtcore.common.event.listener.GeneralEventListener;
import net.dirtcraft.dirtcore.common.logging.Logger;
import net.dirtcraft.dirtcore.common.messaging.InternalMessagingService;
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
import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.dirtcraft.dirtcore.common.platform.sender.SenderFactory;
import net.dirtcraft.dirtcore.common.plugin.bootstrap.DirtCoreBootstrap;
import net.dirtcraft.dirtcore.common.plugin.util.AbstractConnectionListener;
import net.dirtcraft.dirtcore.common.storage.DirtCoreStorage;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.common.tasks.SyncTask;

public interface DirtCorePlugin {

    String MOD_ID = "@mod_id@";
    String MOD_NAME = "@mod_name@";
    String MOD_VERSION = "@version@";

    /**
     * Gets the bootstrap plugin instance
     *
     * @return the bootstrap plugin
     */
    DirtCoreBootstrap getBootstrap();

    /**
     * Gets the command manager instance for the platform.
     *
     * @return the command manager
     */
    CommandManager getCommandManager();

    /**
     * Gets the connection listener.
     *
     * @return the connection listener
     */
    AbstractConnectionListener<?, ?> getConnectionListener();

    /**
     * Gets the dependency manager for the plugin
     *
     * @return the dependency manager
     */
    DependencyManager getDependencyManager();

    /**
     * Returns the class implementing the DirtCoreAPI on this platform.
     *
     * @return the api
     */
    DirtCoreApiProvider getApiProvider();

    /**
     * Gets the discord manager instance for the platform.
     *
     * @return the discord manager, if available
     */
    Optional<DiscordBotClient> getDiscordBotClient();

    /**
     * Gets the plugin's configuration.
     *
     * @return the plugin config
     */
    DirtCoreConfiguration getConfiguration();

    /**
     * Gets the primary data storage instance. This is likely to be wrapped with extra layers for
     * caching, etc.
     *
     * @return the storage handler instance
     */
    DirtCoreStorage getStorage();

    /**
     * Gets the event dispatcher
     *
     * @return the event dispatcher
     */
    EventDispatcher getEventDispatcher();

    /**
     * Gets the messaging service.
     *
     * @return the messaging service
     */
    Optional<InternalMessagingService> getMessagingService();

    /**
     * Gets the log dispatcher running on the platform
     *
     * @return the log dispatcher
     */
    LogDispatcher getLogDispatcher();

    /**
     * Gets a logger instance for the platform.
     *
     * @return the logger
     */
    Logger getLogger();

    /**
     * Gets the sender factory.
     *
     * @return the sender factory
     */
    SenderFactory<? extends DirtCorePlugin, ?> getSenderFactory();

    /**
     * Gets the platform factory used to query data from the current minecraft platform.
     *
     * @return the platform factory
     */
    PlatformFactory<?, ?, ?, ?, ?, ?> getPlatformFactory();

    /**
     * Gets the entity selector options.
     *
     * @return the entity selector options
     */
    AbstractEntitySelectorOptions<? extends AbstractEntitySelectorParser<?, ?, ?>> getEntitySelectorOptions();

    /**
     * Gets the permission handler.
     *
     * @return the permission handler
     */
    PermissionHandler<?> getPermissionHandler();

    /**
     * Gets the network name.
     *
     * @return the network name
     */
    String getNetworkName();

    /**
     * Gets the network icon.
     *
     * @return the network icon
     */
    String getNetworkIcon();

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    String getServerName();

    /**
     * Gets the server identifier.
     *
     * @return the server identifier
     */
    String getServerIdentifier();

    /**
     * Gets the server icon.
     *
     * @return the server icon
     */
    String getServerIcon();

    /**
     * Gets the sync task buffer of the platform, used for scheduling and running sync tasks.
     *
     * @return the sync task buffer instance
     */
    SyncTask.Buffer getSyncTaskBuffer();

    /**
     * Gets the general event listener.
     *
     * @return the general event listener
     */
    GeneralEventListener getGeneralEventListener();

    /**
     * Get the chat manager.
     *
     * @return the chat manager
     */
    ChatManager getChatManager();

    /**
     * Get the crate manager.
     *
     * @return the crate manager
     */
    CrateManager getCrateManager();

    /**
     * Get the economy manager.
     *
     * @return the economy manager
     */
    EconomyManager getEconomyManager();

    /**
     * Get the kit manager.
     *
     * @return the kit manager
     */
    KitManager getKitManager();

    /**
     * Gets the messaging manager.
     *
     * @return the messaging manager
     */
    MessagingManager getMessagingManager();

    /**
     * Get the limit manager.
     *
     * @return the limit manager
     */
    LimitManager getLimitManager();

    /**
     * Get the punishment manager.
     *
     * @return the punishment manager
     */
    PunishmentManager<PunishmentEntity> getPunishmentManager();

    /**
     * Get the restriction manager.
     *
     * @return the restriction manager
     */
    RestrictionManager getRestrictionManager();

    /**
     * Gets the user manager.
     *
     * @return the user manager
     */
    UserManager<User> getUserManager();

    /**
     * Get the verification manager.
     *
     * @return the verification manager
     */
    VerificationManager getVerificationManager();
}
