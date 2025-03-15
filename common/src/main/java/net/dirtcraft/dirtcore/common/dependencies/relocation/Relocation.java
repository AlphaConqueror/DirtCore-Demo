/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.dependencies.relocation;

import java.util.Objects;

public final class Relocation {

    private static final String RELOCATION_PREFIX = "net.dirtcraft.dirtcore.lib.";
    private final String pattern;
    private final String relocatedPattern;

    private Relocation(final String pattern, final String relocatedPattern) {
        this.pattern = pattern;
        this.relocatedPattern = relocatedPattern;
    }

    public static Relocation of(final String id, final String pattern) {
        return new Relocation(pattern.replace("{}", "."), RELOCATION_PREFIX + id);
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getRelocatedPattern() {
        return this.relocatedPattern;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.pattern, this.relocatedPattern);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final Relocation that = (Relocation) o;
        return Objects.equals(this.pattern, that.pattern) && Objects.equals(this.relocatedPattern,
                that.relocatedPattern);
    }
}
