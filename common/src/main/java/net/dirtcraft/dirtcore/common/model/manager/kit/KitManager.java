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

package net.dirtcraft.dirtcore.common.model.manager.kit;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitClaimEntryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages kit related operations in a thread-safe manner.
 */
public interface KitManager {

    /**
     * Checks if the kit exists.
     *
     * @param context the task context
     * @param name    the name
     * @return true, if the kit exists
     */
    boolean checkKitExists(@NonNull TaskContext context, @NonNull String name);

    /**
     * Checks if the target can claim the kit.
     *
     * @param context the task context
     * @param kit     the kit
     * @param target  the target
     * @return the kit claim result
     */
    @NonNull KitClaimResult canClaimKit(@NonNull TaskContext context, @NonNull KitEntity kit,
            @NonNull UUID target);

    /**
     * Computes a list of {@link KitClaimResult}s.
     *
     * @param context the task context
     * @param target  the target
     * @return the list of KitClaimResult
     */
    @NonNull List<KitClaimResult> computeKitListContext(@NonNull TaskContext context,
            @NonNull UUID target);

    /**
     * Creates a kit.
     *
     * @param context the task context
     * @param name    the name
     * @param items   the items
     * @return the kit creation result
     */
    @NonNull KitCreationResult createKit(@NonNull TaskContext context, @NonNull String name,
            @NonNull Collection<ItemStack> items);

    /**
     * Gets the available kits mapped to their names.
     *
     * @param context  the task context
     * @param uniqueId the unique id
     * @return the available kit names
     */
    @NonNull Collection<String> getAvailableKitNames(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets kit by name.
     *
     * @param context the task context
     * @param name    the name
     * @return the optional kit
     */
    @NonNull Optional<KitEntity> getKitByName(@NonNull TaskContext context, @NonNull String name);

    /**
     * Gets all kit names.
     *
     * @param context the task context
     * @return the kit names
     */
    @NonNull List<String> getKitNames(@NonNull TaskContext context);

    /**
     * Tries to display the KitShowGUI for the specified kit to the player.
     *
     * @param kit    the kit
     * @param player the player
     * @return true, if there was an issue
     */
    boolean tryDisplayKitShowGUI(@NonNull KitEntity kit, @NonNull Player player);

    /**
     * Replaces the items in a kit with others.
     *
     * @param context the task context
     * @param kit     the kit
     * @param items   the items
     * @return true, if successful
     */
    boolean updateKitItems(@NonNull TaskContext context, @NonNull KitEntity kit,
            @NonNull Collection<ItemStack> items);

    abstract class KitClaimResult {

        protected final int order;
        @NonNull
        protected final KitEntity kit;

        private KitClaimResult(final int order, @NonNull final KitEntity kit) {
            this.order = order;
            this.kit = kit;
        }

        @NonNull
        static CanBeClaimed canBeClaimed(@NonNull final KitEntity kit) {
            return new CanBeClaimed(kit, null);
        }

        @NonNull
        static CanBeClaimed canBeClaimed(@NonNull final KitEntity kit,
                @NonNull final KitClaimEntryEntity kitClaimEntry) {
            return new CanBeClaimed(kit, kitClaimEntry);
        }

        @NonNull
        static CanNotYetBeClaimed canNotYetBeClaimed(@NonNull final KitEntity kit,
                @NonNull final Instant claimableAt) {
            return new CanNotYetBeClaimed(kit, claimableAt);
        }

        @NonNull
        static CanOnlyBeClaimedOnce canOnlyBeClaimedOnce(@NonNull final KitEntity kit,
                @NonNull final KitClaimEntryEntity kitClaimEntry) {
            return new CanOnlyBeClaimedOnce(kit, kitClaimEntry);
        }

        @NonNull
        static NoPermission noPermission(@NonNull final KitEntity kit) {
            return new NoPermission(kit);
        }

        public int getOrder() {
            return this.order;
        }

        @NonNull
        public KitEntity getKit() {
            return this.kit;
        }

        public static class CanBeClaimed extends KitClaimResult {

            @Nullable
            private final KitClaimEntryEntity kitClaimEntry;

            private CanBeClaimed(@NonNull final KitEntity kit,
                    @Nullable final KitClaimEntryEntity kitClaimEntry) {
                super(0, kit);
                this.kitClaimEntry = kitClaimEntry;
            }

            @Nullable
            public KitClaimEntryEntity getKitClaimEntry() {
                return this.kitClaimEntry;
            }
        }

        public static class CanNotYetBeClaimed extends KitClaimResult {

            @NonNull
            private final Instant claimableAt;

            private CanNotYetBeClaimed(@NonNull final KitEntity kit,
                    @NonNull final Instant claimableAt) {
                super(1, kit);
                this.claimableAt = claimableAt;
            }

            @NonNull
            public Instant getClaimableAt() {
                return this.claimableAt;
            }
        }

        public static class CanOnlyBeClaimedOnce extends KitClaimResult {

            @NonNull
            private final Instant claimedAt;

            private CanOnlyBeClaimedOnce(@NonNull final KitEntity kit,
                    @NonNull final KitClaimEntryEntity kitClaimEntry) {
                super(2, kit);
                this.claimedAt = kitClaimEntry.getTimestamp().toInstant();
            }

            @NonNull
            public Instant getClaimedAt() {
                return this.claimedAt;
            }
        }

        public static class NoPermission extends KitClaimResult {

            private NoPermission(@NonNull final KitEntity kit) {
                super(3, kit);
            }
        }
    }

    interface KitCreationResult {

        @NonNull
        static AlreadyExists alreadyExists() {
            return new AlreadyExists();
        }

        @NonNull
        static NoItems noItems() {
            return new NoItems();
        }

        @NonNull
        static Success success(@NonNull final KitEntity kit) {
            return new Success(kit);
        }

        class AlreadyExists implements KitCreationResult {

            private AlreadyExists() {}
        }

        class NoItems implements KitCreationResult {

            private NoItems() {}
        }

        class Success implements KitCreationResult {

            @NonNull
            private final KitEntity kit;

            private Success(@NonNull final KitEntity kit) {
                this.kit = kit;
            }

            @NonNull
            public KitEntity getKit() {
                return this.kit;
            }
        }
    }
}
