/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.permission;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.permission.AbstractVanillaPermissionHandler;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.management.UserList;
import net.minecraft.server.management.UserListOpsEntry;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeVanillaPermissionHandler extends AbstractVanillaPermissionHandler<ICommandSender> {

    /**
     * {@link UserList#values}
     */
    private static final Field MAP_FIELD;

    static {
        try {
            //noinspection JavaReflectionMemberAccess
            MAP_FIELD = UserList.class.getDeclaredField("field_152696_d");
            MAP_FIELD.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final DirtCoreForgePlugin plugin;

    public ForgeVanillaPermissionHandler(final DirtCoreForgePlugin plugin) {this.plugin = plugin;}

    @Override
    protected boolean isConsole(final UUID uniqueId) {
        return uniqueId.equals(Sender.CONSOLE_UUID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean isOp(final UUID uniqueId) {
        return this.plugin.getBootstrap().getServer().map(server -> {
            try {
                final Map<String, UserListOpsEntry> map =
                        (Map<String, UserListOpsEntry>) MAP_FIELD.get(
                                server.getConfigurationManager().getOppedPlayers());

                return map.containsKey(uniqueId.toString());
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).orElse(false);
    }

    @Override
    protected @NonNull UUID getUniqueId(final ICommandSender sender) {
        return sender instanceof EntityPlayer ? ((EntityPlayer) sender).getUniqueID()
                : Sender.CONSOLE_UUID;
    }
}
