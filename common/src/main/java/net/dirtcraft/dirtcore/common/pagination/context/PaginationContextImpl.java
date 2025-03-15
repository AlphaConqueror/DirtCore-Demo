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
