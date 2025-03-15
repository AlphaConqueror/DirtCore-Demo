/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.permission;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.permission.AbstractVanillaPermissionHandler;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeVanillaPermissionHandler extends AbstractVanillaPermissionHandler<CommandSourceStack> {

    private static final Field MAP_FIELD;

    static {
        try {
            MAP_FIELD = StoredUserList.class.getDeclaredField("map");
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
                final Map<String, ServerOpListEntry> map =
                        (Map<String, ServerOpListEntry>) MAP_FIELD.get(
                                this.plugin.getBootstrap().getServer().get().getPlayerList()
                                        .getOps());

                return map.containsKey(uniqueId.toString());
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }).orElse(false);
    }

    @Override
    protected @NonNull UUID getUniqueId(final CommandSourceStack sender) {
        return sender.source instanceof ServerPlayer ? ((ServerPlayer) sender.source).getUUID()
                : Sender.CONSOLE_UUID;
    }
}
