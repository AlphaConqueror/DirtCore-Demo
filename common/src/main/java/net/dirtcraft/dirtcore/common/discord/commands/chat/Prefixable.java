/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.commands.chat;

import java.util.Locale;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

interface Prefixable {

    @NonNull
    default String parseName(@NonNull final String name) {
        return name.replace(' ', '_').toLowerCase(Locale.ROOT);
    }

    default boolean checkDisplayGuarded(@NonNull final String display) {
        if (display.length() < 2) {
            return true;
        }

        final String raw = MessagingManager.MINIMESSAGE.stripTags(display).trim();

        if (raw.length() < 2) {
            return true;
        }

        return raw.charAt(0) != '[' || raw.charAt(raw.length() - 1) != ']';
    }

    default boolean appendCondition(@NonNull final StringBuilder builder,
            @Nullable final Object object, @NonNull final Object original,
            @NonNull final String title) {
        if (object != null && !object.equals(original)) {
            builder.append('\n')
                    .append(MarkdownUtil.bold(title + ": "))
                    .append(MarkdownUtil.monospace(original.toString()))
                    .append(" -> ")
                    .append(MarkdownUtil.monospace(object.toString()));
            return true;
        }

        return false;
    }
}
