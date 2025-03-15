/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.loader.event;

import java.util.List;
import java.util.UUID;

/**
 * An event dispatcher used in the loader.
 *
 * @param <B> the block type
 * @param <I> the item stack type
 * @param <W> the world type
 */
public interface LoaderEventDispatcher<B, I, W> {

    /**
     * Dispatches a block change event.
     *
     * @param oldBlock the old block
     * @param newBlock the new block
     * @param flags    the flags
     * @param world    the world
     * @param x        the x coordinate
     * @param y        the y coordinate
     * @param z        the z coordinate
     */
    void dispatchBlockChangeEvent(B oldBlock, B newBlock, int flags, W world, int x, int y, int z);

    /**
     * Dispatches a block push reaction event.
     *
     * @param block the block
     * @return true, if the event should be cancelled
     */
    boolean dispatchBlockPushReactionEvent(B block);

    /**
     * Dispatches a player inventory click.
     *
     * @param uniqueId the unique id
     * @param username the username
     * @param list     the list of items interacted with
     * @return true, if the event should be cancelled
     */
    boolean dispatchPlayerInventoryClick(UUID uniqueId, String username, List<I> list);
}
