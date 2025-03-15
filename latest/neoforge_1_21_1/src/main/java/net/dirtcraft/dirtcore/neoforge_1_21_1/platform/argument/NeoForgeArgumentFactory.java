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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.block.NeoForgeBlockArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.datacomponent.NeoForgeDataComponentPatchArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.NeoForgeEntityArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.item.NeoForgeItemArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.world.NeoForgeWorldArgument;
import net.minecraft.commands.CommandBuildContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeArgumentFactory extends ArgumentFactory<DirtCoreNeoForgePlugin> {

    private final CommandBuildContext commandBuildContext;

    public NeoForgeArgumentFactory(final DirtCoreNeoForgePlugin plugin,
            final CommandBuildContext commandBuildContext) {
        super(plugin);
        this.commandBuildContext = commandBuildContext;
    }

    @Override
    public @NonNull AbstractBlockArgument<DirtCoreNeoForgePlugin> block() {
        return NeoForgeBlockArgument.block(this.commandBuildContext);
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreNeoForgePlugin> entity() {
        return NeoForgeEntityArgument.entity(this.getPlugin());
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreNeoForgePlugin> entities() {
        return NeoForgeEntityArgument.entities(this.getPlugin());
    }

    @Override
    public @NonNull NeoForgeItemArgument item() {
        return NeoForgeItemArgument.item(this.commandBuildContext);
    }

    @Override
    public @NonNull AbstractPersistentDataArgument<DirtCoreNeoForgePlugin> persistentData() {
        return NeoForgeDataComponentPatchArgument.dataComponentPatch(this.commandBuildContext);
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreNeoForgePlugin> player() {
        return NeoForgeEntityArgument.player(this.getPlugin());
    }

    @Override
    public @NonNull AbstractEntityArgument<DirtCoreNeoForgePlugin> players() {
        return NeoForgeEntityArgument.players(this.getPlugin());
    }

    @Override
    public @NonNull AbstractWorldArgument<DirtCoreNeoForgePlugin> world() {
        return NeoForgeWorldArgument.world();
    }
}
