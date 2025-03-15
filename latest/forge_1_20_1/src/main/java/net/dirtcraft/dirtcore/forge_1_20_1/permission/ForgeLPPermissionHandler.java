/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.permission;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.permission.AbstractLPPermissionHandler;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeLPPermissionHandler extends AbstractLPPermissionHandler<CommandSourceStack> {

    private final DirtCoreForgePlugin plugin;

    public ForgeLPPermissionHandler(final DirtCoreForgePlugin plugin) {this.plugin = plugin;}

    @Override
    protected @NonNull UUID getUUID(final CommandSourceStack sender) {
        return sender.source instanceof Player ? ((Player) sender.source).getUUID()
                : Sender.CONSOLE_UUID;
    }

    @Override
    protected boolean isConsole(final CommandSourceStack sender) {
        return !(sender.source instanceof Player);
    }
}
