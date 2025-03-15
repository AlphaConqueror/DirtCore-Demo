/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.context;

import java.util.Objects;

public class ParsedArgument<T> {

    private final StringRange range;
    private final T result;

    public ParsedArgument(final int start, final int end, final T result) {
        this.range = StringRange.between(start, end);
        this.result = result;
    }

    public StringRange getRange() {
        return this.range;
    }

    public T getResult() {
        return this.result;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.range, this.result);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParsedArgument)) {
            return false;
        }
        final ParsedArgument<?> that = (ParsedArgument<?>) o;
        return Objects.equals(this.range, that.range) && Objects.equals(this.result, that.result);
    }

    @Override
    public String toString() {
        return "ParsedArgument{" + "range=" + this.range + ", result=" + this.result + '}';
    }
}
