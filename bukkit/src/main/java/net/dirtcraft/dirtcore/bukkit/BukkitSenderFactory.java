/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit;

import java.util.UUID;
import net.dirtcraft.dirtcore.common.platform.sender.Sender;
import net.dirtcraft.dirtcore.common.platform.sender.SenderFactory;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

public class BukkitSenderFactory extends SenderFactory<DirtCoreBukkitPlugin, CommandSender> {


    public BukkitSenderFactory(final DirtCoreBukkitPlugin plugin) {
        super(plugin);
    }

    @Override
    public UUID getUniqueId(final CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        }

        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName(final CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
        }

        return Sender.CONSOLE_NAME;
    }

    @Override
    public void sendMessage(final CommandSender sender, final Component message) {
        sender.spigot()
                .sendMessage(this.getPlugin().getPlatformFactory().transformComponent(message));
    }

    @Override
    public boolean hasPermission(final CommandSender sender, final String node) {
        return this.getPlugin().getPermissionHandler().hasPermission(sender, node);
    }

    @Override
    public boolean isConsole(final CommandSender sender) {
        return sender instanceof ConsoleCommandSender
                || sender instanceof RemoteConsoleCommandSender;
    }
}
