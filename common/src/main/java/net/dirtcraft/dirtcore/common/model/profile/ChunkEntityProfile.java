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

package net.dirtcraft.dirtcore.common.model.profile;

import java.util.Map;
import net.dirtcraft.dirtcore.common.commands.teleport.TeleportChunkCommand;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChunkEntityProfile extends EntityProfile {

    private static final TextColor KEY_COLOR = NamedTextColor.DARK_AQUA;
    private static final TextColor VALUE_COLOR = NamedTextColor.AQUA;

    @NonNull
    private final String worldName;
    private final int chunkX;
    private final int chunkZ;

    public ChunkEntityProfile(@NonNull final Map<String, Integer> map, final int totalEntities,
            @NonNull final String worldName, final int chunkX, final int chunkZ) {
        super(map, totalEntities);
        this.worldName = worldName;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
    }

    @NonNull
    public static ChunkEntityProfile of(@NonNull final Map<String, Integer> map,
            final int totalEntities, @NonNull final String worldName, final int chunkX,
            final int chunkZ) {
        return new ChunkEntityProfile(map, totalEntities, worldName, chunkX, chunkZ);
    }

    @NonNull
    public Component asComponent(@NonNull final Sender sender) {
        final TextComponent.Builder summaryBuilder = Component.text()
                .append(this.summaryAsComponent());
        final ClickEvent clickEvent;

        if (sender.hasPermission(Permission.TELEPORT)) {
            summaryBuilder.appendNewline().appendNewline()
                    .append(Component.text("Click to teleport to this chunk.",
                            NamedTextColor.DARK_GRAY));
            clickEvent = ClickEvent.runCommand(
                    TeleportChunkCommand.COMMAND_TELEPORT_CHUNK.apply(this.worldName, this.chunkX,
                            this.chunkZ));
        } else {
            clickEvent = null;
        }

        final TextComponent.Builder totalBuilder = Component.text()
                .append(Component.text("World: ", KEY_COLOR))
                .append(Component.text(this.worldName, VALUE_COLOR)).appendSpace()
                .append(Component.text("x: ", KEY_COLOR))
                .append(Component.text(this.chunkX, VALUE_COLOR)).appendSpace()
                .append(Component.text("z: ", KEY_COLOR))
                .append(Component.text(this.chunkZ, VALUE_COLOR)).appendSpace()
                .append(Component.text("Entities: ", KEY_COLOR))
                .append(Component.text(this.getTotalEntities(), VALUE_COLOR))
                .hoverEvent(HoverEvent.showText(summaryBuilder));

        if (clickEvent != null) {
            totalBuilder.clickEvent(clickEvent);
        }

        if (sender.hasPermission(Permission.ENTITY_ZAP_CHUNK)) {
            // totalBuilder.appendSpace()
            //         .append(Components.KILL.build().hoverEvent(HoverEvent.showText(
            //                 Component.text("Click to zap all entities in this chunk.",
            //                         NamedTextColor.RED))).clickEvent(ClickEvent.runCommand(
            //                 EntityZapCommand.COMMAND_ENTITY_ZAP_CHUNK.apply(this.worldName,
            //                         this.chunkX, this.chunkZ))));
        }

        return totalBuilder.build();
    }
}
