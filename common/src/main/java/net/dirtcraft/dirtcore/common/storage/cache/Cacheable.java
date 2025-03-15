/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A class which manages instances of a class.
 *
 * @param <I> the class used to identify each object held in this manager
 * @param <T> the implementation class this manager is "managing"
 */
public interface Cacheable<I, T> {

    /**
     * Get a map containing all cached instances held by this manager.
     *
     * @return all instances held in this manager
     */
    @NonNull Map<I, T> getAll();

    /**
     * Get an object by id.
     *
     * @param id The id to search by
     * @return a {@link T} object if the object is loaded, returns Optional.empty() if not
     */
    @NonNull Optional<T> getIfLoaded(I id);

    /**
     * Updates the object in the manager.
     *
     * @param t The object to update.
     */
    void update(@NonNull T t);

    /**
     * Updates the collection of object in the manager.
     *
     * @param collection The collection of objects to update.
     */
    void updateAll(@NonNull Collection<T> collection);

    /**
     * Check to see if an object is loaded or not
     *
     * @param id The id of the object
     * @return true if the object is loaded
     */
    boolean isLoaded(@NonNull I id);

    /**
     * Removes and unloads the object from the manager
     *
     * @param id The object id to unload
     */
    void unload(@NonNull I id);

    /**
     * Removes and unloads all objects from the manager
     */
    void unloadAll();

    /**
     * Calls {@link #unload(Object)} for all objects currently
     * loaded not in the given collection of ids.
     *
     * @param ids the ids to retain
     */
    void retainAll(@NonNull Collection<I> ids);
}
