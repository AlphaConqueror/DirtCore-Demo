/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.platform.minecraft.block;

import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple implementation of {@link Block} using a {@link BlockFactory}.
 *
 * @param <B> the block type
 */
public final class AbstractBlock<B> implements Block {

    private final BlockFactory<?, B> factory;
    private final B block;

    public AbstractBlock(final BlockFactory<?, B> factory, final B block) {
        this.factory = factory;
        this.block = block;
    }

    @Override
    public @NonNull String getMod() {
        return this.factory.getMod(this.block);
    }

    @Override
    public @Nullable String getPersistentDataAsString() {
        return this.factory.getPersistentDataAsString(this.block);
    }

    @Override
    public boolean persistentDataMatches(@Nullable final String s) {
        return this.factory.persistentDataMatches(this.block, s);
    }

    @Override
    public boolean persistentDataPartiallyMatches(@NonNull final String s) {
        return this.factory.persistentDataPartiallyMatches(this.block, s);
    }

    @Override
    public @NonNull String getIdentifier() {
        return this.factory.getIdentifier(this.block);
    }

    @Override
    public boolean isEmpty() {
        return this.factory.isEmpty(this.block);
    }

    @Override
    public boolean hasPersistentData() {
        return this.factory.hasPersistentData(this.block);
    }
}
