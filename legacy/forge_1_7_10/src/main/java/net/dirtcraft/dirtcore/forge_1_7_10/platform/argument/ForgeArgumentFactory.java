/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.platform.argument;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.AbstractBlockArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.persistentdata.AbstractPersistentDataArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.block.ForgeBlockArgument;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.entity.ForgeEntityArgument;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.item.ForgeItemArgument;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.nbt.ForgeNBTArgument;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.world.ForgeWorldArgument;
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
