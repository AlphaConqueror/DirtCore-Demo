/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
