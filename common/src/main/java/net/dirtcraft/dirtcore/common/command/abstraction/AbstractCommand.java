/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction;

import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractCommand<P extends DirtCorePlugin, S extends Sender> {

    protected final P plugin;

    protected AbstractCommand(final P plugin) {this.plugin = plugin;}

    public abstract ArgumentBuilder<P, S, ?> build(@NonNull ArgumentFactory<P> factory);
}
