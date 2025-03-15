/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.entity;

import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.entity.selector.ForgeEntitySelectorParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEntityArgument extends AbstractEntityArgument<DirtCoreForgePlugin> {

    private final DirtCoreForgePlugin plugin;

    protected ForgeEntityArgument(final DirtCoreForgePlugin plugin, final boolean single,
            final boolean playersOnly) {
        super(single, playersOnly);
        this.plugin = plugin;
    }

    public static ForgeEntityArgument entity(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, true, false);
    }

    public static ForgeEntityArgument entities(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, false, false);
    }

    public static ForgeEntityArgument player(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, true, true);
    }

    public static ForgeEntityArgument players(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, false, true);
    }

    @Override
    protected @NonNull ForgeEntitySelectorParser provideEntitySelectorParser(
            @NonNull final StringReader reader, final boolean allowSelectors, final boolean single,
            final boolean playersOnly) {
        return new ForgeEntitySelectorParser(this.plugin, reader, allowSelectors, single,
                playersOnly);
    }
}
