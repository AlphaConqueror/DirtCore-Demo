/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.pagination.context;

import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PaginationContext {

    static PaginationContext of(@NonNull final DirtCorePlugin plugin,
            @NonNull final Sender sender) {
        return new PaginationContextImpl(plugin, sender);
    }

    static PaginationContext of(@NonNull final DirtCorePlugin plugin, @NonNull final Sender sender,
            @Nullable final TaskContext context) {
        return new PaginationContextImpl(plugin, sender, context);
    }

    @NonNull DirtCorePlugin getPlugin();

    @NonNull Sender getSender();

    // TODO: Maybe move to local context instead.

    @Nullable TaskContext getTaskContext();

    @NonNull PaginationContext withTaskContext(@NonNull TaskContext context);

    @NonNull TaskContext getTaskContextOrException();
}
