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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.block.ForgeBlockArgument;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.entity.ForgeEntityArgument;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.item.ForgeItemArgument;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.nbt.ForgeNBTArgument;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.world.ForgeWorldArgument;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeArgumentFactory extends ArgumentFactory<DirtCoreForgePlugin> {

    public ForgeArgumentFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    public @NonNull AbstractBlockArgument<DirtCoreForgePlugin> block() {
        return ForgeBlockArgument.block();
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreForgePlugin> entity() {
        return ForgeEntityArgument.entity(this.getPlugin());
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreForgePlugin> entities() {
        return ForgeEntityArgument.entities(this.getPlugin());
    }

    @Override
    public @NonNull ForgeItemArgument item() {
        return ForgeItemArgument.item();
    }

    @Override
    public @NonNull AbstractPersistentDataArgument<DirtCoreForgePlugin> persistentData() {
        return ForgeNBTArgument.nbt();
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreForgePlugin> player() {
        return ForgeEntityArgument.player(this.getPlugin());
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreForgePlugin> players() {
        return ForgeEntityArgument.players(this.getPlugin());
    }

    @Override
    public @NonNull AbstractWorldArgument<DirtCoreForgePlugin> world() {
        return ForgeWorldArgument.world();
    }
}
