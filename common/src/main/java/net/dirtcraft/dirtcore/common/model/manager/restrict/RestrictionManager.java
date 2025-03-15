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

package net.dirtcraft.dirtcore.common.model.manager.restrict;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictionWorldEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.item.RestrictedItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.mod.RestrictedModEntity;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages restriction related operations in a thread-safe manner.
 */
public interface RestrictionManager {

    /**
     * Loads all restrictions into the cache.
     *
     * @return the future
     */
    @NonNull CompletableFuture<Void> loadAllRestrictions();

    /**
     * Gets all restricted items.
     *
     * @return the restricted items
     */
    @NonNull List<RestrictedItemEntity> getRestrictedItems();

    /**
     * Gets the restricted item for the {@link ItemInfoProvider}.
     * Persistent data has to partially match.
     *
     * @param itemInfoProvider the item info provider
     * @return the restricted item, if available
     */
    @NonNull Optional<RestrictedItemEntity> getRestrictedItem(
            @NonNull ItemInfoProvider itemInfoProvider);

    /**
     * Gets the restricted item for the {@link ItemInfoProvider}.
     * Persistent data has to exactly match.
     *
     * @param itemInfoProvider the item info provider
     * @return the restricted item, if available
     */
    @NonNull Optional<RestrictedItemEntity> getRestrictedItemExact(
            @NonNull ItemInfoProvider itemInfoProvider);

    /**
     * Gets the restricted items with matching identifiers.
     * Results are sorted by presence of persistent data.
     *
     * @param identifier the identifier
     * @return the restricted items
     */
    @NonNull List<RestrictedItemEntity> getRestrictedItems(@NonNull String identifier);

    /**
     * Restricts an item.
     *
     * @param context          the context
     * @param itemInfoProvider the item info provider
     * @return the restricted item
     */
    @NonNull RestrictedItemEntity restrictItem(@NonNull TaskContext context,
            @NonNull ItemInfoProvider itemInfoProvider);

    /**
     * Sets persistent data of a restricted item.
     *
     * @param restrictedItem the restricted item
     * @param persistentData the persistent data
     */
    void setPersistentData(@NonNull RestrictedItemEntity restrictedItem,
            @Nullable String persistentData);

    /**
     * Removes an item restriction.
     *
     * @param context        the context
     * @param restrictedItem the restricted item
     */
    void removeItemRestriction(@NonNull TaskContext context,
            @NonNull RestrictedItemEntity restrictedItem);

    /**
     * Gets the restricted mods.
     *
     * @return the restricted mods
     */
    @NonNull List<RestrictedModEntity> getRestrictedMods();

    /**
     * Gets the restricted mods with matching mod identifiers.
     *
     * @param mod the mod
     * @return the restricted mod, if available
     */
    @NonNull Optional<RestrictedModEntity> getRestrictedMod(@NonNull String mod);

    /**
     * Restricts a mod.
     *
     * @param context the context
     * @param mod     the mod
     * @return the restricted mod
     */
    @NonNull RestrictedModEntity restrictMod(@NonNull TaskContext context, @NonNull String mod);

    /**
     * Removes a mod restriction.
     *
     * @param context       the context
     * @param restrictedMod the restricted mod
     */
    void removeModRestriction(@NonNull TaskContext context,
            @NonNull RestrictedModEntity restrictedMod);

    /**
     * Checks if a user is bypassing restrictions.
     *
     * @param uniqueId the unique id
     * @return true, if user is bypassing restrictions
     */
    boolean isBypassingRestrictions(@NonNull UUID uniqueId);

    /**
     * Changes the restriction bypass status of a user.
     *
     * @param uniqueId the unique id
     * @return true, if limit bypass has been enabled, false, if otherwise
     */
    boolean restrictionsBypassChange(@NonNull UUID uniqueId);

    /**
     * Checks if a {@link ItemInfoProvider} is restricted.
     *
     * @param itemInfoProvider the item info provider
     * @param uniqueId         the unique id
     * @param action           the action
     * @return the result
     */
    @NonNull Result isRestricted(@NonNull ItemInfoProvider itemInfoProvider, @NonNull UUID uniqueId,
            @NonNull Action action);

    default @NonNull List<RestrictedItemEntity> getRestrictedItems(
            @NonNull final ItemInfoProvider itemInfoProvider) {
        return this.getRestrictedItems(itemInfoProvider.getIdentifier());
    }

    default @NonNull Optional<RestrictedModEntity> getRestrictedMod(
            @NonNull final ItemInfoProvider itemInfoProvider) {
        return this.getRestrictedMod(itemInfoProvider.getMod());
    }

    /**
     * Represents the restriction type of worlds.
     * <p>
     * Restricted items can be used in whitelisted worlds.
     * Restricted items can not be used in blacklisted worlds.
     */
    enum AccessControlType {

        /**
         * Regards specified worlds as whitelisted, all other worlds as blacklisted.
         */
        WHITELIST((restrictedWorlds, world) -> restrictedWorlds.stream().noneMatch(
                restrictionWorld -> restrictionWorld.getIdentifier().equals(world.getIdentifier())),
                "None", NamedTextColor.GREEN),
        /**
         * Regards specified worlds as blacklisted, all other worlds as whitelisted.
         */
        BLACKLIST((restrictedWorlds, world) -> restrictedWorlds.stream().anyMatch(
                restrictionWorld -> restrictionWorld.getIdentifier().equals(world.getIdentifier())),
                "All", NamedTextColor.RED);

        @NonNull
        private final BiFunction<Collection<RestrictionWorldEntity>, World, Boolean>
                isRestrictedFunction;
        @NonNull
        private final String descriptor;
        @NonNull
        private final NamedTextColor color;

        AccessControlType(
                @NonNull final BiFunction<Collection<RestrictionWorldEntity>, World, Boolean> isRestrictedFunction,
                @NonNull final String descriptor, @NonNull final NamedTextColor color) {
            this.isRestrictedFunction = isRestrictedFunction;
            this.descriptor = descriptor;
            this.color = color;
        }

        public boolean isRestricted(
                @NonNull final Collection<RestrictionWorldEntity> restrictionWorlds,
                @NonNull final World world) {
            return this.isRestrictedFunction.apply(restrictionWorlds, world);
        }

        @NonNull
        public String getDescriptor() {
            return this.descriptor;
        }

        @NonNull
        public NamedTextColor getColor() {
            return this.color;
        }
    }

    /**
     * Represents an action that can be restricted.
     */
    enum Action {

        /**
         * Attacking another entity.
         */
        ATTACK,
        /**
         * Breaking a block.
         */
        BREAK,
        /**
         * Dropping an item.
         */
        DROP,
        /**
         * Interacting with an item or block.
         */
        INTERACT,
        /**
         * Interacting with an item in a container or inventory.
         */
        INVENTORY_CLICK,
        /**
         * Picking up an item.
         */
        PICK_UP,
        /**
         * Placing a block.
         */
        PLACE
    }

    /**
     * Represents the result of a restriction check.
     */
    enum Result {

        /**
         * User is bypassing.
         */
        BYPASSING(false),
        /**
         * Item is restricted.
         */
        RESTRICTED(true),
        /**
         * Item together with persistent data is restricted.
         */
        RESTRICTED_PERSISTENT_DATA(true),
        /**
         * Item is not restricted.
         */
        UNRESTRICTED(false);

        private final boolean violates;

        Result(final boolean violates) {
            this.violates = violates;
        }

        public boolean violates() {
            return this.violates;
        }
    }
}
