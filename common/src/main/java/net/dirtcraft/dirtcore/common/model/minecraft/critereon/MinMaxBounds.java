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

package net.dirtcraft.dirtcore.common.model.minecraft.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class MinMaxBounds<T extends Number> {

    public static final SimpleCommandExceptionType ERROR_EMPTY =
            new SimpleCommandExceptionType(new LiteralMessage("Expected value or range of values"));
    public static final SimpleCommandExceptionType ERROR_SWAPPED =
            new SimpleCommandExceptionType(new LiteralMessage("Min cannot be bigger than max"));
    @Nullable
    protected final T min;
    @Nullable
    protected final T max;

    protected MinMaxBounds(@Nullable final T min, @Nullable final T max) {
        this.min = min;
        this.max = max;
    }

    protected static <T extends Number, R extends MinMaxBounds<T>> R fromReader(
            final StringReader reader,
            final MinMaxBounds.BoundsFromReaderFactory<T, R> boundedFactory,
            final Function<String, T> valueFactory,
            final Supplier<DynamicCommandExceptionType> commandExceptionSupplier,
            final Function<T, T> formatter) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_EMPTY.createWithContext(reader);
        }

        final int i = reader.getCursor();

        try {
            final T t = optionallyFormat(readNumber(reader, valueFactory, commandExceptionSupplier),
                    formatter);
            final T t1;

            if (reader.canRead(2) && reader.peek() == '.' && reader.peek(1) == '.') {
                reader.skip();
                reader.skip();
                t1 = optionallyFormat(readNumber(reader, valueFactory, commandExceptionSupplier),
                        formatter);
                if (t == null && t1 == null) {
                    throw ERROR_EMPTY.createWithContext(reader);
                }
            } else {
                t1 = t;
            }

            if (t == null && t1 == null) {
                throw ERROR_EMPTY.createWithContext(reader);
            }

            return boundedFactory.create(reader, t, t1);
        } catch (final CommandSyntaxException e) {
            reader.setCursor(i);
            throw new CommandSyntaxException(e.getType(), e.getRawMessage(), e.getInput(), i);
        }
    }

    @Nullable
    private static <T extends Number> T readNumber(final StringReader reader,
            final Function<String, T> pStringToValueFunction,
            final Supplier<DynamicCommandExceptionType> commandExceptionSupplier) throws CommandSyntaxException {
        final int i = reader.getCursor();

        while (reader.canRead() && isAllowedInputChat(reader)) {
            reader.skip();
        }

        final String s = reader.getString().substring(i, reader.getCursor());

        if (s.isEmpty()) {
            return null;
        }

        try {
            return pStringToValueFunction.apply(s);
        } catch (final NumberFormatException e) {
            throw commandExceptionSupplier.get().createWithContext(reader, s);
        }
    }

    private static boolean isAllowedInputChat(final StringReader reader) {
        final char c0 = reader.peek();
        if ((c0 < '0' || c0 > '9') && c0 != '-') {
            if (c0 != '.') {
                return false;
            }

            return !reader.canRead(2) || reader.peek(1) != '.';
        }

        return true;
    }

    @Nullable
    private static <T> T optionallyFormat(@Nullable final T value, final Function<T, T> formatter) {
        return value == null ? null : formatter.apply(value);
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    public boolean isAny() {
        return this.min == null && this.max == null;
    }

    public JsonElement serializeToJson() {
        if (this.isAny()) {
            return JsonNull.INSTANCE;
        }

        if (this.min != null && this.min.equals(this.max)) {
            return new JsonPrimitive(this.min);
        }

        final JsonObject jsonobject = new JsonObject();

        if (this.min != null) {
            jsonobject.addProperty("min", this.min);
        }

        if (this.max != null) {
            jsonobject.addProperty("max", this.max);
        }

        return jsonobject;
    }

    public static class Doubles extends MinMaxBounds<Double> {

        public static final MinMaxBounds.Doubles ANY = new MinMaxBounds.Doubles(null, null);
        @Nullable
        private final Double minSq;
        @Nullable
        private final Double maxSq;

        private Doubles(@Nullable final Double min, @Nullable final Double max) {
            super(min, max);
            this.minSq = squareOpt(min);
            this.maxSq = squareOpt(max);
        }

        private static MinMaxBounds.Doubles create(final StringReader reader,
                @Nullable final Double min,
                @Nullable final Double max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw ERROR_SWAPPED.createWithContext(reader);
            }
            return new MinMaxBounds.Doubles(min, max);
        }

        @Nullable
        private static Double squareOpt(@Nullable final Double value) {
            return value == null ? null : value * value;
        }

        public static MinMaxBounds.Doubles exactly(final double value) {
            return new MinMaxBounds.Doubles(value, value);
        }

        public static MinMaxBounds.Doubles between(final double min, final double max) {
            return new MinMaxBounds.Doubles(min, max);
        }

        public static MinMaxBounds.Doubles atLeast(final double min) {
            return new MinMaxBounds.Doubles(min, null);
        }

        public static MinMaxBounds.Doubles atMost(final double max) {
            return new MinMaxBounds.Doubles(null, max);
        }

        public static MinMaxBounds.Doubles fromReader(
                final StringReader reader) throws CommandSyntaxException {
            return fromReader(reader, d -> d);
        }

        public static MinMaxBounds.Doubles fromReader(final StringReader reader,
                final Function<Double, Double> formatter) throws CommandSyntaxException {
            return fromReader(reader, MinMaxBounds.Doubles::create, Double::parseDouble,
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidDouble, formatter);
        }

        public boolean matches(final double value) {
            if (this.min != null && this.min > value) {
                return false;
            }

            return this.max == null || !(this.max < value);
        }

        public boolean matchesSqr(final double value) {
            if (this.minSq != null && this.minSq > value) {
                return false;
            }

            return this.maxSq == null || !(this.maxSq < value);
        }
    }

    public static class Integers extends MinMaxBounds<Integer> {

        public static final Integers ANY = new Integers(null, null);
        @Nullable
        private final Long minSq;
        @Nullable
        private final Long maxSq;

        private Integers(@Nullable final Integer min, @Nullable final Integer max) {
            super(min, max);
            this.minSq = squareOpt(min);
            this.maxSq = squareOpt(max);
        }

        private static Integers create(final StringReader reader, @Nullable final Integer min,
                @Nullable final Integer max) throws CommandSyntaxException {
            if (min != null && max != null && min > max) {
                throw ERROR_SWAPPED.createWithContext(reader);
            }

            return new Integers(min, max);
        }

        @Nullable
        private static Long squareOpt(@Nullable final Integer value) {
            return value == null ? null : value.longValue() * value.longValue();
        }

        public static Integers exactly(final int value) {
            return new Integers(value, value);
        }

        public static Integers between(final int min, final int max) {
            return new Integers(min, max);
        }

        public static Integers atLeast(final int min) {
            return new Integers(min, null);
        }

        public static Integers atMost(final int max) {
            return new Integers(null, max);
        }

        public static Integers fromReader(final StringReader reader) throws CommandSyntaxException {
            return fromReader(reader, i -> i);
        }

        public static Integers fromReader(final StringReader reader,
                final Function<Integer, Integer> valueFunction) throws CommandSyntaxException {
            return fromReader(reader, Integers::create, Integer::parseInt,
                    CommandSyntaxException.BUILT_IN_EXCEPTIONS::readerInvalidInt, valueFunction);
        }

        public boolean matches(final int value) {
            if (this.min != null && this.min > value) {
                return false;
            }

            return this.max == null || this.max >= value;
        }

        public boolean matchesSqr(final long value) {
            if (this.minSq != null && this.minSq > value) {
                return false;
            }

            return this.maxSq == null || this.maxSq >= value;
        }
    }

    @FunctionalInterface
    protected interface BoundsFactory<T extends Number, R extends MinMaxBounds<T>> {

        R create(@Nullable T min, @Nullable T max);
    }

    @FunctionalInterface
    protected interface BoundsFromReaderFactory<T extends Number, R extends MinMaxBounds<T>> {

        R create(StringReader reader, @Nullable T min,
                @Nullable T max) throws CommandSyntaxException;
    }
}
