/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft;

import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.Limitable;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a block within the minecraft block/block state implementations.
 */
public interface Block extends Limitable, ItemInfoProvider {

    /**
     * Checks if this block has persistent data.
     *
     * @return true, if this block has persistent data
     */
    boolean hasPersistentData();

    @Override
    @NonNull
    default String asString(final boolean includePersistentData) {
        final StringBuilder builder = new StringBuilder(this.getIdentifier());

        if (includePersistentData && this.hasPersistentData()) {
            builder.append(this.getPersistentDataAsString());
        }

        return builder.toString();
    }
}
