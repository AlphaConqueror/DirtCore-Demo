/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.permission;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.permission.AbstractVanillaPermissionHandler;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeVanillaPermissionHandler extends AbstractVanillaPermissionHandler<CommandSourceStack> {

    private static final Field MAP_FIELD;

    static {
        try {
            MAP_FIELD = StoredUserList.class.getDeclaredField("map");
            MAP_FIELD.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private final DirtCoreNeoForgePlugin plugin;

    public NeoForgeVanillaPermissionHandler(final DirtCoreNeoForgePlugin plugin) {
        this.plugin = plugin;
    }

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
