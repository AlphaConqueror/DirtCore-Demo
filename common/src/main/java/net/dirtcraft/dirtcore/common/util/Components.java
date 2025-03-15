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

package net.dirtcraft.dirtcore.common.util;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.commands.misc.VoteCommand;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.model.Identifiable;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.Limitable;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.manager.chat.ChatManager;
import net.dirtcraft.dirtcore.common.model.manager.limit.LimitManager;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.model.manager.restrict.RestrictionManager;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3i;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.kit.KitEntity;
import net.dirtcraft.dirtcore.common.storage.entities.limit.LimitedBlockEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.KickEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnmuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.WarnEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RestrictiveAction;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.item.RestrictedItemEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.mod.RestrictedModEntity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.BLUE;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.DARK_RED;
import static net.kyori.adventure.text.format.NamedTextColor.GOLD;
import static net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static net.kyori.adventure.text.format.NamedTextColor.GREEN;
import static net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE;
import static net.kyori.adventure.text.format.NamedTextColor.RED;
import static net.kyori.adventure.text.format.NamedTextColor.WHITE;
import static net.kyori.adventure.text.format.NamedTextColor.YELLOW;
import static net.kyori.adventure.text.format.Style.style;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;
import static net.kyori.adventure.text.format.TextDecoration.STRIKETHROUGH;

public interface Components {

    /*
     * GLOBAL
     */

    Args0 ADD = () -> text().append(text('[', GRAY))
            .append(text('+', GREEN))
            .append(text(']', GRAY)).build();
    Args1<String> AUTHOR = author -> text().append(text("Author ", RED))
            .append(text("» ", DARK_GRAY))
            .append(text(author, AQUA)).build();
    Component BAR = text().appendSpace()
            .append(text('!', RED, BOLD)).appendSpace()
            .append(text("-------------------------------------------------", DARK_GRAY,
                    STRIKETHROUGH)).appendSpace()
            .append(text('!', RED, BOLD)).appendSpace().build();
    Component CHAT_MARKER_DEFAULT =
            Component.text(ChatManager.DEFAULT_CHAT_MARKER, NamedTextColor.GRAY,
                    TextDecoration.BOLD).hoverEvent(HoverEvent.showText(
                    Component.text().color(NamedTextColor.GRAY)
                            .append(Component.text("Default chat marker."))));
    // FIXME: Fix for legacy at some point.
    Args0 CLEAR = () -> text().append(text('[', GRAY))
            .append(text("\uD83D\uDDD1", RED))
            .append(text(']', GRAY)).build();
    Component DISABLED = Component.text("disabled", RED, BOLD);
    Args1<Component> DURATION = duration -> text().append(text("Duration ", RED))
            .append(text("» ", DARK_GRAY))
            .append(duration.color(DARK_RED)).build();
    Args0 EDIT = () -> text().append(text('[', GRAY))
            .append(text('✎', GOLD))
            .append(text(']', GRAY)).build();
    Component ENABLED = Component.text("enabled", GREEN, BOLD);
    Args1<String> INCIDENT_ID = incidentId -> text().append(text("Incident ID ", RED))
            .append(text("» ", DARK_GRAY))
            .append(text(incidentId, AQUA)).build();
    Component JOIN_MESSAGE_PREFIX = text(">>> ", GREEN, BOLD);
    Args1<Component> JOIN_MESSAGE_DEFAULT = name -> text().append(JOIN_MESSAGE_PREFIX)
            .append(name).build();
    Args0 KILL = () -> text().append(text('[', GRAY))
            .append(text('☠', RED))
            .append(text(']', GRAY)).build();
    Component LEAVE_MESSAGE_PREFIX = text("<<< ", RED, BOLD);
    Args1<Component> LEAVE_MESSAGE_DEFAULT = name -> text().append(LEAVE_MESSAGE_PREFIX)
            .append(name).build();
    MultipleArgs3<TaskContext, User, Component> LEAVE_MESSAGE_SET = (context, user, message) -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(text().append(text("Your leave message has been set.", GOLD))));
        builder.add(prefixed(text("Preview: ", GOLD)).append(
                        LEAVE_MESSAGE_DEFAULT.build(user.formatDisplay(context))).appendSpace()
                .append(message));

        return builder.build();
    };
    MultipleArgs3<TaskContext, User, Component> LEAVE_MESSAGE_SET_SUCCESS =
            (context, user, message) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(prefixed(text().color(GREEN)
                        .append(text("Leave message of user "))
                        .append(text(user.getName(), style(BOLD)))
                        .append(text(" has been set."))));
                builder.add(prefixed(text("Preview: ", GOLD)).append(
                                LEAVE_MESSAGE_DEFAULT.build(user.formatDisplay(context))).appendSpace()
                        .append(message));

                return builder.build();
            };
    Component PREFIX = text().color(DARK_GRAY)
            .append(text('['))
            .append(text().decoration(BOLD, true)
                    .append(text('D', DARK_RED))
                    .append(text('C', RED)))
            .append(text(']')).build();
    MultipleArgs3<TaskContext, User, Component> JOIN_MESSAGE_PREVIEW = (context, user, message) -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(text().color(GOLD)
                .append(text("Join message preview: "))));
        builder.add(prefixed(
                text().append(JOIN_MESSAGE_DEFAULT.build(user.formatDisplay(context))).appendSpace()
                        .append(message)));

        return builder.build();
    };
    MultipleArgs3<TaskContext, User, Component> JOIN_MESSAGE_PREVIEW_OTHER =
            (context, user, message) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(prefixed(text().color(GOLD)
                        .append(text("Join message preview of user "))
                        .append(text(user.getName(), style(BOLD)))
                        .append(text(':'))));
                builder.add(prefixed(
                        text().append(JOIN_MESSAGE_DEFAULT.build(user.formatDisplay(context)))
                                .appendSpace()
                                .append(message)));

                return builder.build();
            };
    MultipleArgs3<TaskContext, User, Component> JOIN_MESSAGE_SET = (context, user, message) -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(text().append(text("Your join message has been set.", GOLD))));
        builder.add(prefixed(text("Preview: ", GOLD)).append(
                        JOIN_MESSAGE_DEFAULT.build(user.formatDisplay(context))).appendSpace()
                .append(message));

        return builder.build();
    };
    MultipleArgs3<TaskContext, User, Component> JOIN_MESSAGE_SET_SUCCESS =
            (context, user, message) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(prefixed(text().color(GREEN)
                        .append(text("Join message of user "))
                        .append(text(user.getName(), style(BOLD)))
                        .append(text(" has been set."))));
                builder.add(prefixed(text("Preview: ", GOLD)).append(
                                JOIN_MESSAGE_DEFAULT.build(user.formatDisplay(context))).appendSpace()
                        .append(message));

                return builder.build();
            };
    MultipleArgs3<TaskContext, User, Component> LEAVE_MESSAGE_PREVIEW =
            (context, user, message) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(prefixed(text().color(GOLD)
                        .append(text("Leave message preview: "))));
                builder.add(prefixed(
                        text().append(LEAVE_MESSAGE_DEFAULT.build(user.formatDisplay(context)))
                                .appendSpace()
                                .append(message)));

                return builder.build();
            };
    MultipleArgs3<TaskContext, User, Component> LEAVE_MESSAGE_PREVIEW_OTHER =
            (context, user, message) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(prefixed(text().color(GOLD)
                        .append(text("Leave message preview of user "))
                        .append(text(user.getName(), style(BOLD)))
                        .append(text(':'))));
                builder.add(prefixed(
                        text().append(LEAVE_MESSAGE_DEFAULT.build(user.formatDisplay(context)))
                                .appendSpace()
                                .append(message)));

                return builder.build();
            };
    Args0 DISCORD_CONNECT_FAIL = () -> prefixed(text("Could not connect to Discord.", RED));
    Args0 INVENTORY_FULL =
            () -> prefixed(text("Your inventory does not have enough capacity!", RED));
    Args0 BLOCK_CAN_NOT_BE_EMPTY = () -> prefixed(text("Block can not be empty.", RED));
    Args2<World, Vec2i> COULD_NOT_LOAD_CHUNK = (world, chunkPos) -> prefixed(text().color(RED)
            .append(text("Could not load chunk "))
            .append(text(chunkPos.toShortString(), Style.style(BOLD)))
            .append(text(" in world "))
            .append(text(world.getIdentifier(), Style.style(BOLD)))
            .append(text('.')));
    Args0 ITEM_CAN_NOT_BE_EMPTY = () -> prefixed(text("Item can not be empty.", RED));
    Args0 NO_BLOCK_IN_HAND = () -> prefixed(text("You have no block in hand.", RED));
    Args0 NO_ITEM_IN_HAND = () -> prefixed(text("You have no item in hand.", RED));
    Args0 NO_WORLD_PROVIDED = () -> prefixed(text("No world has been provided.", RED));
    Args0 PERSISTENT_DATA_EMPTY = () -> prefixed(text("Persistent data is empty.", RED));
    Args1<String> REASON = reason -> text().append(text("Reason ", RED))
            .append(text("» ", DARK_GRAY))
            .append(text(reason, YELLOW)).build();
    Args0 REASON_EMPTY = () -> prefixed(text("The reason can not be empty!", RED));
    Args0 REMOVE = () -> text().append(text('[', GRAY))
            .append(text('-', RED))
            .append(text(']', GRAY)).build();
    Args0 USE = () -> text().append(text('[', GRAY))
            .append(text('✔', GREEN))
            .append(text(']', GRAY)).build();
    Args3<DirtCorePlugin, String, BlockPos> WORLD_POSITION =
            (plugin, worldIdentifier, blockPos) -> {
                final int x = blockPos.getX();
                final int y = blockPos.getY();
                final int z = blockPos.getZ();

                return Component.text()
                        .append(Component.text(worldIdentifier, NamedTextColor.GRAY).hoverEvent(
                                        HoverEvent.showText(
                                                Component.text("Click to copy!",
                                                        NamedTextColor.DARK_GRAY)))
                                .clickEvent(ClickEvent.clickEvent(
                                        plugin.getPlatformFactory().copyEventAction(),
                                        worldIdentifier)))
                        .append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                        .append(Component.text().color(NamedTextColor.GRAY)
                                .append(Component.text('('))
                                .append(Component.text()
                                        .append(Component.text(x, NamedTextColor.WHITE))
                                        .append(Component.text(", "))
                                        .append(Component.text(y, NamedTextColor.WHITE))
                                        .append(Component.text(", "))
                                        .append(Component.text(z, NamedTextColor.WHITE)).hoverEvent(
                                                HoverEvent.showText(Component.text("Click to copy!",
                                                        NamedTextColor.DARK_GRAY))).clickEvent(
                                                ClickEvent.clickEvent(plugin.getPlatformFactory()
                                                                .copyEventAction(),
                                                        String.format("%d %d %d", x, y, z))))
                                .append(Component.text(')'))).build();
            };

    /*
     * COMMANDS
     */

    Args2<String, String> BALANCE_INFO = (username, balance) -> prefixed(text().color(GOLD)
            .append(text("Balance of "))
            .append(text(username, style(BOLD)))
            .append(text(": "))
            .append(text(balance, style(GREEN, BOLD))));
    Args0 BALANCE_PAY_ERROR_SELF = () -> prefixed(text("You can not send money to yourself.", RED));
    Args2<String, String> BALANCE_ADD = (target, amount) -> prefixed(text().color(GOLD)
            .append(text("You have added "))
            .append(text(amount, style(BOLD, GREEN)))
            .append(text(" to "))
            .append(text(target, style(BOLD)))
            .append(text('.')));
    Args2<String, String> BALANCE_PAY_SUCCESS = (payee, amount) -> prefixed(text().color(GOLD)
            .append(text("You have successfully sent "))
            .append(text(amount, style(GREEN, BOLD)))
            .append(text(" to "))
            .append(text(payee, style(BOLD)))
            .append(text('.')));
    Args2<String, String> BALANCE_NO_FUNDS = (amount, target) -> prefixed(text().color(RED)
            .append(text("You do not have enough funds to send "))
            .append(text(amount, style(GOLD, BOLD)))
            .append(text(" to "))
            .append(text(target))
            .append(text('.')));
    Args2<String, String> BALANCE_RECEIVE_SUCCESS = (payer, amount) -> prefixed(text().color(GOLD)
            .append(text("You have received "))
            .append(text(amount, style(BOLD, GREEN)))
            .append(text(" from "))
            .append(text(payer, style(BOLD)))
            .append(text('.')));
    Args2<String, String> BALANCE_REMOVE = (target, amount) -> prefixed(text().color(GOLD)
            .append(text("You have removed "))
            .append(text(amount, style(BOLD, GREEN)))
            .append(text(" from "))
            .append(text(target, style(BOLD)))
            .append(text('.')));
    Args2<String, String> BALANCE_REMOVE_FAILURE = (target, amount) -> prefixed(text().color(GOLD)
            .append(text("Could not remove "))
            .append(text(amount, style(BOLD, GREEN)))
            .append(text(" from "))
            .append(text(target, style(BOLD))));
    Args2<String, String> BALANCE_SET = (target, amount) -> prefixed(text().color(GOLD)
            .append(text("You have set the balance of "))
            .append(text(target, style(BOLD)))
            .append(text(" to "))
            .append(text(amount, style(BOLD, GREEN)))
            .append(text('.')));
    Args0 BALANCE_TRANSACTION_ERROR = () -> prefixed(text().color(RED)
            .append(text("Oops! Your transaction exceeded system limits "))
            .append(text("and was cancelled. Please try again with a "))
            .append(text("smaller amount")));
    Args1<String> BAN_MODIFIED = name -> prefixed(text().color(GREEN)
            .append(text("Ban of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been modified.")));
    Args0 BAN_CHANGES_NEEDED = () -> prefixed(text().color(RED)
            .append(text("No changes have been made. Ban has not been modified.")));
    Args1<String> BAN_IP_USER_ALREADY_IP_BANNED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" is already IP banned.")));
    MultipleArgs1<String> BAN_IP_USER_NOT_BANNED = name -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(text().color(RED)
                .append(text("User "))
                .append(text(name, style(BOLD)))
                .append(text(" is not banned."))));
        builder.add(prefixed(text().color(RED)
                .append(text("IP bans should only be used to escalate bans due to the risk of ban"
                        + " evasion."))));

        return builder.build();
    };
    Args2<@NonNull BanEntity, @NonNull String> BAN_SCREEN =
            (ban, authorName) -> getDefaultPunishmentComponent("You have been banned!", 0, 0, ban,
                    authorName);
    @SuppressWarnings("unused")
    Args2<@NonNull BanEntity, @NonNull String> BAN_SCREEN_LEGACY =
            (ban, authorName) -> getDefaultPunishmentComponent("You have been banned!", 0, 4, ban,
                    authorName);
    Args1<String> BAN_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been banned.")));
    Args1<String> BAN_USER_CAN_NOT_BE_BANNED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" can not be banned.")));
    Args1<String> CHAT_MARKER_DOES_NOT_EXIST = prefixName -> prefixed(text().color(RED)
            .append(text("Chat marker "))
            .append(text(prefixName, style(BOLD)))
            .append(text(" does not exist.")));
    Args1<String> CHAT_MARKER_GRANTED = markerName -> prefixed(text().color(GOLD)
            .append(text("You have been granted the chat marker "))
            .append(text(markerName, style(BOLD)))
            .append(text('.')));
    Args2<String, String> CHAT_MARKER_GRANTED_SUCCESS = (username, markerName) -> prefixed(
            text().color(GREEN)
                    .append(text("Successfully granted user "))
                    .append(text(username, style(BOLD)))
                    .append(text(" the chat marker "))
                    .append(text(markerName, style(BOLD)))
                    .append(text('.')));
    Args0 CHAT_MARKER_NO_ACTIVE_CHAT_MARKER =
            () -> prefixed(text("You have no active chat marker set!", RED));
    Args1<String> CHAT_MARKER_NOT_UNLOCKED = markerName -> prefixed(text().color(RED)
            .append(text("You did not unlock chat marker "))
            .append(text(markerName, style(BOLD)))
            .append(text(" yet.")));
    Args1<String> CHAT_MARKER_REVOKED = markerName -> prefixed(text().color(RED)
            .append(text("Your chat marker "))
            .append(text(markerName, style(BOLD)))
            .append(text(" has been revoked.")));
    Args2<String, String> CHAT_MARKER_REVOKED_SUCCESS = (username, markerName) -> prefixed(
            text().color(GREEN)
                    .append(text("Successfully revoked chat marker "))
                    .append(text(markerName, style(BOLD)))
                    .append(text(" from user "))
                    .append(text(username, style(BOLD)))
                    .append(text('.')));
    Args1<String> CHAT_MARKER_SET = markerName -> prefixed(text().color(GOLD)
            .append(text("Your active chat marker has been set to "))
            .append(text(markerName, style(BOLD)))
            .append(text('.')));
    Args0 CHAT_MARKER_UNSET = () -> prefixed(text().color(GOLD)
            .append(text("Your active chat marker has been unset.")));
    Args1<String> CHAT_MARKER_UNSET_SUCCESS = name -> prefixed(text().color(GREEN)
            .append(text("The active chat marker of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unset.")));
    Args2<String, String> CHAT_MARKER_USER_ALREADY_UNLOCKED = (username, markerName) -> prefixed(
            text().color(RED)
                    .append(text("User "))
                    .append(text(username, style(BOLD)))
                    .append(text(" already unlocked chat marker "))
                    .append(text(markerName, style(BOLD)))
                    .append(text('.')));
    Args1<String> CHAT_MARKER_USER_NO_ACTIVE_CHAT_MARKER = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has no active chat marker set.", RED)));
    Args2<String, String> CHAT_MARKER_USER_NOT_UNLOCKED = (username, markerName) -> prefixed(
            text().color(RED)
                    .append(text("User "))
                    .append(text(username, style(BOLD)))
                    .append(text(" did not unlock chat marker "))
                    .append(text(markerName, style(BOLD)))
                    .append(text(" yet.")));
    Args2<String, String> CHAT_MARKER_USER_SET = (username, markerName) -> prefixed(
            text().color(GOLD)
                    .append(text("The active chat marker of user "))
                    .append(text(username, style(BOLD)))
                    .append(text(" has been set to "))
                    .append(text(markerName, style(BOLD)))
                    .append(text('.')));
    Args1<String> CRATE_ALREADY_EXISTS = crateName -> prefixed(text().color(RED)
            .append(text("Crate "))
            .append(text(crateName, style(BOLD)))
            .append(text(" already exists.")));
    MultipleArgs4<DirtCorePlugin, Sender, CrateEntity, CrateContentEntity> CRATE_CONTENT_ADDED =
            (plugin, sender, crate, crateContent) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Content for crate "))
                                .append(text(crate.getName(), style(BOLD)))
                                .append(text(" has been added.")).build());

                crateContent.onRenderDetails(plugin, sender, components, crate.getTotalTickets());
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args3<Long, Long, String> CRATE_CONTENT_COMMAND_DOES_NOT_EXIST =
            (commandId, contentId, crateName) -> prefixed(text().color(RED)
                    .append(text("Command with id "))
                    .append(text(commandId, style(BOLD)))
                    .append(text(" for content with id "))
                    .append(text(contentId, style(BOLD)))
                    .append(text(" for crate "))
                    .append(text(crateName, style(BOLD)))
                    .append(text(" does not exist.")));
    Args2<Long, String> CRATE_CONTENT_DOES_NOT_EXIST = (contentId, crateName) -> prefixed(
            text().color(RED)
                    .append(text("Content with id "))
                    .append(text(contentId, style(BOLD)))
                    .append(text(" for crate "))
                    .append(text(crateName, style(BOLD)))
                    .append(text(" does not exist.")));
    MultipleArgs4<DirtCorePlugin, Sender, CrateEntity, CrateContentEntity> CRATE_CONTENT_UPDATED =
            (plugin, sender, crate, crateContent) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Content with id "))
                                .append(text(crateContent.getId(), style(BOLD)))
                                .append(text(" for crate "))
                                .append(text(crate.getName(), style(BOLD)))
                                .append(text(" has been updated.")).build());

                crateContent.onRenderDetails(plugin, sender, components, crate.getTotalTickets());
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    MultipleArgs3<DirtCorePlugin, Sender, CrateEntity> CRATE_CREATED = (plugin, sender, crate) -> {
        final ImmutableList.Builder<Component> components =
                new ImmutableList.Builder<Component>().add(text().color(GREEN)
                        .append(text("Crate "))
                        .append(text(crate.getName(), style(BOLD)))
                        .append(text(" has been created.")).build());

        crate.onRenderDetails(plugin, sender, components);
        return components.build().stream().map(Components::prefixed)
                .collect(ImmutableCollectors.toList());
    };
    Args1<String> CRATE_DELETED = crateName -> prefixed(text().color(GREEN)
            .append(text("Crate "))
            .append(text(crateName, style(BOLD)))
            .append(text(" has been deleted.")));
    Args1<String> CRATE_DOES_NOT_EXIST = crateName -> prefixed(text().color(RED)
            .append(text("Crate "))
            .append(text(crateName, style(BOLD)))
            .append(text(" does not exist.")));
    Args1<ItemStack> CRATE_KEY_GIVEN = key -> prefixed(text().color(GREEN)
            .append(key.asDisplayComponent(true))
            .append(text(" has been given to players.")));
    Args1<ItemStack> CRATE_KEY_NO_KEY = key -> prefixed(text().color(RED)
            .append(text("You are missing a "))
            .append(key.asDisplayComponent(false))
            .append(text(" to open this crate.")));
    Args2<String, String> CRATE_KEY_NOT_AN_ITEM = (keyIdentifier, crateName) -> prefixed(
            text().color(RED)
                    .append(text("Key "))
                    .append(text(keyIdentifier, style(BOLD)))
                    .append(text(" for crate "))
                    .append(text(crateName, style(BOLD)))
                    .append(text(" is not an item!")));
    Args1<String> CRATE_NO_REWARDS = crateName -> prefixed(text().color(RED)
            .append(text("Crate "))
            .append(text(crateName, style(BOLD)))
            .append(text(" does not have any rewards.")));
    Args1<String> CRATE_KEY_NOT_SET = crateName -> prefixed(text().color(RED)
            .append(text("Crate "))
            .append(text(crateName, style(BOLD)))
            .append(text(" does not have a key set.")));
    Args1<ItemStack> CRATE_KEY_RECEIVED = key -> prefixed(text().color(GREEN)
            .append(text("You received "))
            .append(key.asDisplayComponent(true))
            .append(text('.')));
    Args3<Vec3i, World, String> CRATE_LOCATION_ALREADY_IN_USE = (pos, world, crateName) -> prefixed(
            text().color(RED)
                    .append(text("Location "))
                    .append(text(pos.toShortString(), style(BOLD)))
                    .append(text(" in world "))
                    .append(text(world.getIdentifier(), style(BOLD)))
                    .append(text(" already in use by crate "))
                    .append(text(crateName, style(BOLD)))
                    .append(text('.')));
    Args2<Vec3i, World> CRATE_LOCATION_NO_CRATE = (pos, world) -> prefixed(text().color(RED)
            .append(text("There is no crate at "))
            .append(text(pos.toShortString(), style(BOLD)))
            .append(text(" in world "))
            .append(text(world.getIdentifier(), style(BOLD)))
            .append(text('.')));
    Args3<Player, Component, ItemStack> CRATE_REWARD_RECEIVED =
            (player, crateTitle, itemStack) -> prefixed(
                    text().append(player.getDisplayName().style(Style.style(BOLD)))
                            .append(text(" opened a ", GOLD))
                            .append(crateTitle)
                            .append(text(" and received ", GOLD))
                            .append(itemStack.asDisplayComponent(true))
                            .append(text('.', GOLD)));
    Args2<Component, ItemStack> CRATE_REWARD_RECEIVED_SELF = (crateTitle, itemStack) -> prefixed(
            text().append(text("You opened a ", GOLD))
                    .append(crateTitle)
                    .append(text(" and received ", GOLD))
                    .append(itemStack.asDisplayComponent(true))
                    .append(text('.', GOLD)));
    MultipleArgs3<DirtCorePlugin, Sender, CrateEntity> CRATE_UPDATED = (plugin, sender, crate) -> {
        final ImmutableList.Builder<Component> components =
                new ImmutableList.Builder<Component>().add(text().color(GREEN)
                        .append(text("Crate "))
                        .append(text(crate.getName(), style(BOLD)))
                        .append(text(" has been updated.")).build());

        crate.onRenderDetails(plugin, sender, components);
        return components.build().stream().map(Components::prefixed)
                .collect(ImmutableCollectors.toList());
    };
    Args1<String> DISCORD_LINK = link -> prefixed(text(link, AQUA).hoverEvent(
                    HoverEvent.showText(text("Click to join the discord!", GRAY)))
            .clickEvent(ClickEvent.openUrl(link)));
    MultipleArgs2<Integer, HoverEvent<Component>> ENTITY_ZAP_CONFIRM = (total, summary) -> {
        final ImmutableList.Builder<Component> components = new ImmutableList.Builder<>();

        components.add(prefixed(text().color(GOLD)
                .append(text().color(GOLD)
                        .append(text("You are about to remove entities: "))
                        .append(text().style(
                                                Style.style(NamedTextColor.DARK_GRAY,
                                                        TextDecoration.ITALIC))
                                        .append(Component.text('['))
                                        .append(Component.text("Confirm", NamedTextColor.GREEN))
                                        .append(Component.text(']')).hoverEvent(HoverEvent.showText(
                                                Component.text("Click to confirm.",
                                                        NamedTextColor.GREEN)))
                                /* .clickEvent(ClickEvent.runCommand(
                                        EntityZapCommand.COMMAND_ENTITY_ZAP_CONFIRM)) */))));
        components.add(prefixed(text().color(GOLD)
                .append(text().color(GOLD)
                        .append(text("Entity estimation: "))
                        .append(text(total, WHITE))
                        .append(text(" - "))
                        .append(text().style(
                                        Style.style(NamedTextColor.DARK_GRAY,
                                                TextDecoration.ITALIC))
                                .append(Component.text('['))
                                .append(Component.text("Summary", NamedTextColor.GRAY))
                                        .append(Component.text(']')).hoverEvent(summary)))));

        return components.build();
    };
    Args2<Integer, HoverEvent<Component>> ENTITY_ZAP_ENTITIES_REMOVED =
            (total, summary) -> prefixed(text().color(GREEN)
                    .append(text(total, Style.style(BOLD)))
                    .append(text(" entities have been removed: ", GREEN))
                    .append(text().style(
                                    Style.style(NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
                            .append(Component.text('['))
                            .append(Component.text("Summary", NamedTextColor.GRAY))
                                    .append(Component.text(']')).hoverEvent(summary)));
    Args0 ENTITY_ZAP_NO_ENTITIES_IN_RANGE =
            () -> prefixed(text("There are no entities in range.", RED));
    MultipleArgs0 ENTITY_ZAP_NO_ENTITIES_IN_RANGE_EXCLUSION = () -> {
        final ImmutableList.Builder<Component> components = new ImmutableList.Builder<>();

        components.add(
                prefixed(text("There are no entities in range that can be mass removed.", RED)));
        components.add(prefixed(text("Use the --type option to remove these entities.", RED)));

        return components.build();
    };
    Args1<String> ENTITY_ZAP_INVALID_TYPE = type -> prefixed(text().color(RED)
            .append(text("Invalid entity type: "))
            .append(text(type)));
    Args0 ENTITY_ZAP_NO_PENDING_ENTITYZAP =
            () -> prefixed(text("You have no pending entity zap to be confirmed.", RED));
    Args0 JOIN_MESSAGE_NO_MESSAGE = () -> prefixed(text("You have no join message set.", RED));
    Args0 JOIN_MESSAGE_UNSET =
            () -> prefixed(text().append(text("Your join message has been unset.", GOLD)));
    Args1<String> JOIN_MESSAGE_UNSET_SUCCESS = (name) -> prefixed(text().color(GREEN)
            .append(text("Join message of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unset.")));
    Args1<String> JOIN_MESSAGE_USER_NO_MESSAGE = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has no join message set.")));
    Args1<String> KICK_FAILED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" could not be kicked.")));
    Args1<String> KICK_USER_CAN_NOT_BE_KICKED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" can not be kicked.")));
    Args2<@NonNull KickEntity, @NonNull String> KICK_SCREEN_INCIDENT =
            (kick, author) -> getDefaultPunishmentComponent("You have been kicked!", 0, 0, kick,
                    author);
    @SuppressWarnings("unused") // used in legacy
    Args2<@NonNull KickEntity, @NonNull String> KICK_SCREEN_INCIDENT_LEGACY =
            (kick, author) -> getDefaultPunishmentComponent("You have been kicked!", 0, 4, kick,
                    author);
    Args2<@NonNull String, @NonNull String> KICK_SCREEN_NO_INCIDENT =
            (reason, author) -> getDefaultPunishmentComponent("You have been kicked!", 0, 0, reason,
                    null, author, null);
    @SuppressWarnings("unused") // used in legacy
    Args2<@NonNull String, @NonNull String> KICK_SCREEN_NO_INCIDENT_LEGACY =
            (reason, author) -> getDefaultPunishmentComponent("You have been kicked!", 0, 4, reason,
                    null, author, null);
    Args1<String> KIT_ALREADY_EXISTS = kitName -> prefixed(text().color(RED)
            .append(text("Kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" already exists.")));
    Args1<KitEntity> KIT_CLAIM_NO_PERMISSION = kit -> prefixed(text().color(RED)
            .append(text("You are not allowed to claim kit "))
            .append(kit.getDisplayNameAsComponent())
            .append(text('.')));
    Args2<KitEntity, Instant> KIT_CLAIM_NOT_YET = (kit, claimableAt) -> prefixed(text().color(RED)
            .append(text("Kit "))
            .append(kit.getDisplayNameAsComponent())
            .append(text(" can be claimed in "))
            .append(text(FormatUtils.formatDateDiff(Instant.now(), claimableAt, false,
                    false)).hoverEvent(
                    HoverEvent.showText(text(FormatUtils.formatDate(claimableAt), BLUE))))
            .append(text('.')));
    Args1<KitEntity> KIT_CLAIM_ONLY_ONCE = kit -> prefixed(text().color(RED)
            .append(text("Kit "))
            .append(kit.getDisplayNameAsComponent())
            .append(text(" can only be claimed once.")));
    Args1<KitEntity> KIT_CLAIMED = kit -> prefixed(text().color(GREEN)
            .append(text("Kit "))
            .append(kit.getDisplayNameAsComponent())
            .append(text(" has been claimed. ")));
    Args2<String, String> KIT_COOLDOWNS_CLEAR = (kitName, username) -> prefixed(text().color(GOLD)
            .append(text("Cooldown for kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" has been cleared for user "))
            .append(text(username, style(BOLD)))
            .append(text('.')));
    Args1<String> KIT_COOLDOWNS_CLEAR_ALL = username -> prefixed(text().color(GOLD)
            .append(text("Cooldowns for all kits have been cleared for user "))
            .append(text(username, style(BOLD)))
            .append(text('.')));
    Args1<String> KIT_COOLDOWNS_WIPE = kitName -> prefixed(text().color(GOLD)
            .append(text("Cooldowns for kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" have been wiped.")));
    Args0 KIT_COOLDOWNS_WIPE_ALL = () -> prefixed(text().color(GOLD)
            .append(text("Cooldowns for all kits have been wiped.")));
    MultipleArgs2<Sender, KitEntity> KIT_CREATED = (sender, kit) -> {
        final ImmutableList.Builder<Component> components =
                new ImmutableList.Builder<Component>().add(text().color(GREEN)
                        .append(text("Kit "))
                        .append(text(kit.getName(), style(BOLD)))
                        .append(text(" has been created.")).build());

        kit.onRenderDetails(sender, components);
        return components.build().stream().map(Components::prefixed)
                .collect(ImmutableCollectors.toList());
    };
    Args0 KIT_CREATION_NO_ITEMS = () -> prefixed(text().color(RED)
            .append(text("Kit could not be created. At least one item is needed.")));
    Args1<String> KIT_DELETED = kitName -> prefixed(text().color(GREEN)
            .append(text("Kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" has been deleted.")));
    Args1<String> KIT_DOES_NOT_EXIST = kitName -> prefixed(text().color(RED)
            .append(text("Kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" does not exist.")));
    Args1<String> KIT_NO_ITEMS = kitName -> prefixed(text().color(RED)
            .append(text("Kit "))
            .append(text(kitName, style(BOLD)))
            .append(text(" has no items.")));
    MultipleArgs2<Sender, KitEntity> KIT_UPDATED = (sender, kit) -> {
        final ImmutableList.Builder<Component> components =
                new ImmutableList.Builder<Component>().add(text().color(GREEN)
                        .append(text("Kit "))
                        .append(text(kit.getName(), style(BOLD)))
                        .append(text(" has been updated.")).build());

        kit.onRenderDetails(sender, components);
        return components.build().stream().map(Components::prefixed)
                .collect(ImmutableCollectors.toList());
    };
    Args0 LEAVE_MESSAGE_NO_MESSAGE = () -> prefixed(text("You have no leave message set.", RED));
    Args0 LEAVE_MESSAGE_UNSET =
            () -> prefixed(text().append(text("Your leave message has been unset.", GOLD)));
    Args1<String> LEAVE_MESSAGE_UNSET_SUCCESS = (name) -> prefixed(text().color(GREEN)
            .append(text("Leave message of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unset.")));
    Args1<String> LEAVE_MESSAGE_USER_NO_MESSAGE = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has no leave message set.")));
    Args1<Limitable> LIMIT_BLOCK_ALREADY_LIMITED = limitable -> prefixed(text().color(RED)
            .append(text("Block "))
            .append(text(limitable.getIdentifier(), style(BOLD)))
            .append(text(" is already limited.")));
    MultipleArgs3<Sender, Limitable, LimitedBlockEntity> LIMIT_BLOCK_ADDED =
            (sender, limitable, limitedBlock) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Block "))
                                .append(text(limitable.getIdentifier(), style(BOLD)))
                                .append(text(" has been limited.")).build());

                limitedBlock.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args2<Block, LimitManager.Result> LIMIT_BLOCK_LIMITED = (block, result) -> prefixed(
            text().color(RED)
                    .append(text("You can not place any more of "))
                    .append(text(block.asString(false), style(BOLD)))
                    .append(text(". You have reached the limit! Rule: "))
                    .append(text(result.getRule().name()))
                    .append(text(" ("))
                    .append(text(result.getAmount()))
                    .append(text('/'))
                    .append(text(result.getMax()))
                    .append(text(')')));
    Args1<Limitable> LIMIT_BLOCK_NOT_LIMITED = limitable -> prefixed(text().color(RED)
            .append(text("Block "))
            .append(text(limitable.getIdentifier(), style(BOLD)))
            .append(text(" is not limited.")));
    Args1<Limitable> LIMIT_BLOCK_REMOVED = limitable -> prefixed(text().color(GREEN)
            .append(text("Limited block "))
            .append(text(limitable.getIdentifier(), style(BOLD)))
            .append(text(" has been removed.")));
    MultipleArgs3<Sender, Limitable, LimitedBlockEntity> LIMIT_BLOCK_UPDATED =
            (sender, limitable, limitedBlock) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Limit for block "))
                                .append(text(limitable.getIdentifier(), style(BOLD)))
                                .append(text(" has been updated.")).build());

                limitedBlock.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args2<String, Boolean> LIMIT_BYPASS = (name, flag) -> prefixed(text().color(GRAY)
            .append(text("Limit bypass", GOLD))
            .append(text(" of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been "))
            .append(flag ? ENABLED : DISABLED)
            .append(text('.')));
    Args1<Boolean> LIMIT_BYPASS_RESPONSE = flag -> prefixed(text().color(GRAY)
            .append(text("Limit bypass", GOLD))
            .append(text(" has been "))
            .append(flag ? ENABLED : DISABLED)
            .append(text('.')));
    Args2<BlockPos, World> LIMIT_ENTRIES_REMOVED = (blockPos, world) -> prefixed(text().color(GREEN)
            .append(text("All entries at "))
            .append(text(blockPos.toShortString(), style(BOLD)))
            .append(text(" in world "))
            .append(text(world.getIdentifier(), style(BOLD)))
            .append(text(" have been removed.")));
    Args1<LimitManager.Rule> LIMIT_RULE_NOT_INCLUDED = rule -> prefixed(text().color(RED)
            .append(text("Limit does not include rule "))
            .append(text(rule.name(), style(BOLD)))
            .append(text('.')));
    Args1<String> KICK_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been kicked from the server.")));
    Args1<String> MUTE_MODIFIED = name -> prefixed(text().color(GREEN)
            .append(text("Mute of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been modified.")));
    Args0 MUTE_CHANGES_NEEDED = () -> prefixed(text().color(RED)
            .append(text("No changes have been made. Mute has not been modified.")));
    Args1<String> MUTE_USER_CAN_NOT_BE_MUTED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" can not be muted.")));
    Args2<@NonNull MuteEntity, @NonNull String> MUTE_REASON =
            (mute, author) -> getDefaultPunishmentComponent("You have been muted!", 2, 0, mute,
                    author);
    Args1<String> MUTE_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been muted.")));
    Args2<@NonNull MuteEntity, @NonNull String> MUTE_STILL_MUTED =
            (mute, author) -> getDefaultPunishmentComponent(
                    "You have been muted. You are not allowed to talk!", 2, 0, mute, author);
    Args1<String> PREFIX_DOES_NOT_EXIST = prefixName -> prefixed(text().color(RED)
            .append(text("Prefix "))
            .append(text(prefixName, style(BOLD)))
            .append(text(" does not exist.")));
    Args1<String> PREFIX_GRANTED = prefixName -> prefixed(text().color(GOLD)
            .append(text("You have been granted the prefix "))
            .append(text(prefixName, style(BOLD)))
            .append(text('.')));
    Args2<String, String> PREFIX_GRANTED_SUCCESS = (username, prefixName) -> prefixed(
            text().color(GREEN)
                    .append(text("Successfully granted user "))
                    .append(text(username, style(BOLD)))
                    .append(text(" the prefix "))
                    .append(text(prefixName, style(BOLD)))
                    .append(text('.')));
    Args0 PREFIX_NO_ACTIVE_PREFIX = () -> prefixed(text("You have no active prefix set!", RED));
    Args1<String> PREFIX_NOT_UNLOCKED = prefixName -> prefixed(text().color(RED)
            .append(text("You did not unlock prefix "))
            .append(text(prefixName, style(BOLD)))
            .append(text(" yet.")));
    Args1<String> PREFIX_REVOKED = prefixName -> prefixed(text().color(RED)
            .append(text("Your prefix "))
            .append(text(prefixName, style(BOLD)))
            .append(text(" has been revoked.")));
    Args2<String, String> PREFIX_REVOKED_SUCCESS = (username, prefixName) -> prefixed(
            text().color(GREEN)
                    .append(text("Successfully revoked prefix "))
                    .append(text(prefixName, style(BOLD)))
                    .append(text(" from user "))
                    .append(text(username, style(BOLD)))
                    .append(text('.')));
    Args1<String> PREFIX_SET = prefixName -> prefixed(text().color(GOLD)
            .append(text("Your active prefix has been set to "))
            .append(text(prefixName, style(BOLD)))
            .append(text('.')));
    Args0 PREFIX_UNSET = () -> prefixed(text().color(GOLD)
            .append(text("Your active prefix has been unset.")));
    Args1<String> PREFIX_UNSET_SUCCESS = name -> prefixed(text().color(GREEN)
            .append(text("The active prefix of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unset.")));
    Args2<String, String> PREFIX_USER_ALREADY_UNLOCKED = (username, prefixName) -> prefixed(
            text().color(RED)
                    .append(text("User "))
                    .append(text(username, style(BOLD)))
                    .append(text(" already unlocked prefix "))
                    .append(text(prefixName, style(BOLD)))
                    .append(text('.')));
    Args1<String> PREFIX_USER_NO_ACTIVE_PREFIX = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has no active prefix set.", RED)));
    Args2<String, String> PREFIX_USER_NOT_UNLOCKED = (username, prefixName) -> prefixed(
            text().color(RED)
                    .append(text("User "))
                    .append(text(username, style(BOLD)))
                    .append(text(" did not unlock prefix "))
                    .append(text(prefixName, style(BOLD)))
                    .append(text(" yet.")));
    Args2<String, String> PREFIX_USER_SET = (username, prefixName) -> prefixed(text().color(GOLD)
            .append(text("The active prefix of user "))
            .append(text(username, style(BOLD)))
            .append(text(" has been set to "))
            .append(text(prefixName, style(BOLD)))
            .append(text('.')));
    MultipleArgs2<World, Vec2i> PROFILE_CHUNK_COULD_NOT_BE_PROFILED = (world, chunkPos) -> {
        final ImmutableList.Builder<Component> components = ImmutableList.builder();

        components.add(prefixed(text().color(RED)
                .append(text("Chunk "))
                .append(text(chunkPos.toShortString(), Style.style(BOLD)))
                .append(text(" in world "))
                .append(text(world.getIdentifier(), Style.style(BOLD)))
                .append(text(" could not be profiled."))));
        components.add(prefixed(text("Make sure the chunk exists and is loaded.", RED)));

        return components.build();
    };
    Args0 REPLY_NO_RECEIVER = () -> prefixed(text("You have no one to reply to!", RED));
    Args1<String> REPLY_RECEIVER_OFFLINE = name -> prefixed(text().color(RED)
            .append(text("Receiver "))
            .append(text(name, style(BOLD)))
            .append(text(" is offline!")));
    Args1<String> RESTRICT_ACCESS_TYPE_ALREADY_SET = typeName -> prefixed(text().color(RED)
            .append(text("Restriction already has access control type "))
            .append(text(typeName, style(BOLD)))
            .append(text('.')));
    Args1<String> RESTRICT_ACCESS_TYPE_NOT_FOUND = typeName -> prefixed(text().color(RED)
            .append(text("Could not find access control type with name "))
            .append(text(typeName, style(BOLD)))
            .append(text('!')));
    Args1<RestrictionManager.Action> RESTRICT_ACTION_ALREADY_INCLUDED = action -> prefixed(
            text().color(RED)
                    .append(text("Restriction already includes action "))
                    .append(text(action.name(), style(BOLD)))
                    .append(text('.')));
    Args1<String> RESTRICT_ACTION_NOT_FOUND = actionName -> prefixed(text().color(RED)
            .append(text("Could not find action with name "))
            .append(text(actionName, style(BOLD)))
            .append(text('!')));
    Args1<RestrictionManager.Action> RESTRICT_ACTION_NOT_INCLUDED = action -> prefixed(
            text().color(RED)
                    .append(text("Restriction does not include action "))
                    .append(text(action.name(), style(BOLD)))
                    .append(text('.')));
    Args1<ItemInfoProvider> RESTRICT_ALTERNATIVE_ALREADY_INCLUDED = restrictable -> prefixed(
            text().color(RED)
                    .append(text("Restriction already includes alternative "))
                    .append(text(restrictable.asString(true), style(BOLD)))
                    .append(text('.')));
    Args1<ItemInfoProvider> RESTRICT_ALTERNATIVE_NOT_INCLUDED = restrictable -> prefixed(
            text().color(RED)
                    .append(text("Restriction does not include alternative "))
                    .append(text(restrictable.asString(true), style(BOLD)))
                    .append(text('.')));
    Args2<Block, RestrictionManager.Result> RESTRICT_BLOCK_RESTRICTED = (block, result) -> prefixed(
            text().color(RED)
                    .append(text("Block "))
                    .append(text(block.asString(
                                    result == RestrictionManager.Result.RESTRICTED_PERSISTENT_DATA),
                            style(BOLD)))
                    .append(text(" is restricted!")));
    Args2<String, Boolean> RESTRICT_BYPASS = (name, flag) -> prefixed(text().color(GRAY)
            .append(text("Restriction bypass", GOLD))
            .append(text(" of user "))
            .append(text(name, style(BOLD)))
            .append(text(" has been "))
            .append(flag ? ENABLED : DISABLED)
            .append(text('.')));
    Args1<Boolean> RESTRICT_BYPASS_RESPONSE = flag -> prefixed(text().color(GRAY)
            .append(text("Restriction bypass", GOLD))
            .append(text(" has been "))
            .append(flag ? ENABLED : DISABLED)
            .append(text('.')));
    Args1<String> RESTRICT_ITEM_ALREADY_RESTRICTED = name -> prefixed(text().color(RED)
            .append(text("Item "))
            .append(text(name, style(BOLD)))
            .append(text(" is already restricted.")));
    MultipleArgs3<Sender, ItemInfoProvider, RestrictedItemEntity> RESTRICT_ITEM_ADDED =
            (sender, restrictable, restrictedItem) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Item "))
                                .append(text(restrictable.asString(false), style(BOLD)))
                                .append(text(" has been restricted.")).build());

                restrictedItem.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args1<ItemInfoProvider> RESTRICT_ITEM_NO_PERSISTENT_DATA = restrictable -> prefixed(
            text().color(RED)
                    .append(text("Restricted item "))
                    .append(text(restrictable.asString(false), style(BOLD)))
                    .append(text(" does not have any persistent data set.")));
    Args2<ItemInfoProvider, Boolean> RESTRICT_ITEM_NOT_RESTRICTED =
            (restrictable, includePersistentData) -> prefixed(text().color(RED)
                    .append(text("Item "))
                    .append(text(restrictable.asString(includePersistentData), style(BOLD)))
                    .append(text(" is not restricted.")));
    Args2<ItemInfoProvider, Boolean> RESTRICT_ITEM_REMOVED =
            (restrictable, includePersistentData) -> prefixed(text().color(GREEN)
                    .append(text("Restricted item "))
                    .append(text(restrictable.asString(false), style(BOLD)))
                    .append(text(" has been removed.")));
    MultipleArgs4<Sender, ItemInfoProvider, RestrictedItemEntity, Boolean> RESTRICT_ITEM_UPDATED =
            (sender, restrictable, restrictedItem, includePersistentData) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Restriction for item "))
                                .append(text(restrictable.asString(includePersistentData),
                                        style(BOLD)))
                                .append(text(" has been updated.")).build());

                restrictedItem.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args2<ItemStack, RestrictionManager.Result> RESTRICT_ITEM_RESTRICTED =
            (itemStack, result) -> prefixed(text().color(RED)
                    .append(text("Item "))
                    .append(text(itemStack.asString(
                                    result == RestrictionManager.Result.RESTRICTED_PERSISTENT_DATA),
                            style(BOLD)))
                    .append(text(" is restricted!")));
    Args1<String> RESTRICT_MOD_ALREADY_RESTRICTED = modName -> prefixed(text().color(RED)
            .append(text("Mod "))
            .append(text(modName, style(BOLD)))
            .append(text(" is already restricted.")));
    MultipleArgs3<Sender, String, RestrictedModEntity> RESTRICT_MOD_ADDED =
            (sender, modName, restrictedItem) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Mod "))
                                .append(text(modName, style(BOLD)))
                                .append(text(" has been restricted.")).build());

                restrictedItem.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args1<String> RESTRICT_MOD_NOT_FOUND = modName -> prefixed(text().color(RED)
            .append(text("Mod "))
            .append(text(modName, style(BOLD)))
            .append(text(" could not be found.")));
    Args1<String> RESTRICT_MOD_NOT_RESTRICTED = modName -> prefixed(text().color(RED)
            .append(text("Mod "))
            .append(text(modName, style(BOLD)))
            .append(text(" is not restricted.")));
    Args1<String> RESTRICT_MOD_REMOVED = modName -> prefixed(text().color(GREEN)
            .append(text("Restricted mod "))
            .append(text(modName, style(BOLD)))
            .append(text(" has been removed.")));
    MultipleArgs3<Sender, String, RestrictedModEntity> RESTRICT_MOD_UPDATED =
            (sender, modName, restrictedItem) -> {
                final ImmutableList.Builder<Component> components =
                        new ImmutableList.Builder<Component>().add(text().color(GREEN)
                                .append(text("Restriction for mod "))
                                .append(text(modName, style(BOLD)))
                                .append(text(" has been updated.")).build());

                restrictedItem.onRenderDetails(sender, components);
                return components.build().stream().map(Components::prefixed)
                        .collect(ImmutableCollectors.toList());
            };
    Args1<World> RESTRICT_WORLD_ALREADY_INCLUDED = world -> prefixed(text().color(RED)
            .append(text("Restriction already includes world "))
            .append(text(world.getIdentifier(), style(BOLD)))
            .append(text('.')));
    Args1<World> RESTRICT_WORLD_NOT_INCLUDED = world -> prefixed(text().color(RED)
            .append(text("Restriction does not include world "))
            .append(text(world.getIdentifier(), style(BOLD)))
            .append(text('.')));
    Args1<Boolean> SOCIAL_SPY_RESPONSE = flag -> prefixed(text().color(GRAY)
            .append(text("SocialSpy", GOLD))
            .append(text(" has been "))
            .append(flag ? ENABLED : DISABLED)
            .append(text('.')));
    Args1<String> STORE_LINK = link -> prefixed(text(link, GOLD).hoverEvent(
                    HoverEvent.showText(text("Click to visit out store!", AQUA)))
            .clickEvent(ClickEvent.openUrl(link)));
    Args2<World, Vec2i> TELEPORT_CHUNK = (world, chunkPos) -> prefixed(text().color(GOLD)
            .append(text("You have been teleported to chunk "))
            .append(text(chunkPos.toShortString(), Style.style(BOLD)))
            .append(text(" in world "))
            .append(text(world.getIdentifier(), Style.style(BOLD)))
            .append(text('.')));
    Args2<World, Vec2i> TELEPORT_CHUNK_OTHERS = (world, chunkPos) -> prefixed(text().color(GOLD)
            .append(text("Players have been teleported to chunk "))
            .append(text(chunkPos.toShortString(), Style.style(BOLD)))
            .append(text(" in world "))
            .append(text(world.getIdentifier(), Style.style(BOLD)))
            .append(text('.')));
    Args2<World, Vec2i> TELEPORT_CHUNK_OTHERS_FAILED = (world, chunkPos) -> prefixed(
            text().color(RED)
                    .append(text("Could not teleport all players to chunk "))
                    .append(text(chunkPos.toShortString(), Style.style(BOLD)))
                    .append(text(" in world "))
                    .append(text(world.getIdentifier(), Style.style(BOLD)))
                    .append(text('.')));
    Args2<Long, String> TIME_SET = (time, world) -> prefixed(text().color(GREEN)
            .append(text("Time has been set to "))
            .append(text(time, style(BOLD)))
            .append(text(" in world "))
            .append(text(world, style(BOLD)))
            .append(text('.')));
    Args1<String> TIME_SET_FAILED = world -> prefixed(text().color(RED)
            .append(text("Failed to set time in world "))
            .append(text(world, style(BOLD)))
            .append(text('.')));
    Args1<String> UNBAN_FAILED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" could not be unbanned.")));
    Args1<String> UNBAN_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unbanned.")));
    Args1<String> UNBAN_USER_NOT_BANNED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" is not banned.")));
    Args1<String> UNBAN_IP_USER_NOT_IP_BANNED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" is not IP banned.")));
    Args1<String> UNMUTE_FAILED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" could not be unmuted.")));
    Args1<String> UNMUTE_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been unmuted.")));
    Args1<String> UNMUTE_USER_NOT_MUTED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" is not muted.")));
    Args2<@NonNull UnmuteEntity, @NonNull String> UNMUTE_REASON =
            (unmute, author) -> getDefaultPunishmentComponent(text("You have been unmuted!", GREEN),
                    2, 0, unmute, author);
    Args0 UNVERIFY_NOT_VERIFIED = () -> prefixed(text().color(RED)
            .append(text("You are not verified.")));
    Args1<String> UNVERIFY_SUCCESS = name -> prefixed(text().color(GREEN)
            .append(text("Successfully unlinked your Minecraft account from Discord user "))
            .append(text(name, style(BOLD)))
            .append(text('.')));
    Args1<String> VERIFY_ALREADY_VERIFIED = name -> prefixed(text().color(RED)
            .append(text("Your Minecraft account is already linked to Discord user "))
            .append(text(name, style(BOLD)))
            .append(text('.')));
    Args0 VERIFY_CODE_INVALID = () -> prefixed(text().color(RED)
            .append(text("The code you provided is invalid.")));
    MultipleArgs1<DirtCorePlugin> VERIFY_INFO_NOT_VERIFIED = plugin -> {
        ImmutableList.Builder<Component> builder = ImmutableList.<Component>builder()
                .add(text("Link your Discord and Minecraft accounts:", GOLD))
                .add(text().color(GRAY)
                        .append(text(" - ", GOLD))
                        .append(text("Join our Discord: "))
                        .append(text(plugin.getConfiguration().get(ConfigKeys.DISCORD_LINK)))
                        .build())
                .add(text().color(GRAY)
                        .append(text(" - ", GOLD))
                        .append(text("Go to channel "))
                        .append(text("#verification", DARK_AQUA))
                        .append(text('.')).build())
                .add(text().color(GRAY)
                        .append(text(" - ", GOLD))
                        .append(text("Click on the "))
                        .append(text("Verify", DARK_AQUA))
                        .append(text(" button.")).build())
                .add(text().color(GRAY)
                        .append(text(" - ", GOLD))
                        .append(text("Run the command: "))
                        .append(text("/verify <code>", DARK_AQUA))
                        .append(text(" with the provided code.")).build());

        return builder.build().stream().map(Components::prefixed).collect(Collectors.toList());
    };
    Args1<String> VERIFY_INFO_VERIFIED = name -> prefixed(text().color(GOLD)
            .append(text("Your Minecraft account is linked to Discord user "))
            .append(text(name, style(BOLD)))
            .append(text('.')));
    Args1<String> VERIFY_SUCCESS = name -> prefixed(text().color(GREEN)
            .append(text("Successfully linked your Minecraft account to Discord user "))
            .append(text(name, style(BOLD)))
            .append(text('.')));
    MultipleArgs1<Integer> VOTE_COULD_NOT_CLAIM_EVERYTHING = (unclaimedVotes) -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(
                text("Could not claim everything. Your inventory does not have enough capacity!",
                        RED)));
        builder.add(prefixed(text().color(GOLD)
                .append(text("Remaining vote rewards to claim: "))
                .append(text(unclaimedVotes, Style.style(BOLD)))));

        return builder.build();
    };
    Args2<String, Integer> VOTE_EXTRA_CLAIM_REWARDS = (name, claimedVotes) -> prefixed(
            text().color(GOLD)
                    .append(text(name, Style.style(BOLD)))
                    .append(text(" received extra rewards for voting a total of "))
                    .append(text(claimedVotes, Style.style(BOLD)))
                    .append(text("x!")));
    Args2<String, Integer> VOTE_EXTRA_STREAK_REWARDS = (name, streak) -> prefixed(text().color(GOLD)
            .append(text(name, Style.style(BOLD)))
            .append(text(" received extra rewards for reaching a vote streak of "))
            .append(text(streak, Style.style(BOLD)))
            .append(text('!')));
    MultipleArgs1<Collection<String>> VOTE_LINKS = links -> {
        final List<Component> components =
                Lists.newArrayList(prefixed(text("Vote here:", DARK_AQUA)));

        links.forEach(link -> components.add(prefixed(text(" - ", AQUA).append(
                text(link, GOLD).hoverEvent(HoverEvent.showText(text("Click to vote!", AQUA)))
                        .clickEvent(ClickEvent.openUrl(link))))));

        return components;
    };
    Args0 VOTE_NO_LINKS = () -> prefixed(text("There currently is no option to vote!", RED));
    Args0 VOTE_NOTHING_TO_CLAIM = () -> prefixed(text("You have nothing to claim!", RED));
    MultipleArgs7<String, Integer, Integer, Integer, Integer, Integer, Component> VOTE_STATS =
            (name, totalVotes, claimedVotes, unclaimedVotes, voteStreak, claimedVoteStreak,
                    lastVote) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();

                builder.add(empty());
                builder.add(prefixed(text().append(text().color(GRAY)
                        .append(text("Vote stats of "))
                        .append(text(name, GOLD))
                        .append(text(':')))));
                builder.add(prefixed(text("  ").append(text().color(GRAY)
                        .append(text("Total Votes: ", GOLD))
                        .append(text(totalVotes)))));
                builder.add(prefixed(text("  ").append(text().color(GRAY)
                        .append(text("Claimed Votes: ", GOLD))
                        .append(text(claimedVotes))
                        .append(text('/'))
                        .append(text(totalVotes)))));
                builder.add(prefixed(text("  ").append(text().color(GRAY)
                        .append(text("Vote Streak: ", GOLD))
                        .append(text(voteStreak)))));
                builder.add(prefixed(text("  ").append(text().color(GRAY)
                        .append(text("Claimed Vote Streak: ", GOLD))
                        .append(text(claimedVoteStreak))
                        .append(text('/'))
                        .append(text(voteStreak)))));
                builder.add(prefixed(text("  ").append(text().color(GRAY)
                        .append(text("Last Vote: ", GOLD))
                        .append(lastVote))));
                builder.add(empty());

                return builder.build();
            };
    Args1<String> WARN_FAILED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" could not be warned.")));
    Args2<@NonNull WarnEntity, @NonNull String> WARN_REASON =
            (warn, author) -> getDefaultPunishmentComponent("You have been warned!", 2, 0, warn,
                    author);
    Args1<String> WARN_SUCCESSFUL = name -> prefixed(text().color(GREEN)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" has been warned.")));
    Args1<String> WARN_USER_CAN_NOT_BE_WARNED = name -> prefixed(text().color(RED)
            .append(text("User "))
            .append(text(name, style(BOLD)))
            .append(text(" can not be warned.")));
    MultipleArgs4<String, String, Integer, String> WORTH_INFO =
            (itemName, worthPerItem, stackSize, totalWorth) -> {
                ImmutableList.Builder<Component> builder = ImmutableList.builder();
                builder.add(prefixed(text().append(text().color(GOLD)
                        .append(text("Worth of item: ", GRAY))
                        .append(text(itemName, style(BOLD, GOLD)))
                        .append(text((stackSize > 1 ? " x" + stackSize : ""), GOLD)))));
                builder.add(prefixed(text().append(text().color(GOLD)
                                .append(text("Total Value: ", GRAY))
                                .append(text(totalWorth, style(GREEN, BOLD))))
                        .append(text(" (", WHITE))
                        .append(text(worthPerItem, style(GREEN, BOLD)))
                        .append(text(" per item", style(GOLD)))
                        .append(text(")", WHITE))));
                return builder.build();
            };
    Args2<String, String> WORTH_SET = (itemName, amount) -> prefixed(text().color(GOLD)
            .append(text("You have set the worth of "))
            .append(text(itemName, style(BOLD)))
            .append(text(" to "))
            .append(text(amount, style(GREEN, BOLD))));
    Args1<String> WORTH_NOT_SET_ERROR = (itemId) -> prefixed(text().color(RED)
            .append(text(itemId, style(GOLD, BOLD)))
            .append(text(" does not have a worth set!")));
    Args1<String> WORTH_REMOVED = (itemId) -> prefixed(text().color(GOLD)
            .append(text("You have removed worth from: "))
            .append(text(itemId, style(BOLD))));

    /*
     * LISTENERS
     */

    Args0 LOADING_STATE_ERROR = () -> prefixed(text("Data for your user was not "
            + "loaded during the pre-login stage - unable to continue. Please try again "
            + "later. If you are a server admin, please check the console for any errors.", RED));

    /*
     * LOG
     */

    Args5<@NonNull LogEntity, @NonNull User, @Nullable Identifiable, @Nullable RestrictiveAction,
            @Nullable User>
            LOG = (log, source, target, restrictiveAction, author) -> {
        ImmutableList.Builder<Component> components = getDefaultLogComponents(log, source, target);

        if (restrictiveAction != null) {
            components.add(prefixed(prefixLog(text().style(Style.style(DARK_GRAY, ITALIC))
                    .append(text('['))
                    .append(text("Hover for details", GRAY))
                    .append(text(']')).hoverEvent(
                            HoverEvent.showText(formatPunishment(restrictiveAction, author))))));
        }

        return join(JoinConfiguration.newlines(), components.build());
    };
    Args6<@NonNull LogEntity, @NonNull User, @Nullable User, @Nullable RestrictiveAction,
            @Nullable User, @Nullable User>
            LOG_BANNED_IP_JOIN =
            (log, source, target, restrictiveAction, author, punishmentTarget) -> {
                ImmutableList.Builder<Component> components =
                        getDefaultLogComponents(log, source, target);

                if (restrictiveAction != null) {
                    components.add(prefixed(prefixLog(text().style(Style.style(DARK_GRAY, ITALIC))
                            .append(text('['))
                            .append(text("Hover for details", GRAY))
                            .append(text(']')).hoverEvent(HoverEvent.showText(
                                    formatPunishment(restrictiveAction, author,
                                            punishmentTarget))))));
                }

                return join(JoinConfiguration.newlines(), components.build());
            };

    /*
     * MESSAGING
     */

    Args2<MessagingManager.ChannelType, Boolean> CHANGE_READ_CHANNEL = (channel, flag) -> prefixed(
            text().color(GRAY)
                    .append(text("Read channel "))
                    .append(text(channel.getIdentifier(), DARK_AQUA))
                    .append(text(" has been "))
                    .append(flag ? ENABLED : DISABLED)
                    .append(text('.')));
    Args0 GLOBAL_PREFIX = () -> text().color(DARK_GRAY)
            .append(text('['))
            .append(text("GLOBAL", GRAY))
            .append(text(']')).build();
    Args0 STAFF_PREFIX = () -> text().color(RED)
            .append(text('['))
            .append(text("STAFF", GOLD))
            .append(text(']')).build();
    Args2<DirtCorePlugin, Component> WELCOME_MESSAGE_DEFAULT =
            (plugin, name) -> text().color(LIGHT_PURPLE)
                    .append(JOIN_MESSAGE_PREFIX)
                    .append(text("Welcome "))
                    .append(name)
                    .append(text(" to "))
                    .append(text(plugin.getServerName()))
                    .append(text('!')).build();
    Args1<MessagingManager.ChannelType> WRITE_CHANNEL_SET = channel -> prefixed(
            text("Set write channel to: ", GRAY).append(text(channel.getIdentifier(), DARK_AQUA)));
    Args1<MessagingManager.ChannelType> WRITE_CHANNEL_NOT_MODIFIED = channel -> prefixed(
            text("Write channel already set to: ", RED).append(
                    text(channel.getIdentifier(), DARK_AQUA)));

    /*
     * VOTE
     */

    Args2<Player, Integer> VOTE_PLAYER_VOTED = (player, amount) -> prefixed(
            text().append(player.getDisplayName().style(Style.style(BOLD)))
                    .append(text().color(GOLD)
                            .append(text(" has claimed rewards for voting "))
                            .append(text(amount, Style.style(BOLD)))
                            .append(text("x!"))));
    Args2<Player, Optional<String>> VOTE_RECEIVED = (player, serviceName) -> {
        final TextComponent.Builder builder = text().color(GRAY)
                .append(Component.text(player.getName(), GOLD, BOLD))
                .append(text(" has voted"));

        serviceName.ifPresent(s -> builder.append(text(" on "))
                .append(text(s, AQUA, BOLD)));
        return prefixed(builder.append(text('.')));
    };
    Args1<Player> VOTE_RECEIVED_PLAYER = player -> prefixed(text().color(GOLD)
            .append(text("Execute: "))
            .append(text(VoteCommand.COMMAND_VOTE_CLAIM, GRAY))
            .append(text(" to claim your rewards.")));
    MultipleArgs1<Integer> VOTE_UNCLAIMED_VOTES = amount -> {
        ImmutableList.Builder<Component> builder = ImmutableList.builder();

        builder.add(prefixed(text().color(GOLD)
                .append(text("You have "))
                .append(text(amount, Style.style(BOLD)))
                .append(text(" unclaimed votes!"))));
        builder.add(prefixed(text().color(GOLD)
                .append(text("Execute: "))
                .append(text(VoteCommand.COMMAND_VOTE_CLAIM, GRAY))
                .append(text(" to claim your rewards."))));

        return builder.build();
    };

    /*
     * HELPER
     */

    @NonNull
    static TextComponent prefixed(final ComponentLike component) {
        return text().append(PREFIX)
                .append(space())
                .append(component).build();
    }

    @NonNull
    static TextComponent prefixLog(final ComponentLike component) {
        return text().append(text().color(RED)
                        .append(text("LOG").append(space())
                                .append(text('>', Style.style(BOLD)))))
                .append(space())
                .append(component).build();
    }

    static ImmutableList.@NonNull Builder<Component> getDefaultLogComponents(
            @NonNull final LogEntity log, @NonNull final User source,
            @Nullable final Identifiable target) {
        final ImmutableList.Builder<Component> components = ImmutableList.builder();

        final TextComponent.Builder header = text().color(DARK_GRAY)
                        .append(Component.text('('))
                        .append(text(source.getName() + '@' + log.getSourceServer(), YELLOW))
                        .append(Component.text(')'))
                .append(space())
                .append(text().color(DARK_GRAY)
                        .append(text('['))
                        .append(text(log.getType().getText(),
                                LogEntity.getTextColor(log.getType())))
                        .append(text(']')));

        if (target != null) {
            header.append(space())
                    .append(text().color(DARK_GRAY)
                            .append(Component.text('('))
                            .append(text(target.getName(), AQUA))
                            .append(Component.text(')')));
        }

        components.add(prefixed(prefixLog(header.build())));
        log.getTitle().ifPresent(title -> components.add(prefixed(prefixLog(text(title)))));
        log.getDescription().ifPresent(
                description -> components.add(prefixed(prefixLog(text(description, DARK_AQUA)))));

        return components;
    }

    @NonNull
    static Component getDefaultPunishmentComponent(@NonNull final String title, final int indent,
            final int newLines, @NonNull final RestrictiveAction restrictiveAction,
            @NonNull final String authorName) {
        return getDefaultPunishmentComponent(text(title, RED), indent, newLines, restrictiveAction,
                authorName);
    }

    @NonNull
    static Component getDefaultPunishmentComponent(@NonNull final Component title, final int indent,
            final int newLines, @NonNull final RestrictiveAction restrictiveAction,
            @NonNull final String authorName) {
        final Component duration = restrictiveAction instanceof ExpirablePunishmentEntity
                ? ((ExpirablePunishmentEntity<?, ?>) restrictiveAction).formatDurationNow() : null;

        return getDefaultPunishmentComponent(title, indent, newLines, restrictiveAction.getReason(),
                duration, authorName, restrictiveAction.getIncidentId());
    }

    @NonNull
    static Component getDefaultPunishmentComponent(@NonNull final String title, final int indent,
            final int newLines, @NonNull final String reason, @Nullable final Component duration,
            @NonNull final String authorName, @Nullable final String incidentId) {
        return getDefaultPunishmentComponent(text(title, RED), indent, newLines, reason, duration,
                authorName, incidentId);
    }

    @NonNull
    static Component getDefaultPunishmentComponent(@NonNull final Component title, final int indent,
            final int newLines, @NonNull final String reason, @Nullable final Component duration,
            @NonNull final String authorName, @Nullable final String incidentId) {
        final String indentation = Strings.repeat(" ", indent);
        final TextComponent.Builder builder = text().appendNewline()
                .append(BAR).appendNewline().appendNewline()
                .append(text(indentation).append(title)).appendNewline().appendNewline();

        for (int i = 0; i < newLines; i++) {
            builder.appendNewline();
        }

        builder.append(text(indentation).append(REASON.build(reason)));

        if (duration != null) {
            builder.appendNewline()
                    .append(text(indentation).append(DURATION.build(duration)));
        }

        builder.appendNewline()
                .append(text(indentation).append(AUTHOR.build(authorName)));

        if (incidentId != null) {
            builder.appendNewline()
                    .append(text(indentation).append(INCIDENT_ID.build(incidentId)));
        }

        return builder.appendNewline().appendNewline()
                .append(BAR).appendNewline().build();
    }

    @NonNull
    static TextComponent formatPunishment(@NonNull final RestrictiveAction restrictiveAction,
            @Nullable final User author, @Nullable final User target) {
        if (target != null) {
            return text().color(GRAY)
                    .append(text("Target", WHITE, BOLD))
                    .append(text(": "))
                    .append(text(target.getName())).appendNewline()
                    .append(formatPunishment(restrictiveAction, author)).build();
        }

        return formatPunishment(restrictiveAction, author);
    }

    @NonNull
    static TextComponent formatPunishment(@NonNull final RestrictiveAction restrictiveAction,
            @Nullable final User author) {
        final TextComponent.Builder detailsBuilder = text().color(GRAY)
                .append(text("Code", WHITE, BOLD))
                .append(text(": "))
                .append(text(restrictiveAction.getIncidentId())).appendNewline()
                .append(text("Author", WHITE, BOLD))
                .append(text(": "))
                .append(text(author == null ? restrictiveAction.getAuthor().toString()
                        : author.getName()));

        if (restrictiveAction instanceof ExpirablePunishmentEntity) {
            final ExpirablePunishmentEntity<?, ?> expirablePunishment =
                    (ExpirablePunishmentEntity<?, ?>) restrictiveAction;

            detailsBuilder.appendNewline()
                    .append(text("Duration", WHITE, BOLD))
                    .append(text(": "));

            final Optional<Timestamp> expiryOptional = expirablePunishment.getExpiry();

            if (expiryOptional.isPresent()) {
                final Instant expiry = expiryOptional.get().toInstant();

                detailsBuilder.append(
                                text(FormatUtils.formatDateDiff(Instant.now(), expiry, false,
                                        false)))
                        .append(text(" ["))
                        .append(text(FormatUtils.formatDate(expiry)))
                        .append(text(']'));
            } else {
                detailsBuilder.append(text("Permanent"));
            }
        }

        final Instant issued = restrictiveAction.getTimestamp().toInstant();

        detailsBuilder.appendNewline()
                .append(text("Issued", WHITE, BOLD))
                .append(text(": "))
                .append(text(FormatUtils.formatDateDiff(issued, Instant.now(), true, true)))
                .append(text(" ["))
                .append(text(FormatUtils.formatDate(issued)))
                .append(text(']')).appendNewline()
                .append(text("Reason", WHITE, BOLD))
                .append(text(": '"))
                .append(text(restrictiveAction.getReason()))
                .append(text('\''));

        return detailsBuilder.build();
    }

    interface Args0 {

        Component build();
    }

    interface Args1<A0> {

        Component build(A0 arg0);
    }

    interface Args2<A0, A1> {

        Component build(A0 arg0, A1 arg1);
    }

    interface Args3<A0, A1, A2> {

        Component build(A0 arg0, A1 arg1, A2 arg2);
    }

    interface Args4<A0, A1, A2, A3> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
    }

    interface Args5<A0, A1, A2, A3, A4> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    interface Args7<A0, A1, A2, A3, A4, A5, A6> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
    }

    interface MultipleArgs0 {

        Collection<Component> build();
    }

    interface MultipleArgs1<A0> {

        Collection<Component> build(A0 arg0);
    }

    interface MultipleArgs2<A0, A1> {

        Collection<Component> build(A0 arg0, A1 arg1);
    }

    interface MultipleArgs3<A0, A1, A2> {

        Collection<Component> build(A0 arg0, A1 arg1, A2 arg2);
    }

    interface MultipleArgs4<A0, A1, A2, A3> {

        Collection<Component> build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
    }

    interface MultipleArgs5<A0, A1, A2, A3, A4> {

        Collection<Component> build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    interface MultipleArgs6<A0, A1, A2, A3, A4, A5> {

        Collection<Component> build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    interface MultipleArgs7<A0, A1, A2, A3, A4, A5, A6> {

        Collection<Component> build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
    }
}
