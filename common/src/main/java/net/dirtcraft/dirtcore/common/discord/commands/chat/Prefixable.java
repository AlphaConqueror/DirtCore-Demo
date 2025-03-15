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
