/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive;

import java.util.Arrays;
import java.util.Collection;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LongArgumentType implements ArgumentType<DirtCorePlugin, Long> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final long minimum;
    private final long maximum;

    private LongArgumentType(final long minimum, final long maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static LongArgumentType longArg() {
        return longArg(Long.MIN_VALUE);
    }

    public static LongArgumentType longArg(final long min) {
        return longArg(min, Long.MAX_VALUE);
    }

    public static LongArgumentType longArg(final long min, final long max) {
        return new LongArgumentType(min, max);
    }

    public static long getLong(final CommandContext<DirtCorePlugin, ?> context, final String name) {
        return context.getArgument(name, long.class);
    }

    public long getMinimum() {
        return this.minimum;
    }

    public long getMaximum() {
        return this.maximum;
    }

    @Override
    public @NonNull Long parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final long result = reader.readLong();

        if (result < this.minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow()
                    .createWithContext(reader, result, this.minimum);
        }

        if (result > this.maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh()
                    .createWithContext(reader, result, this.maximum);
        }

        return result;
    }

    @Override
    public @NonNull String getName() {
        return "long";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(this.minimum) + Long.hashCode(this.maximum);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LongArgumentType)) {
            return false;
        }

        final LongArgumentType that = (LongArgumentType) o;
        return this.maximum == that.maximum && this.minimum == that.minimum;
    }

    @Override
    public String toString() {
        if (this.minimum == Long.MIN_VALUE && this.maximum == Long.MAX_VALUE) {
            return "longArg()";
        } else if (this.maximum == Long.MAX_VALUE) {
            return "longArg(" + this.minimum + ")";
        } else {
            return "longArg(" + this.minimum + ", " + this.maximum + ")";
        }
    }
}
