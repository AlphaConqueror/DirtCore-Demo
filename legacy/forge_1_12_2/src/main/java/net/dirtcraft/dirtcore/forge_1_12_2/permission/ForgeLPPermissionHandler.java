/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.permission;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.permission.AbstractLPPermissionHandler;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeLPPermissionHandler extends AbstractLPPermissionHandler<ICommandSender> {

    @Override
    protected @NonNull UUID getUUID(final ICommandSender sender) {
        return sender instanceof EntityPlayer ? ((EntityPlayer) sender).getUniqueID()
                : Sender.CONSOLE_UUID;
    }

    @Override
    protected boolean isConsole(final ICommandSender sender) {
        return !(sender instanceof EntityPlayer);
    }
}
