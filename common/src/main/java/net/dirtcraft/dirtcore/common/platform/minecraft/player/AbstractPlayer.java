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

package net.dirtcraft.dirtcore.common.platform.minecraft.player;

import java.util.Collection;
import java.util.List;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.AbstractEntity;
import net.dirtcraft.dirtcore.common.platform.minecraft.entity.EntityFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of {@link Player} using a {@link PlayerFactory}.
 *
 * @param <E> the entity type
 * @param <P> the player type
 */
public final class AbstractPlayer<E, P extends E> extends AbstractEntity<E> implements Player {

    private final PlayerFactory<?, E, P> playerFactory;
    private final P player;

    public AbstractPlayer(final DirtCorePlugin plugin, final EntityFactory<?, E> entityFactory,
            final PlayerFactory<?, E, P> playerFactory, final P player) {
        super(plugin, entityFactory, player);
        this.playerFactory = playerFactory;
        this.player = player;
    }

    @Override
    public @NonNull Component getDisplayName() {
        return this.playerFactory.getDisplayName(this.player);
    }

    @Override
    public boolean hasDisconnected() {
        return this.playerFactory.hasDisconnected(this.player);
    }

    @Override
    public @NonNull ItemStack getItemInHand() {
        return this.playerFactory.getItemInHand(this.player);
    }

    @Override
    public @NonNull List<ItemStack> getItems() {
        return this.playerFactory.getItems(this.player);
    }

    @Override
    public boolean kick(@NonNull final Component reason) {
        return this.getPlugin().getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.playerFactory.kick(this.player, reason));
    }

    @Override
    public void sendMessage(@NonNull final Component message) {
        this.playerFactory.sendMessage(this.player, message);
    }

    @Override
    public void sendMessage(@NonNull final Iterable<Component> messages) {
        this.playerFactory.sendMessage(this.player, messages);
    }

    @Override
    public void performCommand(final String command) {
        this.playerFactory.performCommand(this.player, command);
    }

    @Override
    public int getFreeInventorySpace() {
        return this.playerFactory.getFreeInventorySpace(this.player);
    }

    @Override
    public boolean isCreative() {
        return this.playerFactory.isCreative(this.player);
    }

    @Override
    public void openContainer(@NonNull final Container container) {
        this.getPlugin().getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.playerFactory.openContainer(this.player, container));
    }

    @Override
    public void playSound(@NonNull final Sound sound) {
        this.playerFactory.playSound(this.player, sound);
    }

    @Override
    public void addItem(@NonNull final ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return;
        }

        this.getPlugin().getBootstrap().getScheduler()
                .executeSyncBlocking(() -> this.playerFactory.addItem(this.player, itemStack));
    }

    @Override
    public boolean teleport(@NonNull final World world, final double x, final double y,
            final double z, final float yRot, final float xRot) {
        return this.getPlugin().getBootstrap().getScheduler().executeSyncBlocking(
                () -> this.playerFactory.teleport(this.player, world, x, y, z, yRot, xRot));
    }

    @Override
    public boolean hasPermission(final @NonNull String permission) {
        return this.getPlugin().getPermissionHandler()
                .hasPermission(this.getUniqueId(), permission);
    }

    @Override
    public boolean isPartOfGroup(@NonNull final String name) {
        return this.getPlugin().getPermissionHandler().isPartOfGroup(this.getUniqueId(), name);
    }

    @Override
    public @NonNull Collection<String> getGroups() {
        return this.getPlugin().getPermissionHandler().getGroups(this.getUniqueId());
    }
}
