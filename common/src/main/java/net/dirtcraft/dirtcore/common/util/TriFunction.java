/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

@FunctionalInterface
public interface TriFunction<A0, A1, A2, R> {

    R apply(A0 a0, A1 a1, A2 a2);
}
