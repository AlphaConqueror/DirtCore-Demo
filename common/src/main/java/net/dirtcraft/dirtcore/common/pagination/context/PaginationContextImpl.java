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

public class PaginationContextImpl implements PaginationContext {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final Sender sender;
    @Nullable
    private TaskContext context;

    protected PaginationContextImpl(@NonNull final DirtCorePlugin plugin,
            @NonNull final Sender sender) {
        this.plugin = plugin;
        this.sender = sender;
    }

    protected PaginationContextImpl(@NonNull final DirtCorePlugin plugin,
            @NonNull final Sender sender, @Nullable final TaskContext context) {
        this.plugin = plugin;
        this.sender = sender;
        this.context = context;
    }


    @Override
    public @NonNull DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public @NonNull Sender getSender() {
        return this.sender;
    }

    @Override
    public @Nullable TaskContext getTaskContext() {
        return this.context;
    }

    @Override
    public @NonNull PaginationContext withTaskContext(@NonNull final TaskContext context) {
        this.context = context;
        return this;
    }

    @Override
    public @NonNull TaskContext getTaskContextOrException() {
        if (this.context == null) {
            throw new AssertionError();
        }

        return this.context;
    }
}
