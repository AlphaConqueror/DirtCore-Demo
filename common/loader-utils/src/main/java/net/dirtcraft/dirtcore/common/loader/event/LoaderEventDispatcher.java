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
