/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

public interface Throwing {

    @FunctionalInterface
    interface Runnable {

        void run() throws Exception;
    }

    @FunctionalInterface
    interface Consumer<T> {

        void accept(T t) throws Exception;
    }
}
