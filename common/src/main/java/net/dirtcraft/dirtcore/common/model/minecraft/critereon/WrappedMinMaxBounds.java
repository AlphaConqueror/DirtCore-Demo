/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import org.checkerframework.checker.nullness.qual.Nullable;

public class WrappedMinMaxBounds {

    public static final WrappedMinMaxBounds ANY = new WrappedMinMaxBounds(null, null);
    public static final SimpleCommandExceptionType ERROR_INTS_ONLY = new SimpleCommandExceptionType(
            new LiteralMessage("Only whole numbers allowed, not decimals"));
    @Nullable
    private final Float min;
    @Nullable
    private final Float max;

    public WrappedMinMaxBounds(@Nullable final Float pMin, @Nullable final Float pMax) {
        this.min = pMin;
        this.max = pMax;
    }

    public static WrappedMinMaxBounds exactly(final float pValue) {
        return new WrappedMinMaxBounds(pValue, pValue);
    }

    public static WrappedMinMaxBounds between(final float pMin, final float pMax) {
        return new WrappedMinMaxBounds(pMin, pMax);
    }

    public static WrappedMinMaxBounds atLeast(final float pMin) {
        return new WrappedMinMaxBounds(pMin, null);
    }

    public static WrappedMinMaxBounds atMost(final float pMax) {
        return new WrappedMinMaxBounds(null, pMax);
    }

    public static WrappedMinMaxBounds fromReader(final StringReader pReader,
            final boolean pIsFloatingPoint) throws CommandSyntaxException {
        return fromReader(pReader, pIsFloatingPoint, f -> f);
    }

    public static WrappedMinMaxBounds fromReader(final StringReader pReader,
            final boolean pIsFloatingPoint,
            final Function<Float, Float> pValueFactory) throws CommandSyntaxException {
        if (!pReader.canRead()) {
            throw MinMaxBounds.ERROR_EMPTY.createWithContext(pReader);
        } else {
            final int i = pReader.getCursor();
            final Float f = optionallyFormat(readNumber(pReader, pIsFloatingPoint), pValueFactory);
            final Float f1;
            if (pReader.canRead(2) && pReader.peek() == '.' && pReader.peek(1) == '.') {
                pReader.skip();
                pReader.skip();
                f1 = optionallyFormat(readNumber(pReader, pIsFloatingPoint), pValueFactory);
                if (f == null && f1 == null) {
                    pReader.setCursor(i);
                    throw MinMaxBounds.ERROR_EMPTY.createWithContext(pReader);
                }
            } else {
                if (!pIsFloatingPoint && pReader.canRead() && pReader.peek() == '.') {
                    pReader.setCursor(i);
                    throw ERROR_INTS_ONLY.createWithContext(pReader);
                }

                f1 = f;
            }

            if (f == null && f1 == null) {
                pReader.setCursor(i);
                throw MinMaxBounds.ERROR_EMPTY.createWithContext(pReader);
            } else {
                return new WrappedMinMaxBounds(f, f1);
            }
        }
    }

    @Nullable
    private static Float readNumber(final StringReader pReader,
            final boolean pIsFloatingPoint) throws CommandSyntaxException {
        final int i = pReader.getCursor();

        while (pReader.canRead() && isAllowedNumber(pReader, pIsFloatingPoint)) {
            pReader.skip();
        }

        final String s = pReader.getString().substring(i, pReader.getCursor());
        if (s.isEmpty()) {
            return null;
        } else {
            try {
                return Float.parseFloat(s);
            } catch (final NumberFormatException numberformatexception) {
                if (pIsFloatingPoint) {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble()
                            .createWithContext(pReader, s);
                } else {
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt()
                            .createWithContext(pReader, s);
                }
            }
        }
    }

    private static boolean isAllowedNumber(final StringReader pReader,
            final boolean pIsFloatingPoint) {
        final char c0 = pReader.peek();
        if ((c0 < '0' || c0 > '9') && c0 != '-') {
            if (pIsFloatingPoint && c0 == '.') {
                return !pReader.canRead(2) || pReader.peek(1) != '.';
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @Nullable
    private static Float optionallyFormat(@Nullable final Float pValue,
            final Function<Float, Float> pValueFactory) {
        return pValue == null ? null : pValueFactory.apply(pValue);
    }

    public boolean matches(final float pValue) {
        if (this.min != null && this.max != null && this.min > this.max && this.min > pValue
                && this.max < pValue) {
            return false;
        } else if (this.min != null && this.min > pValue) {
            return false;
        } else {
            return this.max == null || !(this.max < pValue);
        }
    }

    public boolean matchesSqr(final double pValue) {
        if (this.min != null && this.max != null && this.min > this.max
                && (double) (this.min * this.min) > pValue
                && (double) (this.max * this.max) < pValue) {
            return false;
        } else if (this.min != null && (double) (this.min * this.min) > pValue) {
            return false;
        } else {
            return this.max == null || !((double) (this.max * this.max) < pValue);
        }
    }

    @Nullable
    public Float getMin() {
        return this.min;
    }

    @Nullable
    public Float getMax() {
        return this.max;
    }

    public JsonElement serializeToJson() {
        if (this == ANY) {
            return JsonNull.INSTANCE;
        } else if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        } else {
            final JsonObject jsonobject = new JsonObject();
            if (this.min != null) {
                jsonobject.addProperty("min", this.min);
            }

            if (this.max != null) {
                jsonobject.addProperty("max", this.min);
            }

            return jsonobject;
        }
    }
}
