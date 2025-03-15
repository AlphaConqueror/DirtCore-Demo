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

public class FloatArgumentType implements ArgumentType<DirtCorePlugin, Float> {

    private static final Collection<String> EXAMPLES =
            Arrays.asList("0", "1.2", ".5", "-1", "-.5", "-1234.56");

    private final float minimum;
    private final float maximum;

    private FloatArgumentType(final float minimum, final float maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static FloatArgumentType floatArg() {
        return floatArg(-Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min) {
        return floatArg(min, Float.MAX_VALUE);
    }

    public static FloatArgumentType floatArg(final float min, final float max) {
        return new FloatArgumentType(min, max);
    }

    public static float getFloat(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, Float.class);
    }

    public float getMinimum() {
        return this.minimum;
    }

    public float getMaximum() {
        return this.maximum;
    }

    @Override
    public @NonNull Float parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final float result = reader.readFloat();

        if (result < this.minimum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow()
                    .createWithContext(reader, result, this.minimum);
        }

        if (result > this.maximum) {
            reader.setCursor(start);
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh()
                    .createWithContext(reader, result, this.maximum);
        }

        return result;
    }

    @Override
    public @NonNull String getName() {
        return "float";
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
        if (!(o instanceof FloatArgumentType)) {
            return false;
        }

        final FloatArgumentType that = (FloatArgumentType) o;
        return this.maximum == that.maximum && this.minimum == that.minimum;
    }

    @Override
    public String toString() {
        if (this.minimum == -Float.MAX_VALUE && this.maximum == Float.MAX_VALUE) {
            return "float()";
        } else if (this.maximum == Float.MAX_VALUE) {
            return "float(" + this.minimum + ")";
        } else {
            return "float(" + this.minimum + ", " + this.maximum + ")";
        }
    }
}
