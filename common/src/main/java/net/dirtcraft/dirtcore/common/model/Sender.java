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

package net.dirtcraft.dirtcore.common.model;

import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.exception.PlayerNotFoundException;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a CommandSender/CommandSource within the common command
 * implementations.
 */
public interface Sender extends Identifiable, Permissible, SharedSuggestionProvider {

    /**
     * The uuid used by the console sender.
     */
    UUID CONSOLE_UUID = new UUID(0, 0); // 00000000-0000-0000-0000-000000000000
    /**
     * The name used by the console sender.
     */
    String CONSOLE_NAME = "Console";

    /**
     * Gets the plugin instance the sender is from.
     *
     * @return the plugin
     */
    @NonNull DirtCorePlugin getPlugin();

    /**
     * Send a json message to the Sender.
     *
     * @param message the message to send.
     */
    void sendMessage(Component message);

    /**
     * Send multiple json messages to the Sender.
     *
     * @param messages the messages to send.
     */
    void sendMessage(final Iterable<Component> messages);

    /**
     * Gets whether this sender is the console.
     *
     * @return if the sender is the console
     */
    boolean isConsole();

    /**
     * Gets whether this sender is an entity.
     *
     * @return if the sender is an entity
     */
    boolean isEntity();

    /**
     * Gets the entity instance for this sender.
     *
     * @return the entity instance
     */
    @NonNull Optional<Entity> getEntity();

    /**
     * Gets the world the sender is in.
     *
     * @return the world
     */
    @NonNull Optional<World> getWorld();

    /**
     * Gets a string representing the senders username, and their current location
     * within the network.
     *
     * @return a friendly identifier for the sender
     */
    default String getNameWithLocation() {
        return this.getName() + '@' + this.getPlugin().getServerIdentifier();
    }

    @NonNull
    default User getUser(@NonNull final TaskContext context) {
        return this.getPlugin().getUserManager().getOrCreateUser(context, this.getUniqueId());
    }

    /**
     * Gets the player instance for this object.
     *
     * @return the player instance
     */
    @NonNull
    default Optional<Player> getPlayer() {
        return this.getPlugin().getPlatformFactory().getPlayer(this.getUniqueId());
    }

    /**
     * Gets the player instance for this object.
     * Only to be used in cases a command can NOT be used by the console.
     * This might cause issues in async context.
     *
     * @return the player instance
     * @throws PlayerNotFoundException in case the player could not be found
     */
    @NonNull
    default Player getPlayerOrException() throws PlayerNotFoundException {
        final UUID uniqueId = this.getUniqueId();
        return this.getPlugin().getPlatformFactory().getPlayer(uniqueId)
                .orElseThrow(() -> new PlayerNotFoundException(uniqueId));
    }

    @NonNull
    default Vec3 getPosition() {
        return this.getPlayer().map(Player::getPosition).orElse(Vec3.ZERO);
    }

    @NonNull
    default Vec2i getChunkPos() {
        return this.getPlayer().map(Player::getChunkPos).orElse(Vec2i.ZERO);
    }

    @NonNull
    default Vec2 getRotation() {
        return this.getPlayer().map(Player::getRotation).orElse(Vec2.ZERO);
    }
}
