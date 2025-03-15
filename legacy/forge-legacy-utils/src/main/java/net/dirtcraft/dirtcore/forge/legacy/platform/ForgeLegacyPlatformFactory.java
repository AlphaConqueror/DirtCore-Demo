/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge.legacy.platform;

import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public interface ForgeLegacyPlatformFactory<E, P, C, I, W, B> extends PlatformFactory<E, P, C, I,
        W, B> {

    GsonComponentSerializer GSON_COMPONENT_SERIALIZER_LEGACY =
            GsonComponentSerializer.colorDownsamplingGson();
    LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
            LegacyComponentSerializer.builder().character(LegacyComponentSerializer.SECTION_CHAR)
                    .build();

    @Override
    default ClickEvent.Action copyEventAction() {
        return ClickEvent.Action.SUGGEST_COMMAND;
    }
}
