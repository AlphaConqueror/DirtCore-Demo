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

package net.dirtcraft.dirtcore.common.platform.sender;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Factory class to make a thread-safe sender instance.
 *
 * @param <P> the plugin type
 * @param <T> the command sender type
 */
public abstract class SenderFactory<P extends DirtCorePlugin, T> implements AutoCloseable {

    private final P plugin;

    public SenderFactory(final P plugin) {
        this.plugin = plugin;
    }

    public abstract UUID getUniqueId(@NonNull T sender);

    public abstract String getName(@NonNull T sender);

    public abstract void sendMessage(@NonNull T sender, @NonNull Component message);

    public abstract void sendMessage(@NonNull T sender, @NonNull Iterable<Component> messages);

    public abstract boolean isConsole(@NonNull T sender);

    public abstract boolean isEntity(@NonNull T sender);

    @NonNull
    public abstract Optional<Entity> getEntity(@NonNull T sender);

    @NonNull
    public abstract Optional<World> getWorld(@NonNull T sender);

    @NonNull
    public abstract Collection<SharedSuggestionProvider.TextCoordinates> getRelevantCoordinates(
            @NonNull T sender);

    @NonNull
    public abstract Collection<SharedSuggestionProvider.TextCoordinates> getAbsoluteCoordinates(
            @NonNull T sender);

    @NonNull
    public abstract Collection<String> getSelectedEntities(@NonNull T sender);

    public Sender wrap(@NonNull final T sender) {
        Objects.requireNonNull(sender, "sender");
        return new AbstractSender<>(this.plugin, this, sender);
    }

    @Override
    public void close() {}

    protected P getPlugin() {
        return this.plugin;
    }
}
