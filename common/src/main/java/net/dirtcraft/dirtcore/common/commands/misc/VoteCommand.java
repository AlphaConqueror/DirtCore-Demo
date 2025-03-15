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

package net.dirtcraft.dirtcore.common.commands.misc;

import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.DefaultArguments;
import net.dirtcraft.dirtcore.common.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.user.UserParser;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.VoteDataEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.dirtcraft.dirtcore.common.util.VoteReward;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.Session;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;

public class VoteCommand extends AbstractCommand<DirtCorePlugin, Sender> {

    public static final String COMMAND_VOTE_CLAIM = "/vote claim";

    public VoteCommand(final DirtCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public ArgumentBuilder<DirtCorePlugin, Sender, ?> build(
        @NonNull final ArgumentFactory<DirtCorePlugin> factory) {
        return Commands.literal("vote").requiresPermission(Permission.VOTE)
            .executes(context -> this.list(context.getSource()))
            .then(Commands.literal("claim").consoleUsage(ConsoleUsage.DENIED)
                .requiresPermission(Permission.VOTE_CLAIM)
                .executes(context -> this.claim(context.getSource().getPlayerOrException())))
            .then(Commands.literal("stats").requiresPermission(Permission.VOTE_STATS_OWN)
                .executes(context -> this.stats(context.getSource()))
                .then(DefaultArguments.USER_TARGET.getArgument()
                    .requiresPermission(Permission.VOTE_STATS_OTHERS)
                    .executes(context -> this.stats(context.getSource(),
                        DefaultArguments.USER_TARGET.fromContext(context)))));
    }

    private int list(final Sender sender) {
        final List<String> links = this.plugin.getConfiguration().get(ConfigKeys.VOTE_LINKS);

        if (links.isEmpty()) {
            sender.sendMessage(Components.VOTE_NO_LINKS.build());
        } else {
            sender.sendMessage(Components.VOTE_LINKS.build(links));
        }

        return Command.SINGLE_SUCCESS;
    }

    private int claim(final Player player) {
        final UUID uniqueId = player.getUniqueId();

        // player inventory change, need to execute sync
        this.plugin.getBootstrap().getScheduler()
            .executeSync(() -> this.plugin.getStorage().performTask(context -> {
                final VoteDataEntity voteData =
                    context.session().get(VoteDataEntity.class, uniqueId.toString());
                final int unclaimedVotes;

                if (voteData == null || (unclaimedVotes = voteData.getUnclaimedVotes()) == 0) {
                    context.queue(
                        () -> player.sendMessage(Components.VOTE_NOTHING_TO_CLAIM.build()));
                    return;
                }

                final int freeSpacePerReward =
                    this.plugin.getConfiguration().get(ConfigKeys.VOTE_CLAIM_FREE_INVENTORY_SPACE);
                final ImmutableList.Builder<Component> additionalAnnouncementBuilder =
                    ImmutableList.builder();
                final Map<Integer, VoteReward> claimedRewardsExtraCommandsMap =
                    this.plugin.getConfiguration()
                        .get(ConfigKeys.VOTE_CLAIMED_REWARDS_EXTRA_COMMANDS);
                final List<Integer> claimedRewardsExtraCommandsKeys =
                    claimedRewardsExtraCommandsMap.keySet().stream()
                        .sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                final Map<Integer, VoteReward> streakExtraRewardCommandsMap =
                    this.plugin.getConfiguration()
                        .get(ConfigKeys.VOTE_STREAK_EXTRA_REWARD_COMMANDS);
                final List<Integer> streakExtraRewardCommandsKeys =
                    streakExtraRewardCommandsMap.keySet().stream().sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList());
                final List<String> commands =
                    this.plugin.getConfiguration().get(ConfigKeys.VOTE_CLAIM_COMMANDS);
                final int claimedVotes = voteData.getClaimedVotes();
                final int streak = voteData.getClaimedVoteStreak();
                final String playerName = player.getName();
                final Session session = context.session();
                int claimed = 0;

                while (claimed < unclaimedVotes) {
                    int freeSlotsNeeded = freeSpacePerReward;
                    List<String> claimedRewardsExtraCommands = null;

                    for (final int key : claimedRewardsExtraCommandsKeys) {
                        if ((claimedVotes + claimed + 1) % key == 0) {
                            final VoteReward voteReward = claimedRewardsExtraCommandsMap.get(key);

                            claimedRewardsExtraCommands = voteReward.getCommands();
                            freeSlotsNeeded += voteReward.getSpace();
                            break;
                        }
                    }

                    List<String> streakExtraRewardCommands = null;

                    for (final int key : streakExtraRewardCommandsKeys) {
                        if ((streak + claimed + 1) % key == 0) {
                            final VoteReward voteReward = streakExtraRewardCommandsMap.get(key);

                            streakExtraRewardCommands = voteReward.getCommands();
                            freeSlotsNeeded += voteReward.getSpace();
                            break;
                        }
                    }

                    // check if there is enough space for all the rewards
                    // if not, simply abort
                    if (player.getFreeInventorySpace() < freeSlotsNeeded) {
                        break;
                    }

                    claimed++;

                    final List<String> finalClaimedRewardsExtraCommands =
                        claimedRewardsExtraCommands;
                    final List<String> finalStreakExtraRewardCommands = streakExtraRewardCommands;
                    final int finalClaimed = claimed;

                    context.queue(() -> {
                        commands.forEach(
                            command -> this.performFormattedCommand(command, playerName, uniqueId));

                        if (finalClaimedRewardsExtraCommands != null) {
                            finalClaimedRewardsExtraCommands.forEach(
                                command -> this.performFormattedCommand(command, playerName,
                                    uniqueId));
                            additionalAnnouncementBuilder.add(
                                Components.VOTE_EXTRA_CLAIM_REWARDS.build(playerName,
                                    claimedVotes + finalClaimed));
                        }

                        if (finalStreakExtraRewardCommands != null) {
                            finalStreakExtraRewardCommands.forEach(
                                command -> this.performFormattedCommand(command, playerName,
                                    uniqueId));
                            additionalAnnouncementBuilder.add(
                                Components.VOTE_EXTRA_STREAK_REWARDS.build(playerName,
                                    streak + finalClaimed));
                        }
                    });
                }

                if (claimed == 0) {
                    context.queue(() -> player.sendMessage(Components.INVENTORY_FULL.build()));
                    return;
                }

                voteData.claim(claimed);
                session.merge(voteData);

                final int finalClaimed = claimed;

                context.queue(() -> {
                    this.plugin.getPlatformFactory()
                        .broadcast(Components.VOTE_PLAYER_VOTED.build(player, finalClaimed));
                    additionalAnnouncementBuilder.build().forEach(
                        component -> this.plugin.getPlatformFactory().broadcast(component));

                    if (finalClaimed < unclaimedVotes) {
                        final int remaining = unclaimedVotes - finalClaimed;
                        player.sendMessage(
                            Components.VOTE_COULD_NOT_CLAIM_EVERYTHING.build(remaining));
                    }
                });
            }));

        return Command.SINGLE_SUCCESS;
    }

    private void performFormattedCommand(@NonNull final String formattedCommand,
        @NonNull final String name, @NonNull final UUID uniqueId) {
        final String command = formattedCommand.replace("{player_name}", name)
            .replace("{player_uuid}", uniqueId.toString());
        this.plugin.getPlatformFactory().performCommand(command);
    }

    private int stats(@NonNull final Sender sender) {
        this.plugin.getStorage().performTask(context -> {
            final User target =
                this.plugin.getUserManager().getOrCreateUser(context, sender.getUniqueId());
            this.stats(context, sender, target);
        });

        return Command.SINGLE_SUCCESS;
    }

    private int stats(@NonNull final Sender sender,
        final UserParser.@NonNull UserInformation targetInformation) {
        this.plugin.getStorage().performTask(context -> {
            final User target = targetInformation.getUser(context);
            this.stats(context, sender, target);
        });

        return Command.SINGLE_SUCCESS;
    }

    private void stats(@NonNull final TaskContext context, @NonNull final Sender sender,
        @NonNull final User target) {
        final VoteDataEntity voteData =
            context.session().get(VoteDataEntity.class, target.getUniqueId().toString());

        context.queue(() -> {
            final int totalVotes;
            final int claimedVotes;
            final int unclaimedVotes;
            final int voteStreak;
            final int claimedVoteStreak;
            final Component lastVote;

            if (voteData == null) {
                totalVotes = 0;
                claimedVotes = 0;
                unclaimedVotes = 0;
                voteStreak = 0;
                claimedVoteStreak = 0;
                lastVote = Component.text("never", NamedTextColor.RED);
            } else {
                final Instant lastVoteInstant = voteData.getLastVote().toInstant();

                totalVotes = voteData.getTotalVotes();
                claimedVotes = voteData.getClaimedVotes();
                unclaimedVotes = voteData.getUnclaimedVotes();
                voteStreak = voteData.getVoteStreak();
                claimedVoteStreak = voteData.getClaimedVoteStreak();
                lastVote = Component.text(
                    FormatUtils.formatDateDiff(lastVoteInstant, Instant.now(), true, true),
                    NamedTextColor.BLUE).hoverEvent(
                    HoverEvent.showText(text(FormatUtils.formatDate(lastVoteInstant), BLUE)));
            }

            sender.sendMessage(
                Components.VOTE_STATS.build(target.getName(), totalVotes, claimedVotes,
                    unclaimedVotes, voteStreak, claimedVoteStreak, lastVote));
        });
    }
}
