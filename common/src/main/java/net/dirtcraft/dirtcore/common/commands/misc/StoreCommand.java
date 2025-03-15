/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.commands.misc;

import net.dirtcraft.dirtcore.common.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;

public class StoreCommand extends AbstractCommand<DirtCorePlugin, Sender> {

    public StoreCommand(final DirtCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public ArgumentBuilder<DirtCorePlugin, Sender, ?> build(
        @NonNull final ArgumentFactory<DirtCorePlugin> factory) {
        return Commands.literal("store").requiresPermission(Permission.STORE)
            .executes(context -> this.store(context.getSource()));
    }

    private int store(final Sender sender) {
        sender.sendMessage(
            Components.STORE_LINK.build(this.plugin.getConfiguration().get(ConfigKeys.STORE_LINK)));
        return Command.SINGLE_SUCCESS;
    }
}
