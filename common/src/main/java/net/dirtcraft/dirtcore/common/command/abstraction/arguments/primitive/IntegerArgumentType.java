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

public class IntegerArgumentType implements ArgumentType<DirtCorePlugin, Integer> {

    private static final Collection<String> EXAMPLES = Arrays.asList("0", "123", "-123");

    private final int minimum;
    private final int maximum;

    private IntegerArgumentType(final int minimum, final int maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static IntegerArgumentType integer() {
        return integer(Integer.MIN_VALUE);
    }

    public static IntegerArgumentType integer(final int min) {
        return integer(min, Integer.MAX_VALUE);
    }

    public static IntegerArgumentType integer(final int min, final int max) {
        return new IntegerArgumentType(min, max);
    }

    public static int getInteger(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, int.class);
    }

    public int getMinimum() {
        return this.minimum;
    }

    public int getMaximum() {
        return this.maximum;
    }

    @Override
    public @NonNull Integer parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final int result = reader.readInt();

        if (result < this.minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow()
                    .createWithContext(reader, result, this.minimum);
        }

        if (result > this.maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh()
                    .createWithContext(reader, result, this.maximum);
        }

        return result;
    }

    @Override
    public @NonNull String getName() {
        return "int";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public int hashCode() {
        return 31 * this.minimum + this.maximum;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegerArgumentType)) {
            return false;
        }

        final IntegerArgumentType that = (IntegerArgumentType) o;
        return this.maximum == that.maximum && this.minimum == that.minimum;
    }

    @Override
    public String toString() {
        if (this.minimum == Integer.MIN_VALUE && this.maximum == Integer.MAX_VALUE) {
            return "integer()";
        } else if (this.maximum == Integer.MAX_VALUE) {
            return "integer(" + this.minimum + ")";
        } else {
            return "integer(" + this.minimum + ", " + this.maximum + ")";
        }
    }
}
