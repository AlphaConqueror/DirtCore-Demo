/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.dependencies;

import java.util.Set;
import net.dirtcraft.storageutils.StorageType;

/**
 * Loads and manages runtime dependencies for the plugin.
 */
public interface DependencyManager extends AutoCloseable {

    /**
     * Loads dependencies.
     *
     * @param dependencies the dependencies to load
     */
    void loadDependencies(Set<Dependency> dependencies);

    /**
     * Loads storage dependencies.
     *
     * @param storageTypes the storage types in use
     */
    void loadStorageDependencies(Set<StorageType> storageTypes);

    /**
     * Obtains an isolated classloader containing the given dependencies.
     *
     * @param dependencies the dependencies
     * @return the classloader
     */
    ClassLoader obtainClassLoaderWith(Set<Dependency> dependencies);

    @Override
    void close();
}
