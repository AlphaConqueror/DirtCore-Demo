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

public class DoubleArgumentType implements ArgumentType<DirtCorePlugin, Double> {

    private static final Collection<String> EXAMPLES =
            Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

    private final double minimum;
    private final double maximum;

    private DoubleArgumentType(final double minimum, final double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static DoubleArgumentType doubleArg() {
        return doubleArg(-Double.MAX_VALUE);
    }

    public static DoubleArgumentType doubleArg(final double min) {
        return doubleArg(min, Double.MAX_VALUE);
    }

    public static DoubleArgumentType doubleArg(final double min, final double max) {
        return new DoubleArgumentType(min, max);
    }

    public static double getDouble(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, Double.class);
    }

    public double getMinimum() {
        return this.minimum;
    }

    public double getMaximum() {
        return this.maximum;
    }

    @Override
    public @NonNull Double parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final double result = reader.readDouble();

        if (result < this.minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow()
                    .createWithContext(reader, result, this.minimum);
        }

        if (result > this.maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh()
                    .createWithContext(reader, result, this.maximum);
        }

        return result;
    }

    @Override
    public @NonNull String getName() {
        return "double";
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public int hashCode() {
        return (int) (31 * this.minimum + this.maximum);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DoubleArgumentType)) {
            return false;
        }

        final DoubleArgumentType that = (DoubleArgumentType) o;
        return this.maximum == that.maximum && this.minimum == that.minimum;
    }

    @Override
    public String toString() {
        if (this.minimum == -Double.MAX_VALUE && this.maximum == Double.MAX_VALUE) {
            return "double()";
        } else if (this.maximum == Double.MAX_VALUE) {
            return "double(" + this.minimum + ")";
        } else {
            return "double(" + this.minimum + ", " + this.maximum + ")";
        }
    }
}
