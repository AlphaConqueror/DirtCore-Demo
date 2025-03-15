/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
