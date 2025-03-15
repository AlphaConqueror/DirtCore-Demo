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

package net.dirtcraft.dirtcore.forge_1_12_2.util;

import javax.annotation.Nullable;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeUtils {

    public static final char IDENTIFIER_SEPARATOR = ':';
    private static final SimpleCommandExceptionType ERROR_INVALID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));
    private static final SimpleCommandExceptionType ERROR_INVALID_METADATA =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid metadata"));

    @NonNull
    public static String getWorldName(@NonNull final World world) {
        return world.getSaveHandler().getWorldDirectory().getName();
    }

    @NonNull
    public static NBTTagCompound parseForTag(
            @NonNull final StringReader reader) throws CommandSyntaxException {
        try {
            return JsonToNBT.getTagFromJson(reader.getRemaining());
        } catch (final Exception e) {
            // tag could not be parsed
            throw new SimpleCommandExceptionType(new LiteralMessage("Could not parse tag: %s",
                    e.getMessage())).createWithContext(reader);
        }
    }

    @NonNull
    public static ResourceLocation read(final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        while (reader.canRead() && isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        final String s = reader.getString().substring(i, reader.getCursor());

        try {
            return new ResourceLocation(s);
        } catch (final Exception ignored) {
            reader.setCursor(i);
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    private static boolean isAllowedInResourceLocation(final char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/'
                || c == '.' || c == '-';
    }

    public static int readTag(@NonNull final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();

        while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
            reader.skip();
        }

        final int tag;

        try {
            tag = Integer.parseInt(reader.getString().substring(start, reader.getCursor()));
        } catch (final NumberFormatException ignored) {
            reader.setCursor(start);
            throw ERROR_INVALID_METADATA.createWithContext(reader);
        }

        return tag;
    }

    public static int readMetadata(
            @NonNull final StringReader reader) throws CommandSyntaxException {
        int metadata = 0;

        if (reader.canRead() && reader.peek() == IDENTIFIER_SEPARATOR) {
            reader.skip();

            final int start = reader.getCursor();

            while (reader.canRead() && StringReader.isAllowedNumber(reader.peek())) {
                reader.skip();
            }

            try {
                metadata =
                        Integer.parseInt(reader.getString().substring(start, reader.getCursor()));
            } catch (final NumberFormatException ignored) {
                reader.setCursor(start);
                throw ERROR_INVALID_METADATA.createWithContext(reader);
            }
        }

        return metadata;
    }

    @Nullable
    public static ResourceLocation tryParse(@NonNull final String location) {
        return tryBySeparator(location, ':');
    }

    @Nullable
    public static ResourceLocation tryBySeparator(final String location, final char separator) {
        final int i = location.indexOf(separator);

        if (i >= 0) {
            final String s = location.substring(i + 1);

            if (!isValidPath(s)) {
                return null;
            }

            if (i != 0) {
                final String s1 = location.substring(0, i);
                return isValidNamespace(s1) ? new ResourceLocation(s1, s) : null;
            }

            return new ResourceLocation("minecraft", s);
        }

        return isValidPath(location) ? new ResourceLocation("minecraft", location) : null;
    }

    public static boolean isValidPath(final String path) {
        for (int i = 0; i < path.length(); i++) {
            if (!validPathChar(path.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isValidNamespace(final String namespace) {
        for (int i = 0; i < namespace.length(); i++) {
            if (!validNamespaceChar(namespace.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean validPathChar(final char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '/'
                || c == '.';
    }

    public static boolean validNamespaceChar(final char c) {
        return c == '_' || c == '-' || c >= 'a' && c <= 'z' || c >= '0' && c <= '9' || c == '.';
    }
}
