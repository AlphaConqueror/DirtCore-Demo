/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft;

import java.util.List;
import net.dirtcraft.dirtcore.common.model.Permissible;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.KickEntity;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a player within the minecraft player implementations.
 */
public interface Player extends Entity, Permissible {

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    @NonNull Component getDisplayName();

    /**
     * Checks if the player has disconnected.
     *
     * @return true, if disconnected
     */
    boolean hasDisconnected();

    /**
     * Gets the item in hand.
     *
     * @return the item in hand
     */
    @NonNull ItemStack getItemInHand();

    /**
     * Gets the items in the players inventory.
     *
     * @return the items
     */
    @NonNull List<ItemStack> getItems();

    /**
     * Kicks the player.
     *
     * @param reason the reason
     * @return true, if successful
     */
    boolean kick(@NonNull Component reason);

    /**
     * Sends a message.
     *
     * @param message the message
     */
    void sendMessage(@NonNull Component message);

    /**
     * Sends messages.
     *
     * @param messages the messages
     */
    void sendMessage(@NonNull Iterable<Component> messages);

    /**
     * Performs a command.
     *
     * @param command the command
     */
    void performCommand(String command);

    /**
     * Gets the free inventory space in the players inventory.
     *
     * @return the free inventory space
     */
    int getFreeInventorySpace();

    /**
     * Checks if the player is in creative mode.
     *
     * @return true, if in creative
     */
    boolean isCreative();

    /**
     * Open container.
     *
     * @param container the container
     */
    void openContainer(@NonNull Container container);

    /**
     * Plays a sound to the player.
     *
     * @param sound the sound
     */
    void playSound(@NonNull Sound sound);

    /**
     * Adds an item to the players inventory.
     * <p>
     * If the item is empty, nothing happens.
     * If the inventory can not handle all items, the remaining ones will be dropped instead.
     *
     * @param itemStack the item stack
     * @see ItemStack#isEmpty()
     */
    void addItem(@NonNull ItemStack itemStack);

    /**
     * Teleports a player to a position in a world.
     *
     * @param world the world
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @param yRot  the y rotation
     * @param xRot  the x rotation
     * @return true, if successful
     */
    boolean teleport(@NonNull World world, double x, double y, double z, float yRot, float xRot);

    /**
     * Gets the user for this player.
     *
     * @param context the context
     * @return the user
     */
    @NonNull
    default User getUser(@NonNull final TaskContext context) {
        return this.getPlugin().getUserManager().getOrCreateUser(context, this.getUniqueId());
    }

    /**
     * Kicks the user from the server.
     *
     * @param context    the task context
     * @param sender     the sender
     * @param reason     the reason
     * @param isIncident true, if this should be logged
     * @return true if successful
     */
    default boolean kick(@NonNull final TaskContext context, @NonNull final Sender sender,
            @NonNull final String reason, final boolean isIncident) {
        final PlatformFactory<?, ?, ?, ?, ?, ?> platformFactory =
                this.getPlugin().getPlatformFactory();
        final boolean result;

        if (isIncident) {
            final String incidentId =
                    this.getPlugin().getPunishmentManager().nextIncidentId(context);
            final KickEntity kick = this.getPlugin().getPunishmentManager()
                    .kick(context, incidentId, sender, this.getUser(context), reason);

            result = this.kick(
                    platformFactory.kickIncidentScreenComponent().build(kick, sender.getName()));
        } else {
            result = this.kick(platformFactory.kickNoIncidentScreenComponent()
                    .build(reason, sender.getName()));
        }

        return result;
    }

    default boolean teleport(final double x, final double y, final double z) {
        return this.teleport(this.getWorld(), x, y, z);
    }

    default boolean teleport(final double x, final double y, final double z, final float yRot,
            final float xRot) {
        return this.teleport(this.getWorld(), x, y, z, yRot, xRot);
    }

    default boolean teleport(@NonNull final World world, final double x, final double y,
            final double z) {
        final Vec2 rotation = this.getRotation();
        return this.teleport(world, x, y, z, rotation.y, rotation.x);
    }

    default boolean teleport(@NonNull final Vec3 pos) {
        return this.teleport(pos.x, pos.y, pos.z);
    }

    default boolean teleport(@NonNull final World world, @NonNull final Vec3 pos) {
        return this.teleport(world, pos.x, pos.y, pos.z);
    }

    default boolean teleport(@NonNull final Vec3 pos, @NonNull final Vec2 rot) {
        return this.teleport(pos.x, pos.y, pos.z, rot.y, rot.x);
    }

    default boolean teleport(@NonNull final World world, @NonNull final Vec3 pos,
            @NonNull final Vec2 rot) {
        return this.teleport(world, pos.x, pos.y, pos.z, rot.y, rot.x);
    }
}
