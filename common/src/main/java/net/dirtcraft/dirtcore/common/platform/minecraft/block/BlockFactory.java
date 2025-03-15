/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.block;

import java.util.Objects;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Factory class to make a thread-safe block instance.
 *
 * @param <P> the plugin type
 * @param <B> the block type
 */
public abstract class BlockFactory<P extends DirtCorePlugin, B> {

    private final P plugin;

    public BlockFactory(final P plugin) {this.plugin = plugin;}

    @NonNull
    protected abstract String getIdentifier(@NonNull B block);

    @NonNull
    protected abstract String getMod(@NonNull B block);

    protected abstract boolean isEmpty(@NonNull B block);

    protected abstract boolean hasPersistentData(@NonNull B block);

    @Nullable
    protected abstract String getPersistentDataAsString(@NonNull B block);

    protected abstract boolean persistentDataMatches(@NonNull B block, @Nullable final String s);

    protected abstract boolean persistentDataPartiallyMatches(@NonNull B block, @NonNull String s);

    public final Block wrap(final B block) {
        Objects.requireNonNull(block, "block");
        return new AbstractBlock<>(this, block);
    }

    protected P getPlugin() {
        return this.plugin;
    }
}
