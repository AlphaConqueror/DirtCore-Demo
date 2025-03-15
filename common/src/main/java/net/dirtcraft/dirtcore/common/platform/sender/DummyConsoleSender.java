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
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Dummy implementation of {@link Sender}.
 */
public abstract class DummyConsoleSender implements Sender {

    private final DirtCorePlugin plugin;

    public DummyConsoleSender(final DirtCorePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(final @NonNull String permission) {
        return true;
    }

    @Override
    public boolean isPartOfGroup(@NonNull final String name) {
        return false;
    }

    @Override
    public @NonNull Collection<String> getGroups() {
        return Collections.emptySet();
    }

    @Override
    public @NonNull DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public boolean isEntity() {
        return false;
    }

    @Override
    public @NonNull Optional<Entity> getEntity() {
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<World> getWorld() {
        return Optional.empty();
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return CONSOLE_UUID;
    }

    @Override
    public @NonNull String getName() {
        return CONSOLE_NAME;
    }

    @Override
    public @NonNull Collection<TextCoordinates> getRelevantCoordinates() {
        return Collections.singleton(TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<TextCoordinates> getAbsoluteCoordinates() {
        return Collections.singleton(TextCoordinates.DEFAULT_GLOBAL);
    }

    @Override
    public @NonNull Collection<String> getSelectedEntities() {
        return Collections.emptySet();
    }
}
