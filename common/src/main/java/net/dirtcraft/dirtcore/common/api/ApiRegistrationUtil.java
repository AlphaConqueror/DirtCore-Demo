/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.api;

import java.lang.reflect.Method;
import net.dirtcraft.dirtcore.api.DirtCore;
import net.dirtcraft.dirtcore.api.DirtCoreProvider;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

public class ApiRegistrationUtil {

    private static final Method REGISTER;
    private static final Method UNREGISTER;

    static {
        try {
            REGISTER = DirtCoreProvider.class.getDeclaredMethod("register", DirtCore.class);
            REGISTER.setAccessible(true);

            UNREGISTER = DirtCoreProvider.class.getDeclaredMethod("unregister");
            UNREGISTER.setAccessible(true);
        } catch (final NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void registerProvider(final DirtCorePlugin plugin, final DirtCore dirtCoreApi) {
        try {
            REGISTER.invoke(null, dirtCoreApi);
        } catch (final Exception e) {
            plugin.getLogger().severe("Caught exception while registering API provider.", e);
        }
    }

    public static void unregisterProvider(final DirtCorePlugin plugin) {
        try {
            UNREGISTER.invoke(null);
        } catch (final Exception e) {
            plugin.getLogger().severe("Caught exception while unregistering API provider.", e);
        }
    }

}
