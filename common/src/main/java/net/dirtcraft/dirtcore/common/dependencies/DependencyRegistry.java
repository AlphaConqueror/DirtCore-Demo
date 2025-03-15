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

package net.dirtcraft.dirtcore.common.dependencies;

import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.gson.JsonElement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.dirtcraft.dirtcore.common.dependencies.relocation.Relocation;
import net.dirtcraft.dirtcore.common.dependencies.relocation.RelocationHandler;
import net.dirtcraft.storageutils.StorageType;

/**
 * Applies DirtCore specific behaviour for {@link Dependency}s.
 */
public class DependencyRegistry {

    private static final SetMultimap<StorageType, Dependency> STORAGE_DEPENDENCIES =
            ImmutableSetMultimap.<StorageType, Dependency>builder()
                    .putAll(StorageType.MARIADB, Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE,
                            Dependency.HIKARI, Dependency.MARIADB_DRIVER)
                    .putAll(StorageType.MYSQL, Dependency.SLF4J_API, Dependency.SLF4J_SIMPLE,
                            Dependency.HIKARI, Dependency.MYSQL_DRIVER).build();

    public DependencyRegistry() {}

    @SuppressWarnings("ConstantConditions")
    public static boolean isGsonRelocated() {
        return JsonElement.class.getName().startsWith("net.dirtcraft");
    }

    private static boolean classExists(final String className) {
        try {
            Class.forName(className);
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean slf4jPresent() {
        return classExists("org.slf4j.Logger") && classExists("org.slf4j.LoggerFactory");
    }

    public Set<Dependency> resolveStorageDependencies(final Set<StorageType> storageTypes) {
        final Set<Dependency> dependencies = new LinkedHashSet<>();

        for (final StorageType storageType : storageTypes) {
            dependencies.addAll(STORAGE_DEPENDENCIES.get(storageType));
        }

        dependencies.add(Dependency.JAVAX_PERSISTENCE);
        dependencies.add(Dependency.HIBERNATE_CORE);
        dependencies.add(Dependency.HIBERNATE_COMMONS_ANNOTATIONS);
        dependencies.add(Dependency.ANTLR);
        dependencies.add(Dependency.FASTERXML_CLASSMATE);
        dependencies.add(Dependency.DOM4J);
        dependencies.add(Dependency.JAVASSIST);
        dependencies.add(Dependency.GERONIMO);
        dependencies.add(Dependency.JAKARTA_XML_BIND);
        dependencies.add(Dependency.JBOSS_LOGGING);

        // don't load slf4j if it's already present
        if ((dependencies.contains(Dependency.SLF4J_API) || dependencies.contains(
                Dependency.SLF4J_SIMPLE)) && slf4jPresent()) {
            dependencies.remove(Dependency.SLF4J_API);
            dependencies.remove(Dependency.SLF4J_SIMPLE);
        }

        return dependencies;
    }

    public void applyRelocationSettings(final Dependency dependency,
            final List<Relocation> relocations) {
        // support for DirtCore legacy (bukkit 1.7.10)
        if (!RelocationHandler.DEPENDENCIES.contains(dependency) && isGsonRelocated()) {
            relocations.add(Relocation.of("guava", "com{}google{}common"));
            relocations.add(Relocation.of("gson", "com{}google{}gson"));
        }
    }

    public boolean shouldAutoLoad(final Dependency dependency) {
        switch (dependency) {
            // all used within 'isolated' classloaders, and are therefore not
            // relocated.
            case ASM:
            case ASM_COMMONS:
            case JAR_RELOCATOR:
                return false;
            default:
                return true;
        }
    }
}
