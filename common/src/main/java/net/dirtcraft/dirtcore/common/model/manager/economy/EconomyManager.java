/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
