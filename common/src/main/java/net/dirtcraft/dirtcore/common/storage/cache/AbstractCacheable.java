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

package net.dirtcraft.dirtcore.common.storage.cache;

import com.google.common.collect.ImmutableMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An abstract cacheable class.
 *
 * @param <I> the class used to identify each object held in this manager
 * @param <T> the implementation class this manager is "managing"
 */
public abstract class AbstractCacheable<I, T> implements Cacheable<I, T> {

    private final Map<I, T> objects = new ConcurrentHashMap<>();

    /**
     * Gets the key of the object.
     *
     * @param t the object
     * @return the key
     */
    protected abstract @NonNull I getKey(@NonNull T t);

    @Override
    public @NonNull Map<I, T> getAll() {
        return ImmutableMap.copyOf(this.objects);
    }

    @Override
    public @NonNull Optional<T> getIfLoaded(@NonNull final I id) {
        return Optional.ofNullable(this.objects.get(id));
    }

    @Override
    public void update(@NonNull final T t) {
        this.objects.put(this.getKey(t), t);
    }

    @Override
    public void updateAll(@NonNull final Collection<T> collection) {
        collection.forEach(this::update);
    }

    @Override
    public boolean isLoaded(@NonNull final I id) {
        return this.objects.containsKey(id);
    }

    @Override
    public void unload(@NonNull final I id) {
        this.objects.remove(id);
    }

    @Override
    public void unloadAll() {
        this.objects.clear();
    }

    @Override
    public void retainAll(@NonNull final Collection<I> ids) {
        this.objects.keySet().stream().filter(g -> !ids.contains(g)).forEach(this::unload);
    }
}
