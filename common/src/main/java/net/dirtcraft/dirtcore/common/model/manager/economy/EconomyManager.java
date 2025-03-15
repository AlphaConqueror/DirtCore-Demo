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

package net.dirtcraft.dirtcore.common.model.manager.economy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.economy.WorthItemEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Manages economy related operations in a thread-safe manner.
 */
public interface EconomyManager {

    /**
     * Gets all worth items.
     *
     * @param context the context
     * @return the worth items
     */
    @NonNull List<WorthItemEntity> getWorthItems(@NonNull TaskContext context);

    /**
     * Gets the worth item for the {@link ItemInfoProvider}.
     * Persistent data has to partially match.
     *
     * @param context          the context
     * @param itemInfoProvider the item info provider
     * @return the worth item, if available
     */
    @NonNull Optional<WorthItemEntity> getWorthItem(@NonNull TaskContext context,
            @NonNull ItemInfoProvider itemInfoProvider);

    /**
     * Gets the worth item for the {@link ItemInfoProvider}.
     * Persistent data has to exactly match.
     *
     * @param context          the context
     * @param itemInfoProvider the item info provider
     * @return the worth item, if available
     */
    @NonNull Optional<WorthItemEntity> getWorthItemExact(@NonNull TaskContext context,
            @NonNull ItemInfoProvider itemInfoProvider);

    /**
     * Gets the worth items with matching identifiers.
     * Results are sorted by presence of persistent data.
     *
     * @param context    the context
     * @param identifier the identifier
     * @return the worth items
     */
    @NonNull List<WorthItemEntity> getWorthItems(@NonNull TaskContext context,
            @NonNull String identifier);

    /**
     * Sets the worth of an item.
     *
     * @param context          the context
     * @param itemInfoProvider the item info provider
     * @param amount           the amount
     */
    void setItemWorth(@NonNull TaskContext context, @NonNull ItemInfoProvider itemInfoProvider,
            double amount);

    /**
     * Gets the leaderboard of the users with the most balance.
     * Mapped {@link UUID} to balance of the users.
     *
     * @param context the context
     * @return the leaderboard
     */
    @NonNull Map<UUID, Double> getLeaderboard(@NonNull TaskContext context);

    default @NonNull List<WorthItemEntity> getWorthItems(@NonNull final TaskContext context,
            @NonNull final ItemInfoProvider itemInfoProvider) {
        return this.getWorthItems(context, itemInfoProvider.getIdentifier());
    }
}
