/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.plugin.classpath;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Provides access to {@link URLClassLoader}#addURL.
 */
public abstract class URLClassLoaderAccess {

    private final URLClassLoader classLoader;

    protected URLClassLoaderAccess(final URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Creates a {@link URLClassLoaderAccess} for the given class loader.
     *
     * @param classLoader the class loader
     * @return the access object
     */
    public static URLClassLoaderAccess create(final URLClassLoader classLoader) {
        if (Reflection.isSupported()) {
            return new Reflection(classLoader);
        }

        if (Unsafe.isSupported()) {
            return new Unsafe(classLoader);
        }

        return Noop.INSTANCE;
    }

    private static void throwError(final Throwable cause) throws UnsupportedOperationException {
        throw new UnsupportedOperationException(
                "DirtCore is unable to inject into the plugin URLClassLoader.\n"
                        + "You may be able to fix this problem by adding the following "
                        + "command-line argument "
                        + "directly after the 'java' command in your start script: \n'--add-opens"
                        + " java.base/java.lang=ALL-UNNAMED'", cause);
    }

    /**
     * Adds the given URL to the class loader.
     *
     * @param url the URL to add
     */
    public abstract void addURL(@NonNull URL url);

    /**
     * Accesses using reflection, not supported on Java 9+.
     */
    private static class Reflection extends URLClassLoaderAccess {

        private static final Method ADD_URL_METHOD;

        static {
            Method addUrlMethod;

            try {
                addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlMethod.setAccessible(true);
            } catch (final Exception e) {
                addUrlMethod = null;
            }

            ADD_URL_METHOD = addUrlMethod;
        }

        Reflection(final URLClassLoader classLoader) {
            super(classLoader);
        }

        private static boolean isSupported() {
            return ADD_URL_METHOD != null;
        }

        @Override
        public void addURL(@NonNull final URL url) {
            try {
                ADD_URL_METHOD.invoke(super.classLoader, url);
            } catch (final ReflectiveOperationException e) {
                URLClassLoaderAccess.throwError(e);
            }
        }
    }

    /**
     * Accesses using sun.misc.Unsafe, supported on Java 9+.
     *
     * @author Vaishnav Anil (<a href="https://github.com/slimjar/slimjar">...</a>)
     */
    private static class Unsafe extends URLClassLoaderAccess {

        private static final sun.misc.Unsafe UNSAFE;

        static {
            sun.misc.Unsafe unsafe;

            try {
                final Field unsafeField = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (sun.misc.Unsafe) unsafeField.get(null);
            } catch (final Throwable t) {
                unsafe = null;
            }

            UNSAFE = unsafe;
        }

        private final Collection<URL> unopenedURLs;
        private final Collection<URL> pathURLs;

        @SuppressWarnings("unchecked")
        Unsafe(final URLClassLoader classLoader) {
            super(classLoader);

            Collection<URL> unopenedURLs;
            Collection<URL> pathURLs;
            try {
                final Object ucp = fetchField(URLClassLoader.class, classLoader, "ucp");
                unopenedURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "unopenedUrls");
                pathURLs = (Collection<URL>) fetchField(ucp.getClass(), ucp, "path");
            } catch (final Throwable e) {
                unopenedURLs = null;
                pathURLs = null;
            }

            this.unopenedURLs = unopenedURLs;
            this.pathURLs = pathURLs;
        }

        private static boolean isSupported() {
            return UNSAFE != null;
        }

        private static Object fetchField(final Class<?> clazz, final Object object,
                final String name) throws NoSuchFieldException {
            final Field field = clazz.getDeclaredField(name);
            final long offset = UNSAFE.objectFieldOffset(field);
            return UNSAFE.getObject(object, offset);
        }

        @Override
        public void addURL(@NonNull final URL url) {
            if (this.unopenedURLs == null || this.pathURLs == null) {
                URLClassLoaderAccess.throwError(
                        new NullPointerException("unopenedURLs or pathURLs"));
            }

            synchronized (this.unopenedURLs) {
                this.unopenedURLs.add(url);
                this.pathURLs.add(url);
            }
        }
    }

    private static class Noop extends URLClassLoaderAccess {

        private static final Noop INSTANCE = new Noop();

        private Noop() {
            super(null);
        }

        @Override
        public void addURL(@NonNull final URL url) {
            URLClassLoaderAccess.throwError(null);
        }
    }
}
