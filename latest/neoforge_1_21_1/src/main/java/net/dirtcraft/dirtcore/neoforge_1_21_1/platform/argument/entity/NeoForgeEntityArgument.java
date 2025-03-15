/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity;

import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.selector.NeoForgeEntitySelectorParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeEntityArgument extends AbstractEntityArgument<DirtCoreNeoForgePlugin> {

    private final DirtCoreNeoForgePlugin plugin;

    protected NeoForgeEntityArgument(final DirtCoreNeoForgePlugin plugin, final boolean single,
            final boolean playersOnly) {
        super(single, playersOnly);
        this.plugin = plugin;
    }

    public static NeoForgeEntityArgument entity(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, true, false);
    }

    public static NeoForgeEntityArgument entities(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, false, false);
    }

    public static NeoForgeEntityArgument player(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, true, true);
    }

    public static NeoForgeEntityArgument players(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, false, true);
    }

    @Override
    protected @NonNull NeoForgeEntitySelectorParser provideEntitySelectorParser(
            @NonNull final StringReader reader, final boolean allowSelectors, final boolean single,
            final boolean playersOnly) {
        return new NeoForgeEntitySelectorParser(this.plugin, reader, allowSelectors, single,
                playersOnly);
    }
}
