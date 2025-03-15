/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.api;

import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.api.discord.DiscordManager;
import net.dirtcraft.dirtcore.api.event.EventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The DirtCore API.
 *
 * <p>The API allows other plugins on the server to read and modify DirtCore
 * data, change behaviour of the plugin, listen to certain events, and integrate
 * DirtCore into other plugins and systems.</p>
 *
 * <p>This interface represents the base of the API package. All functions are
 * accessed via this interface.</p>
 *
 * <p>To start using the API, you need to obtain an instance of this interface.
 * These are registered by the DirtCore plugin to the platforms Services
 * Manager. This is the preferred method for obtaining an instance.</p>
 *
 * <p>For ease of use, and for platforms without a Service Manager, an instance
 * can also be obtained from the static singleton accessor in
 * {@link DirtCoreProvider}.</p>
 */
public interface DirtCore {

    /**
     * Gets the {@link DiscordManager}, responsible for Discord bot access.
     *
     * @return the discord manager
     */
    @NonNull DiscordManager getDiscordManager();

    /**
     * Gets the {@link EventBus}, used for subscribing to internal DirtCore
     * events.
     *
     * @return the event bus
     */
    @NonNull EventBus getEventBus();

    /**
     * Schedules the execution of an update task, and returns an encapsulation
     * of the task as a {@link CompletableFuture}.
     *
     * <p>The exact actions performed in an update task remains an
     * implementation detail of the plugin, however, as a minimum, it is
     * expected to perform a full reload of user, group and track data, and
     * ensure that any changes are fully applied and propagated.</p>
     *
     * @return a future
     */
    @NonNull CompletableFuture<Void> runUpdateTask();
}
