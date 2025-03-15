/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;

public class WorldCoordinate {

    public static final SimpleCommandExceptionType ERROR_EXPECTED_DOUBLE =
            new SimpleCommandExceptionType(new LiteralMessage("Expected a coordinate"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_INT =
            new SimpleCommandExceptionType(new LiteralMessage("Expected a block position"));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_INT_CHUNK =
            new SimpleCommandExceptionType(new LiteralMessage("Expected a chunk position"));
    private static final char PREFIX_RELATIVE = '~';
    private final boolean relative;
    private final double value;

    public WorldCoordinate(final boolean pRelative, final double pValue) {
        this.relative = pRelative;
        this.value = pValue;
    }

    public static WorldCoordinate parseDouble(final StringReader pReader,
            final boolean pCenterCorrect) throws CommandSyntaxException {
        if (pReader.canRead() && pReader.peek() == LocalCoordinates.PREFIX_LOCAL_COORDINATE) {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(pReader);
        }

        if (!pReader.canRead()) {
            throw ERROR_EXPECTED_DOUBLE.createWithContext(pReader);
        }

        final boolean flag = isRelative(pReader);
        final int i = pReader.getCursor();
        double d0 = pReader.canRead() && pReader.peek() != ' ' ? pReader.readDouble() : 0.0D;
        final String s = pReader.getString().substring(i, pReader.getCursor());

        if (flag && s.isEmpty()) {
            return new WorldCoordinate(true, 0.0D);
        }

        if (!s.contains(".") && !flag && pCenterCorrect) {
            d0 += 0.5D;
        }

        return new WorldCoordinate(flag, d0);
    }

    public static WorldCoordinate parseIntForBlock(
            final StringReader pReader) throws CommandSyntaxException {
        return parseInt(pReader, ERROR_EXPECTED_INT);
    }

    public static WorldCoordinate parseIntForChunk(
            final StringReader pReader) throws CommandSyntaxException {
        return parseInt(pReader, ERROR_EXPECTED_INT_CHUNK);
    }

    public static WorldCoordinate parseInt(final StringReader pReader,
            final SimpleCommandExceptionType exceptionType) throws CommandSyntaxException {
        if (pReader.canRead() && pReader.peek() == LocalCoordinates.PREFIX_LOCAL_COORDINATE) {
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(pReader);
        }

        if (!pReader.canRead()) {
            throw exceptionType.createWithContext(pReader);
        }

        final boolean flag = isRelative(pReader);
        final double d0;

        if (pReader.canRead() && pReader.peek() != ' ') {
            d0 = flag ? pReader.readDouble() : (double) pReader.readInt();
        } else {
            d0 = 0.0D;
        }

        return new WorldCoordinate(flag, d0);
    }

    public static boolean isRelative(final StringReader pReader) {
        final boolean flag;
        if (pReader.peek() == PREFIX_RELATIVE) {
            flag = true;
            pReader.skip();
        } else {
            flag = false;
        }

        return flag;
    }

    public double get(final double pCoord) {
        return this.relative ? this.value + pCoord : this.value;
    }

    @Override
    public int hashCode() {
        final int i = this.relative ? 1 : 0;
        final long j = Double.doubleToLongBits(this.value);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof WorldCoordinate)) {
            return false;
        } else {
            final WorldCoordinate worldcoordinate = (WorldCoordinate) pOther;
            if (this.relative != worldcoordinate.relative) {
                return false;
            } else {
                return Double.compare(worldcoordinate.value, this.value) == 0;
            }
        }
    }

    public boolean isRelative() {
        return this.relative;
    }
}
