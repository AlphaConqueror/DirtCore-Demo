/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.manager.limit;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.Limitable;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.storage.cache.Cacheable;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockEntity;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockEntryEntity;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages limit related operations in a thread-safe manner.
 */
public interface LimitManager extends Cacheable<String, LimitedBlockEntity> {

    /**
     * Loads all limited blocks into the cache.
     *
     * @return the future
     */
    @NonNull CompletableFuture<Void> loadAllLimitedBlocks();

    /**
     * Gets all limited blocks.
     *
     * @return the limited blocks
     */
    @NonNull Collection<LimitedBlockEntity> getLimitedBlocks();

    /**
     * Gets the limited block for the {@link Limitable}.
     *
     * @param limitable the limitable
     * @return the limited block, if available
     */
    @NonNull Optional<LimitedBlockEntity> getLimitedBlock(@NonNull Limitable limitable);

    /**
     * Limits a block.
     *
     * @param context   the context
     * @param limitable the limitable
     * @param rule      the rule
     * @param amount    the amount
     * @return the limited block
     */
    @NonNull LimitedBlockEntity limitBlock(@NonNull TaskContext context,
            @NonNull Limitable limitable, @NonNull Rule rule, long amount);

    /**
     * Checks if a user is bypassing limits.
     *
     * @param uniqueId the unique id
     * @return true, if user is bypassing limits
     */
    boolean isBypassingLimits(@NonNull UUID uniqueId);

    /**
     * Changes the limit bypass status of a user.
     *
     * @param uniqueId the unique id
     * @return true, if limit bypass has been enabled, false, if otherwise
     */
    boolean limitBypassChange(@NonNull UUID uniqueId);

    /**
     * Checks if a rule violation is present.
     *
     * @param context      the context
     * @param limitedBlock the limited block
     * @param uniqueId     the unique id
     * @param world        the world
     * @param x            the x
     * @param z            the z
     * @return the result, if available
     */
    @NonNull Optional<Result> checkRuleViolation(@NonNull TaskContext context,
            @NonNull LimitedBlockEntity limitedBlock, @NonNull UUID uniqueId, @NonNull World world,
            int x, int z);

    /**
     * Gets all limited block entries for a limited block.
     *
     * @param context      the context
     * @param limitedBlock the limited block
     * @return the limited block entries
     */
    @NonNull List<LimitedBlockEntryEntity> getLimitedBlockEntries(@NonNull TaskContext context,
            @NonNull final LimitedBlockEntity limitedBlock);

    /**
     * Gets limited block entries.
     *
     * @param context      the context
     * @param world        the world
     * @param chunkPos     the chunk position
     * @param limitedBlock the limited block
     * @param rule         the rule
     * @param uniqueId     the unique id
     * @return the limited block entries
     */
    @NonNull List<LimitedBlockEntryEntity> getLimitedBlockEntries(@NonNull TaskContext context,
            @NonNull World world, @NonNull Vec2i chunkPos, @NonNull LimitedBlockEntity limitedBlock,
            @NonNull Rule rule, @NonNull UUID uniqueId);

    /**
     * Gets the limited block count.
     *
     * @param context  the context
     * @param uniqueId the unique id
     * @return the limited block count
     */
    @NonNull Map<LimitedBlockEntity, Long> getLimitedBlockCount(@NonNull TaskContext context,
            @NonNull UUID uniqueId);

    /**
     * Gets the limited block count as a map of limited blocks to a map of rules
     * and the count related to that rule.
     *
     * @param context  the context
     * @param player   the player
     * @param uniqueId the unique id
     * @return the limited block count
     */
    @NonNull Map<LimitedBlockEntity, Map<Rule, Long>> getLimitedBlockCount(
            @NonNull TaskContext context, @NonNull Player player, @NonNull UUID uniqueId);

    /**
     * Adds a limited block entry.
     *
     * @param context      the context
     * @param limitedBlock the limited block
     * @param uniqueId     the unique id
     * @param world        the world
     * @param x            the x
     * @param y            the y
     * @param z            the z
     */
    void addEntry(@NonNull TaskContext context, @NonNull LimitedBlockEntity limitedBlock,
            @NonNull UUID uniqueId, @NonNull World world, int x, int y, int z);

    /**
     * Removes all limited block entries at a position.
     *
     * @param context the context
     * @param world   the world
     * @param x       the x
     * @param y       the y
     * @param z       the z
     */
    void removeLimitedBlockEntries(@NonNull TaskContext context, @NonNull World world, int x, int y,
            int z);

    default void removeLimitedBlockEntries(@NonNull final TaskContext context,
            @NonNull final World world, @NonNull final BlockPos blockPos) {
        this.removeLimitedBlockEntries(context, world, blockPos.getX(), blockPos.getY(),
                blockPos.getZ());
    }

    /**
     * Represents a limitation rule, sorted by their priority.
     */
    enum Rule {

        /**
         * Limits the amount of blocks a player can place across all worlds.
         */
        GLOBAL((uniqueId, limitedBlockEntry, world, chunkX, chunkZ) -> {
            final String uniqueIdAsString = uniqueId.toString();
            return limitedBlockEntry.getUniqueId().equals(uniqueIdAsString);
        }, NamedTextColor.DARK_RED,
                "The number of blocks of this type you can place across all worlds."),
        /**
         * Limits the amount of blocks a player can place in a world.
         */
        WORLD((uniqueId, limitedBlockEntry, world, chunkX, chunkZ) -> {
            final String uniqueIdAsString = uniqueId.toString();
            final String identifier = world.getIdentifier();
            return limitedBlockEntry.getUniqueId().equals(uniqueIdAsString)
                    && limitedBlockEntry.getWorld().equals(identifier);
        }, NamedTextColor.DARK_BLUE,
                "The number of blocks of this type you can place in this world."),
        /**
         * Limits the amount of blocks all players can place in a single chunk.
         */
        CHUNK_TOTAL((uniqueId, limitedBlockEntry, world, chunkX, chunkZ) -> {
            final String identifier = world.getIdentifier();
            return limitedBlockEntry.getWorld().equals(identifier)
                    && limitedBlockEntry.getChunkX() == chunkX
                    && limitedBlockEntry.getChunkZ() == chunkZ;
        }, NamedTextColor.DARK_GREEN,
                "The combined number of blocks of this type that can be placed in this chunk by "
                        + "all players."),
        /**
         * Limits the amount of blocks a players can place in a single chunk.
         */
        CHUNK((uniqueId, limitedBlockEntry, world, chunkX, chunkZ) -> {
            final String uniqueIdAsString = uniqueId.toString();
            final String identifier = world.getIdentifier();
            return limitedBlockEntry.getUniqueId().equals(uniqueIdAsString)
                    && limitedBlockEntry.getWorld().equals(identifier)
                    && limitedBlockEntry.getChunkX() == chunkX
                    && limitedBlockEntry.getChunkZ() == chunkZ;
        }, NamedTextColor.GREEN, "The number of blocks of this type you can place in this chunk.");

        @NonNull
        public static final Set<String> IDENTIFIERS =
                Arrays.stream(values()).map(Rule::name).collect(ImmutableCollectors.toSet());

        @NonNull
        private final RuleMatcher ruleMatcher;
        @NonNull
        private final NamedTextColor color;
        @NonNull
        private final String description;

        Rule(@NonNull final RuleMatcher ruleMatcher, @NonNull final NamedTextColor color,
                @NonNull final String description) {
            this.ruleMatcher = ruleMatcher;
            this.color = color;
            this.description = description;
        }

        @NonNull
        public static Rule fromString(@NonNull final String s) {
            for (final Rule value : Rule.values()) {
                if (s.equalsIgnoreCase(value.name())) {
                    return value;
                }
            }

            throw new IllegalArgumentException(s);
        }

        @NonNull
        public Result getResult(@NonNull final UUID uniqueId,
                @NonNull final Collection<LimitedBlockEntryEntity> limitedBlockEntries,
                final long max, @NonNull final World world, final int x, final int z) {
            final int amount = limitedBlockEntries.stream()
                    .filter(limitedBlockEntry -> this.ruleMatcher.matches(uniqueId,
                            limitedBlockEntry, world, x, z)).mapToInt(value -> 1).sum();
            return Result.of(this, max, amount);
        }

        @NonNull
        public Component getFormatted(final long count, final long max,
                @Nullable final Function<String, String> command) {
            final TextComponent.Builder builder = Component.text().color(this.color)
                    .append(Component.text('['))
                    .append(Component.text(count, NamedTextColor.GRAY))
                    .append(Component.text('/'))
                    .append(Component.text(max, NamedTextColor.GRAY))
                    .append(Component.text(']'));
            final TextComponent.Builder hoverBuilder = Component.text()
                    .append(Component.text(this.name(), this.color, TextDecoration.BOLD))
                    .appendNewline()
                    .append(Component.text(this.description, this.color));

            if (command != null) {
                hoverBuilder.appendNewline().appendNewline()
                        .append(Component.text("Click to view entries.", NamedTextColor.DARK_GRAY));
                builder.clickEvent(ClickEvent.runCommand(command.apply(this.name())));
            }

            return builder.hoverEvent(HoverEvent.showText(hoverBuilder)).build();
        }

        @NonNull
        public RuleMatcher getRuleMatcher() {
            return this.ruleMatcher;
        }

        @NonNull
        public NamedTextColor getColor() {
            return this.color;
        }

        @NonNull
        public String getDescription() {
            return this.description;
        }
    }

    /**
     * Represents the result of a rule violation check.
     */
    class Result {

        @NonNull
        private final Rule rule;
        private final long max;
        private final long amount;

        private Result(@NonNull final Rule rule, final long max, final long amount) {
            this.rule = rule;
            this.max = max;
            this.amount = amount;
        }

        @NonNull
        public static Result of(final Rule rule, final long max, final long amount) {
            return new Result(rule, max, amount);
        }

        public boolean violates() {
            return this.amount >= this.max;
        }

        @NonNull
        public Rule getRule() {
            return this.rule;
        }

        public long getMax() {
            return this.max;
        }

        public long getAmount() {
            return this.amount;
        }
    }

    @FunctionalInterface
    interface RuleMatcher {

        boolean matches(@NonNull UUID uniqueId, @NonNull LimitedBlockEntryEntity limitedBlockEntry,
                @NonNull World world, int chunkX, int chunkZ);

        default boolean matches(@NonNull final UUID uniqueId,
                @NonNull final LimitedBlockEntryEntity limitedBlockEntry,
                @NonNull final World world, @NonNull final Vec2i chunkPos) {
            return this.matches(uniqueId, limitedBlockEntry, world, chunkPos.x, chunkPos.y);
        }
    }
}
