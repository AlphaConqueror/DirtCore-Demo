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

package net.dirtcraft.dirtcore.forge_1_7_10.util;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeUtils {

    public static final char IDENTIFIER_SEPARATOR = ':';
    private static final SimpleCommandExceptionType ERROR_INVALID_METADATA =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid metadata"));

    @NonNull
    public static String getWorldName(@NonNull final World world) {
        return world.getSaveHandler().getWorldDirectoryName();
    }

    @NonNull
    public static NBTTagCompound parseForNBTTagCompound(
            @NonNull final StringReader reader) throws CommandSyntaxException {
        final NBTBase tag = parseForTag(reader);

        if (!(tag instanceof NBTTagCompound)) {
            // tag is not a compound tag
            throw new SimpleCommandExceptionType(
                    new LiteralMessage("Tag is not a compound tag")).createWithContext(reader);
        }

        return (NBTTagCompound) tag;
    }

    @NonNull
    public static NBTBase parseForTag(
            @NonNull final StringReader reader) throws CommandSyntaxException {
        try {
            return JsonToNBT.func_150315_a(reader.getRemaining());
        } catch (final Exception e) {
            // tag could not be parsed
            throw new SimpleCommandExceptionType(new LiteralMessage("Could not parse tag: %s",
                    e.getMessage())).createWithContext(reader);
        }
    }

    @NonNull
    public static String readIdentifier(
            @NonNull final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();

        while (reader.canRead() && isAllowedInIdentifier(reader.peek())) {
            reader.skip();
        }

        reader.expect(IDENTIFIER_SEPARATOR);

        while (reader.canRead() && isAllowedInIdentifier(reader.peek())) {
            reader.skip();
        }

        return reader.getString().substring(start, reader.getCursor());
    }

    public static int readTag(@NonNull final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();

        while (reader.canRead() && isAllowedInNumber(reader.peek())) {
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

            while (reader.canRead() && isAllowedInNumber(reader.peek())) {
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

    private static boolean isAllowedInIdentifier(final char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_'
                || c == '-' || c == '.' || c == '+';
    }

    private static boolean isAllowedInNumber(final char c) {
        return c >= '0' && c <= '9';
    }
}
