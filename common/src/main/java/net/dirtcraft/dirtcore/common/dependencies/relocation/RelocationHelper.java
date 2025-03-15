/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.dependencies.relocation;

public final class RelocationHelper {

    // screw maven shade
    public static final String OKIO_STRING = String.valueOf(new char[] {'o', 'k', 'i', 'o'});
    public static final String OKIO_JVM_STRING =
            String.valueOf(new char[] {'o', 'k', 'i', 'o', '-', 'j', 'v', 'm'});
    public static final String OKHTTP3_STRING =
            String.valueOf(new char[] {'o', 'k', 'h', 't', 't', 'p', '3'});

    private RelocationHelper() {}
}
