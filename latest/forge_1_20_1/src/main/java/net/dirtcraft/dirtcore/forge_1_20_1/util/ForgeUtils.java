/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.util;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class ForgeUtils {

    private static final SimpleCommandExceptionType ERROR_INVALID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));

    public static ResourceLocation read(final StringReader pReader) throws CommandSyntaxException {
        final int i = pReader.getCursor();

        while (pReader.canRead() && ResourceLocation.isAllowedInResourceLocation(pReader.peek())) {
            pReader.skip();
        }

        final String s = pReader.getString().substring(i, pReader.getCursor());

        try {
            return new ResourceLocation(s);
        } catch (final ResourceLocationException resourcelocationexception) {
            pReader.setCursor(i);
            throw ERROR_INVALID.createWithContext(pReader);
        }
    }
}
