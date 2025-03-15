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

package net.dirtcraft.dirtcore.api.messaging;

import net.dirtcraft.dirtcore.api.DirtCore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A means to send messages to other servers using the platforms networking
 */
public interface MessagingService {

    /**
     * Gets the name of this messaging service
     *
     * @return the name of this messaging service
     */
    @NonNull String getName();

    /**
     * Uses the messaging service to inform other servers about a general
     * change.
     *
     * <p>The standard response by other servers will be to execute a overall
     * sync of all live data, equivalent to calling
     * {@link DirtCore#runUpdateTask()}.</p>
     *
     * <p>This will push the update asynchronously, and this method will return
     * immediately. Note that this method will not cause an update to be
     * processed on the local server.</p>
     */
    void pushUpdate();
}
