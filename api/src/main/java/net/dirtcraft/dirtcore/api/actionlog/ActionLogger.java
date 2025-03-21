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

package net.dirtcraft.dirtcore.api.actionlog;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.api.messaging.MessagingService;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents the object responsible for handling action logging.
 */
public interface ActionLogger {

    /**
     * Returns a new {@link Action.Builder} instance
     *
     * @param timestamp     the timestamp
     * @param source        the source
     * @param type          the type
     * @param authorization the authorization
     * @return a new builder
     */
    Action.@NonNull Builder actionBuilder(long timestamp, @NonNull UUID source, Action.Type type,
            Action.Authorization authorization);

    /**
     * Returns a new {@link Action.Builder} instance
     *
     * @param source        the source
     * @param type          the type
     * @param authorization the authorization
     * @return a new builder
     */
    Action.@NonNull Builder actionBuilder(@NonNull UUID source, Action.Type type,
            Action.Authorization authorization);


    /**
     * Gets a {@link ActionLog} instance from the plugin storage.
     *
     * @return a log instance
     */
    @NonNull CompletableFuture<ActionLog> getLog();

    /**
     * Submits a log entry to the plugin to be handled.
     *
     * <p>This method submits the log to the storage provider and broadcasts
     * it.</p>
     *
     * <p>It is therefore roughly equivalent to calling
     * {@link #submitToStorage(Action)} and {@link #broadcastAction(Action)},
     * however, using this method is preferred to making the calls individually.</p>
     *
     * <p>If you want to submit a log entry but don't know which method to pick,
     * use this one.</p>
     *
     * @param entry the entry to submit
     * @return a future which will complete when the action is done
     */
    @NonNull CompletableFuture<Void> submit(@NonNull Action entry);

    /**
     * Submits a log entry to the plugins storage handler.
     *
     * @param entry the entry to submit
     * @return a future which will complete when the action is done
     */
    @NonNull CompletableFuture<Void> submitToStorage(@NonNull Action entry);

    /**
     * Submits a log entry to the plugins log broadcasting handler.
     *
     * <p>If enabled, this method will also dispatch the log entry via the
     * plugins {@link MessagingService}.</p>
     *
     * @param action the action to submit
     * @return a future which will complete when the action is done
     */
    @NonNull CompletableFuture<Void> broadcastAction(@NonNull Action action);
}
