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

package net.dirtcraft.dirtcore.common.platform.argument;

import java.util.Collection;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.AbstractItemArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe platform dependant arguments.
 *
 * @param <P> the plugin type
 */
public abstract class ArgumentFactory<P extends DirtCorePlugin> {

    private final P plugin;

    protected ArgumentFactory(final P plugin) {
        this.plugin = plugin;
    }

    @NonNull
    public abstract AbstractBlockArgument<P> block();

    @NonNull
    public abstract AbstractEntityArgument<P> entity();

    @NonNull
    public abstract AbstractEntityArgument<P> entities();

    @NonNull
    public abstract AbstractItemArgument<P> item();

    @NonNull
    public abstract AbstractPersistentDataArgument<P> persistentData();

    @NonNull
    public abstract AbstractEntityArgument<P> player();

    @NonNull
    public abstract AbstractEntityArgument<P> players();

    @NonNull
    public abstract AbstractWorldArgument<P> world();

    @NonNull
    public BlockResult getBlock(final CommandContext<P, Sender> context, final String name) {
        return context.getArgument(name, BlockResult.class);
    }

    @NonNull
    public Entity getEntity(final CommandContext<P, Sender> context, final String name) {
        return AbstractEntityArgument.getEntity(context, name);
    }

    @NonNull
    public Collection<? extends Entity> getEntities(final CommandContext<P, Sender> context,
            final String name) {
        return AbstractEntityArgument.getEntities(context, name);
    }

    @NonNull
    public Collection<? extends Entity> getOptionalEntities(final CommandContext<P, Sender> context,
            final String name) {
        return AbstractEntityArgument.getOptionalEntities(context, name);
    }

    @NonNull
    public ItemResult getItem(final CommandContext<P, Sender> context, final String name) {
        return context.getArgument(name, ItemResult.class);
    }

    @NonNull
    public String getPersistentData(final CommandContext<P, Sender> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @NonNull
    public Player getPlayer(final CommandContext<P, Sender> context, final String name) {
        return AbstractEntityArgument.getPlayer(context, name);
    }

    @NonNull
    public Collection<Player> getPlayers(final CommandContext<P, Sender> context,
            final String name) {
        return AbstractEntityArgument.getPlayers(context, name);
    }

    @NonNull
    public Collection<Player> getOptionalPlayers(final CommandContext<P, Sender> context,
            final String name) {
        return AbstractEntityArgument.getOptionalPlayers(context, name);
    }

    @NonNull
    public World getWorld(final CommandContext<P, Sender> context, final String name) {
        return context.getArgument(name, World.class);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
