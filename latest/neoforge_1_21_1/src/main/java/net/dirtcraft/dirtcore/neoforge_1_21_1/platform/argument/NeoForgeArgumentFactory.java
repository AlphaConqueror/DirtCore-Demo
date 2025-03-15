/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
