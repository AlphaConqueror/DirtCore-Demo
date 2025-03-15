/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.manager.crate;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3i;
import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateLocationEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentCommandEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Manages crate related operations in a thread-safe manner.
 */
public interface CrateManager {

    /**
     * The lowest id a command entry can have.
     */
    int MIN_COMMAND_ID = 0;
    /**
     * The lowest id a content entry can have.
     */
    int MIN_CONTENT_ID = 0;
    /**
     * The greatest amount of keys to be given.
     */
    int MAX_KEYS_AMOUNT = 64;
    /**
     * The lowest amount of keys to be given.
     */
    int MIN_KEYS_AMOUNT = 1;
    /**
     * The greatest max stack size of a reward item.
     */
    int MAX_MAX_AMOUNT = 64;
    /**
     * The greatest min stack size of a reward item.
     */
    int MAX_MIN_AMOUNT = 64;
    /**
     * The lowest min stack size of a reward item.
     */
    int MIN_MIN_AMOUNT = 1;
    /**
     * The lowest max stack size of a reward item.
     */
    int MIN_MAX_AMOUNT = 1;
    /**
     * The lowest amount of tickets of a reward item.
     */
    int MIN_TICKETS = 1;
    /**
     * The amount of items to be displayed in the crate animation and chose a reward from.
     */
    int REWARD_LIST_SIZE = 20;

    /**
     * Checks if the crate exists.
     *
     * @param context the task context
     * @param name    the name
     * @return True, if the crate exists, false if otherwise.
     */
    boolean checkCrateExists(@NonNull TaskContext context, @NonNull String name);

    /**
     * Creates a crate.
     *
     * @param context the task context
     * @param name    the name
     * @return the crate, if created
     */
    @NonNull Optional<CrateEntity> createCrate(@NonNull TaskContext context, @NonNull String name);

    /**
     * Gets a crate by name.
     *
     * @param context the task context
     * @param name    the name
     * @return the crate, if available
     */
    @NonNull Optional<CrateEntity> getCrateByName(@NonNull TaskContext context,
            @NonNull String name);

    /**
     * Gets all crates.
     *
     * @param context the task context
     * @return the crates
     */
    @NonNull List<CrateEntity> getCrates(@NonNull TaskContext context);

    /**
     * Gets a crate content by id.
     *
     * @param context   the task context
     * @param crate     the crate
     * @param contentId the content id
     * @return the crate content
     */
    @NonNull Optional<CrateContentEntity> getCrateContentById(@NonNull TaskContext context,
            @NonNull CrateEntity crate, long contentId);

    /**
     * Gets a crate location by position.
     *
     * @param context the task context
     * @param world   the world
     * @param vec3i   the vector
     * @return the crate location, if available
     */
    @NonNull Optional<CrateLocationEntity> getCrateLocationByVec3i(@NonNull TaskContext context,
            @NonNull World world, @NonNull Vec3i vec3i);

    /**
     * Tries to display the CrateOpenGUI to a player.
     *
     * @param context the task context
     * @param player  the player
     * @param pos     the block position
     * @return true, if there is a crate at the position
     */
    boolean tryDisplayCrateOpenGUI(@NonNull TaskContext context, @NonNull Player player,
            @NonNull BlockPos pos);

    /**
     * Tries to display the CratePreviewGUI to a player.
     *
     * @param context the task context
     * @param player  the player
     * @param pos     the block position
     * @return true, if there is a crate at the position
     */
    boolean tryDisplayCratePreviewGUI(@NonNull TaskContext context, @NonNull Player player,
            @NonNull BlockPos pos);

    /**
     * Gets the generator for all crate related operations.
     *
     * @return the generator
     */
    @NonNull Random getGenerator();

    /**
     * Generates the rewards of a crate.
     *
     * @param crate the crate
     * @return the reward list
     */
    @NonNull List<ContentExecutable> generateRewards(@NonNull CrateEntity crate);

    class ContentExecutable {

        @NonNull
        private final Random random;
        private final int minAmount;
        private final int maxAmount;
        private final int tickets;
        private final boolean giveItem;
        @NonNull
        private final ItemStack rewardItemStack;
        @NonNull
        private final List<String> commands;

        public ContentExecutable(@NonNull final Random random,
                @NonNull final CrateContentEntity crateContent,
                @NonNull final ItemStack itemStack) {
            this(random, crateContent.getMinAmount(), crateContent.getMaxAmount(),
                    crateContent.getTickets(), crateContent.isGiveItem(), itemStack,
                    crateContent.getCommands().stream().map(CrateContentCommandEntity::getCommand)
                            .collect(ImmutableCollectors.toList()));
        }

        private ContentExecutable(@NonNull final Random random, final int minAmount,
                final int maxAmount, final int tickets, final boolean giveItem,
                @NonNull final ItemStack itemStack, @NonNull final List<String> commands) {
            this.random = random;
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            this.tickets = tickets;
            this.giveItem = giveItem;
            this.rewardItemStack = this.generateRewardItemStack(random, itemStack);
            this.commands = commands;
        }

        public void reward(@NonNull final DirtCorePlugin plugin, @NonNull final Player player,
                @NonNull final Component title, @NonNull final String displayName,
                final boolean shouldBroadcast) {
            if (this.giveItem) {
                player.addItem(this.rewardItemStack.copy());
            }

            final PlatformFactory<?, ?, ?, ?, ?, ?> platformFactory = plugin.getPlatformFactory();

            if (!this.commands.isEmpty()) {
                final String playerName = player.getName();
                final String uniqueId = player.getUniqueId().toString();

                // TODO: Maybe implement placeholders as classes, since they are used often
                this.commands.forEach(rawCommand -> {
                    final String command = rawCommand.replace("{player_name}", playerName)
                            .replace("{player_uuid}", uniqueId);
                    platformFactory.performCommand(command);
                });
            }

            final ItemStack rewardItemStackCopy = this.rewardItemStack.copy();

            if (shouldBroadcast) {
                platformFactory.broadcast(
                        Components.CRATE_REWARD_RECEIVED.build(player, title, rewardItemStackCopy));
                plugin.getDiscordBotClient().flatMap(
                                discordBotClient -> discordBotClient.getDiscordManager().getGameChannel())
                        .ifPresent(channel -> channel.sendMessage(MarkdownUtil.bold(
                                        String.format("> %s %s opened a %s and received %s.",
                                                plugin.getConfiguration()
                                                        .get(ConfigKeys.DISCORD_EMOJIS_KEY),
                                                platformFactory.componentToUnformattedString(
                                                        player.getDisplayName()), displayName,
                                                platformFactory.componentToUnformattedString(
                                                        rewardItemStackCopy.asDisplayComponent(true)))))
                                .queue());
            } else {
                player.sendMessage(
                        Components.CRATE_REWARD_RECEIVED_SELF.build(title, rewardItemStackCopy));
            }
        }

        @NonNull
        public ItemStack generateRewardItemStack(@NonNull final Random random,
                @NonNull final ItemStack itemStack) {
            final int stackSize = this.maxAmount <= this.minAmount ? this.minAmount
                    : (random.nextInt(this.maxAmount - this.minAmount + 1) + this.minAmount);
            return this.generateItemStack(itemStack, stackSize);
        }

        @NonNull
        public ItemStack generateItemStack(@NonNull final ItemStack itemStack,
                final int stackSize) {
            final ItemStack display = itemStack.copy();
            display.setStackSize(stackSize);
            return display;
        }

        @NonNull
        public ItemStack getRewardItemStack() {
            return this.rewardItemStack;
        }

        @NonNull
        public ContentExecutable copyNewReward() {
            return new ContentExecutable(this.random, this.minAmount, this.maxAmount, this.tickets,
                    this.giveItem, this.rewardItemStack, this.commands);
        }
    }
}
