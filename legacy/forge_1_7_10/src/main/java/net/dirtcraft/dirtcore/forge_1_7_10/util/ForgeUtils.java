/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
