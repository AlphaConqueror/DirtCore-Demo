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

package net.dirtcraft.dirtcore.common.event;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import net.dirtcraft.dirtcore.api.event.DirtCoreEvent;
import net.dirtcraft.dirtcore.api.event.type.Cancellable;
import net.dirtcraft.dirtcore.api.event.type.ResultEvent;
import net.dirtcraft.dirtcore.common.event.gen.GeneratedEventClass;
import net.dirtcraft.dirtcore.common.event.internal.block.BlockChangeEvent;
import net.dirtcraft.dirtcore.common.event.internal.block.BlockPushReactionEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerAchievementEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerAttackEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerBlockBreakEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerBlockPlaceEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerChatEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerDeathEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerInteractEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerInventoryClickEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerItemDropEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerItemPickupEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLoginEvent;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLogoutEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerCommandEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerStartedEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerStartingEvent;
import net.dirtcraft.dirtcore.common.event.internal.server.ServerStoppingEvent;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class EventDispatcher {

    private final AbstractEventBus<?> eventBus;

    public EventDispatcher(final AbstractEventBus<?> eventBus) {
        this.eventBus = eventBus;
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends DirtCoreEvent>[] getKnownEventTypes() {
        return new Class[] {BlockChangeEvent.class, BlockPushReactionEvent.class,
                PlayerAchievementEvent.class, PlayerAttackEvent.class,
                PlayerBlockBreakEvent.class, PlayerBlockPlaceEvent.class, PlayerChatEvent.class,
                PlayerChatEvent.class, PlayerDeathEvent.class, PlayerInteractEvent.class,
                PlayerInventoryClickEvent.class, PlayerItemDropEvent.class,
                PlayerItemPickupEvent.class, PlayerLoginEvent.class, PlayerLogoutEvent.class,
                ServerCommandEvent.class, ServerStartedEvent.class, ServerStartingEvent.class,
                ServerStoppingEvent.class};
    }

    public AbstractEventBus<?> getEventBus() {
        return this.eventBus;
    }

    public void dispatchBlockChangeEvent(final Block oldBlock, final Block newBlock,
            final int flags, final World world, final int x, final int y, final int z) {
        this.postAsync(BlockChangeEvent.class, oldBlock, newBlock, flags, world, x, y, z);
    }

    public boolean dispatchBlockPushReactionEvent(final boolean initialState, final Block block) {
        return this.postCancellable(BlockPushReactionEvent.class, initialState, block);
    }

    public void dispatchPlayerAchievement(final UUID uniqueId, final String username,
            final String achievementName, final String achievementDescription) {
        this.postAsync(PlayerAchievementEvent.class, uniqueId, username, achievementName,
                achievementDescription);
    }

    public boolean dispatchPlayerAttack(final boolean initialState, final UUID uniqueId,
            final String username, final ItemStack itemStack) {
        return this.postCancellable(PlayerAttackEvent.class, initialState, uniqueId, username,
                itemStack);
    }

    public boolean dispatchPlayerBlockBreak(final boolean initialState, final UUID uniqueId,
            final String username, final Block block, final World world, final BlockPos blockPos,
            final boolean isFakePlayer) {
        return this.postCancellable(PlayerBlockBreakEvent.class, initialState, uniqueId, username,
                block, world, blockPos, isFakePlayer);
    }

    public boolean dispatchPlayerBlockPlace(final boolean initialState, final UUID uniqueId,
            final String username, final Block block, final World world, final int x, final int y,
            final int z, final boolean isFakePlayer) {
        return this.postCancellable(PlayerBlockPlaceEvent.class, initialState, uniqueId, username,
                block, world, x, y, z, isFakePlayer);
    }

    public boolean dispatchPlayerChat(final boolean initialState, final UUID uniqueId,
            final String message) {
        return this.postCancellable(PlayerChatEvent.class, initialState, uniqueId, message);
    }

    public void dispatchPlayerDeath(final UUID uniqueId, final String username,
            final String deathMessage) {
        this.postAsync(PlayerDeathEvent.class, uniqueId, username, deathMessage);
    }

    public boolean dispatchPlayerInteract(final boolean initialState,
            final PlayerInteractEvent.@NonNull Type type, @NonNull final UUID uniqueId,
            @NonNull final String username, @NonNull final BlockPos pos,
            @Nullable final ItemStack itemStack, @Nullable final Block block) {
        return this.postCancellable(PlayerInteractEvent.class, initialState, type, uniqueId,
                username, pos, itemStack, block);
    }

    public boolean dispatchPlayerInventoryClick(final boolean initialState, final UUID uniqueId,
            final String username, final ItemStack itemStack) {
        return this.postCancellable(PlayerInventoryClickEvent.class, initialState, uniqueId,
                username, itemStack);
    }

    public boolean dispatchPlayerItemDrop(final boolean initialState, final UUID uniqueId,
            final String username, final ItemStack itemStack) {
        return this.postCancellable(PlayerItemDropEvent.class, initialState, uniqueId, username,
                itemStack);
    }

    public boolean dispatchPlayerItemPickup(final boolean initialState, final UUID uniqueId,
            final String username, final ItemStack itemStack) {
        return this.postCancellable(PlayerItemPickupEvent.class, initialState, uniqueId, username,
                itemStack);
    }

    public void dispatchPlayerLogin(final UUID uniqueId, final String username) {
        this.postSync(PlayerLoginEvent.class, uniqueId, username);
    }

    public void dispatchPlayerLogout(final UUID uniqueId, final String username) {
        this.postAsync(PlayerLogoutEvent.class, uniqueId, username);
    }

    public boolean dispatchServerCommand(final boolean initialState, final UUID uniqueId,
            final String username, final String commandLine) {
        return this.postCancellable(ServerCommandEvent.class, initialState, uniqueId, username,
                commandLine);
    }

    public void dispatchServerStarted() {
        this.postAsync(ServerStartedEvent.class);
    }

    public void dispatchServerStarting() {
        this.postAsync(ServerStartingEvent.class);
    }

    public void dispatchServerStopping() {
        this.postAsync(ServerStoppingEvent.class);
    }

    protected void postAsync(final Class<? extends DirtCoreEvent> eventClass,
            final Object... params) {
        // check against common mistakes - events with any sort of result shouldn't be posted async
        if (Cancellable.class.isAssignableFrom(eventClass) || ResultEvent.class.isAssignableFrom(
                eventClass)) {
            throw new RuntimeException(
                    "Event cannot be posted async (" + eventClass.getName() + ")");
        }

        // if there aren't any handlers registered for the event, don't bother trying to post it
        if (!this.eventBus.shouldPost(eventClass)) {
            return;
        }

        // async: generate an event class and post it
        this.eventBus.getPlugin().getBootstrap().getScheduler()
                .executeAsync(() -> this.post(eventClass, params));
    }

    protected void postSync(final Class<? extends DirtCoreEvent> eventClass,
            final Object... params) {
        // if there aren't any handlers registered for our event, don't bother trying to post it
        if (!this.eventBus.shouldPost(eventClass)) {
            return;
        }

        // generate an event class and post it
        this.post(eventClass, params);
    }

    protected boolean postCancellable(final Class<? extends DirtCoreEvent> eventClass,
            final Object... params) {
        if (!Cancellable.class.isAssignableFrom(eventClass)) {
            throw new RuntimeException("Event is not cancellable: " + eventClass.getName());
        }

        // extract the initial state from the first parameter
        final boolean initialState = (boolean) params[0];

        // if there aren't any handlers registered for the event, just return the initial state
        if (!this.eventBus.shouldPost(eventClass)) {
            return initialState;
        }

        // otherwise:
        // - initialise an AtomicBoolean for the result with the initial state
        // - replace the boolean with the AtomicBoolean in the params array
        // - generate an event class and post it
        final AtomicBoolean cancel = new AtomicBoolean(initialState);
        params[0] = cancel;
        this.post(eventClass, params);

        // return the final status
        return cancel.get();
    }

    private DirtCoreEvent generate(final Class<? extends DirtCoreEvent> eventClass,
            final Object... params) {
        try {
            return GeneratedEventClass.generate(eventClass)
                    .newInstance(this.eventBus.getApiProvider(), params);
        } catch (final Throwable e) {
            throw new RuntimeException("Exception occurred whilst generating event instance", e);
        }
    }

    private void post(final Class<? extends DirtCoreEvent> eventClass, final Object... params) {
        final DirtCoreEvent event = this.generate(eventClass, params);
        this.eventBus.post(event);
    }
}
