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

package net.dirtcraft.dirtcore.common.model;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.api.implementation.ApiUser;
import net.dirtcraft.dirtcore.common.exception.PlayerNotFoundException;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.player.PlayerDataEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.UnmuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.WarnEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.BanHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.history.MuteHistoryEntity;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class User implements Identifiable, Permissible {

    @NonNull
    private final DirtCorePlugin plugin;
    @NonNull
    private final ApiUser apiProxy = new ApiUser(this);
    @NonNull
    private final UUID uniqueId;
    @NonNull
    private final UserEntity userEntity; // the user entity at the time of querying

    public User(@NonNull final DirtCorePlugin plugin, @NonNull final UUID uniqueId,
            @NonNull final UserEntity userEntity) {
        this.plugin = plugin;
        this.uniqueId = uniqueId;
        this.userEntity = userEntity;
    }

    public @NonNull ApiUser getApiProxy() {
        return this.apiProxy;
    }

    public boolean isConsole() {
        return this.uniqueId.equals(Sender.CONSOLE_UUID);
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NonNull String getName() {
        return this.userEntity.getUsername();
    }

    @NonNull
    public Optional<Timestamp> getLastSeen() {
        return this.userEntity.getLastSeen();
    }

    public void setLastSeenNow(@NonNull final TaskContext context) {
        this.userEntity.setLastSeenNow();
        context.session().merge(this.userEntity);
    }

    @NonNull
    public Component getDisplayName() {
        return this.plugin.getPlatformFactory().getPlayer(this.getUniqueId())
                .map(Player::getDisplayName).orElse(Component.text(this.getName()))
                .color(NamedTextColor.GRAY);
    }

    @NonNull
    public Component formatDisplay(@NonNull final TaskContext context) {
        final TextComponent.Builder builder = Component.text();
        final Optional<PlayerDataEntity> playerDataOptional =
                this.plugin.getUserManager().getPlayerData(context, this.uniqueId);
        final Optional<PrefixEntity> prefixOptional =
                playerDataOptional.flatMap(playerData -> playerData.getPrefix(context));
        final List<StaffPrefixEntity> staffPrefixes =
                this.plugin.getChatManager().getStaffPrefixesOrdered(context, this);
        boolean hasPrefix = prefixOptional.isPresent();

        // if user has active prefix, use shortened staff prefix
        if (!staffPrefixes.isEmpty()) {
            final StaffPrefixEntity staffPrefix = staffPrefixes.get(0);

            builder.append(prefixOptional.isPresent() ? staffPrefix.getShortDisplayAsComponent(
                    staffPrefixes) : staffPrefix.getFullDisplayAsComponent(staffPrefixes));
            hasPrefix = true;
        }

        prefixOptional.ifPresent(prefix -> builder.append(prefix.getDisplayAsComponent()));

        if (hasPrefix) {
            builder.appendSpace();
        }

        // show the display name in chat and the actual name as a hover event
        return builder.append(this.getDisplayName()).hoverEvent(HoverEvent.showText(
                Component.text().color(NamedTextColor.GRAY)
                        .append(Component.text("Name: ", NamedTextColor.GOLD))
                        .append(Component.text(this.getName())))).build();
    }

    @NonNull
    public Component formatShortDisplay(@NonNull final TaskContext context) {
        final TextComponent.Builder builder = Component.text();
        final List<StaffPrefixEntity> staffPrefixes =
                this.plugin.getChatManager().getStaffPrefixesOrdered(context, this);

        if (!staffPrefixes.isEmpty()) {
            final StaffPrefixEntity staffPrefix = staffPrefixes.get(0);
            builder.append(staffPrefix.getShortDisplayAsComponent(staffPrefixes)).appendSpace();
        }

        // show the display name in chat and the actual name as a hover event
        return builder.append(this.getDisplayName()).hoverEvent(HoverEvent.showText(
                Component.text().color(NamedTextColor.GRAY)
                        .append(Component.text("Name: ", NamedTextColor.GOLD))
                        .append(Component.text(this.getName())))).build();
    }

    /**
     * Bans the user from the server.
     *
     * @param context the task context
     * @param sender  the sender
     * @param reason  the reason
     * @param expiry  the expiry
     * @return the result
     */
    @NonNull
    public Component ban(@NonNull final TaskContext context, @NonNull final Sender sender,
            @NonNull final String reason, @Nullable final Instant expiry) {
        final User source = sender.getUser(context);
        final Optional<BanEntity> activeBan =
                this.plugin.getPunishmentManager().getActiveBan(context, this.uniqueId);

        if (activeBan.isPresent()) {
            final BanEntity ban = activeBan.get();
            return this.modifyBan(context, sender, ban, source, reason, expiry, ban.isIpBanned());
        }

        if (this.isPunishmentExempt()) {
            context.queue(() -> this.plugin.getLogDispatcher()
                    .dispatchPunishmentExempt(sender, source, this, "Ban attempt failed."));
            return Components.BAN_USER_CAN_NOT_BE_BANNED.build(this.getName());
        }

        final String incidentId = this.plugin.getPunishmentManager().nextIncidentId(context);
        final BanEntity ban = this.plugin.getPunishmentManager()
                .ban(context, incidentId, sender, source, this, reason, expiry);

        context.queue(() -> this.getPlayer().ifPresent(player -> player.kick(
                this.plugin.getPlatformFactory().banScreenComponent()
                        .build(ban, sender.getName()))));

        return Components.BAN_SUCCESSFUL.build(this.getName());
    }

    /**
     * Modifies the ban of the user.
     *
     * @param context the task context
     * @param sender  the sender
     * @param reason  the reason
     * @param expiry  the expiry
     * @param ipBan   if it is an IP-Ban
     * @return the result
     */
    @NonNull
    public Component modifyBan(@NonNull final TaskContext context, @NonNull final Sender sender,
            @NonNull final BanEntity ban, @NonNull final User source, @NonNull final String reason,
            @Nullable final Instant expiry, final boolean ipBan) {
        final boolean change1 = !ban.getAuthor().equals(sender.getUniqueId());
        final boolean change2 = !ban.getReason().equals(reason);
        final boolean change3 = !ban.getServer().equals(this.plugin.getServerIdentifier());
        // do not update if:
        // - both are permanent
        // - both expiries have the same length in seconds
        final boolean change4 =
                (ban.getExpiry().isPresent() || expiry != null) && (!ban.getExpiry().isPresent()
                        || expiry == null || ban.getExpiry().get().toInstant().getEpochSecond()
                        != expiry.getEpochSecond());
        final boolean change5 = ban.isIpBanned() != ipBan;

        // only merge upon change
        if (change1 || change2 || change3 || change4 || change5) {
            // create new ban history entry before applying changes
            final BanHistoryEntity banHistory = new BanHistoryEntity(ban);

            ban.setTimestampNow();
            ban.setAuthor(sender.getUniqueId());
            ban.setReason(reason);
            ban.setServer(this.plugin.getServerIdentifier());
            ban.setExpiry(expiry);
            ban.setIpBanned(ipBan);

            context.session().merge(ban);
            context.session().persist(banHistory);

            context.queue(() -> {
                this.plugin.getMessagingService().ifPresent(service -> service.pushIncident(ban));

                final LogEntity log =
                        LogEntity.builder(source.getUniqueId(), this.plugin.getServerIdentifier(),
                                        ban.getType(), Action.Authorization.STAFF)
                                .target(this.getUniqueId()).incidentId(ban.getIncidentId())
                                .title("Ban has been modified.").build();

                this.plugin.getLogDispatcher()
                        .dispatch(log, sender, ban.getLogEmbed(context, log, source, this),
                                Components.LOG.build(log, source, this, ban, source));
            });

            return Components.BAN_MODIFIED.build(this.getName());
        }

        return Components.BAN_CHANGES_NEEDED.build();
    }

    /**
     * Bans the user from the server.
     *
     * @param context the task context
     * @param sender  the sender
     * @param reason  the reason
     * @param expiry  the expiry
     * @return the result
     */
    @NonNull
    public Component mute(@NonNull final TaskContext context, @NonNull final Sender sender,
            @NonNull final String reason, @Nullable final Instant expiry) {
        final User source =
                this.plugin.getUserManager().getOrCreateUser(context, sender.getUniqueId());
        final Optional<MuteEntity> activeMute =
                this.plugin.getPunishmentManager().getActiveMute(context, this.uniqueId);

        if (activeMute.isPresent()) {
            final MuteEntity mute = activeMute.get();

            final boolean change1 = !mute.getAuthor().equals(sender.getUniqueId());
            final boolean change2 = !mute.getReason().equals(reason);
            final boolean change3 = !mute.getServer().equals(this.plugin.getServerIdentifier());
            // do not update if:
            // - both are permanent
            // - both expiries have the same length in seconds
            final boolean change4 = (mute.getExpiry().isPresent() || expiry != null) && (
                    !mute.getExpiry().isPresent() || expiry == null
                            || mute.getExpiry().get().toInstant().getEpochSecond()
                            != expiry.getEpochSecond());

            // only merge upon change
            if (change1 || change2 || change3 || change4) {
                // create new ban history entry before applying changes
                final MuteHistoryEntity banHistory = new MuteHistoryEntity(mute);

                mute.setTimestampNow();
                mute.setAuthor(sender.getUniqueId());
                mute.setReason(reason);
                mute.setServer(this.plugin.getServerIdentifier());
                mute.setExpiry(expiry);

                context.session().merge(mute);
                context.session().persist(banHistory);

                context.queue(() -> {
                    this.plugin.getMessagingService()
                            .ifPresent(service -> service.pushIncident(mute));

                    final LogEntity log = LogEntity.builder(source.getUniqueId(),
                                    this.plugin.getServerIdentifier(), mute.getType(),
                                    Action.Authorization.STAFF).target(this.getUniqueId())
                            .incidentId(mute.getIncidentId()).title("Mute has been modified.")
                            .build();

                    this.plugin.getLogDispatcher()
                            .dispatch(log, sender, mute.getLogEmbed(context, log, source, this),
                                    Components.LOG.build(log, source, this, mute, source));
                });

                return Components.MUTE_MODIFIED.build(this.getName());
            }

            return Components.MUTE_CHANGES_NEEDED.build();
        }

        if (this.isPunishmentExempt()) {
            context.queue(() -> this.plugin.getLogDispatcher()
                    .dispatchPunishmentExempt(sender, source, this, "Mute attempt failed."));
            return Components.MUTE_USER_CAN_NOT_BE_MUTED.build(this.getName());
        }

        final String incidentId = this.plugin.getPunishmentManager().nextIncidentId(context);
        final MuteEntity mute = this.plugin.getPunishmentManager()
                .mute(context, incidentId, sender, source, this, reason, expiry);

        context.queue(() -> this.getPlayer().ifPresent(player -> player.sendMessage(
                Components.MUTE_REASON.build(mute, sender.getName()))));

        return Components.MUTE_SUCCESSFUL.build(this.getName());
    }

    /**
     * Unbans the user from the server.
     *
     * @param original the original ban
     * @param sender   the sender
     * @param reason   the reason
     * @return true if successful
     */
    public boolean unban(@NonNull final TaskContext context, @NonNull final BanEntity original,
            @NonNull final Sender sender, @NonNull final String reason) {
        final String incidentId = this.plugin.getPunishmentManager().nextIncidentId(context);

        this.plugin.getPunishmentManager()
                .unban(context, original, incidentId, sender, this, reason);
        return true;
    }

    /**
     * Unmutes the user from the server.
     *
     * @param original the original mute
     * @param source   the source
     * @param reason   the reason
     * @return true if successful
     */
    public boolean unmute(@NonNull final TaskContext context, @NonNull final MuteEntity original,
            @NonNull final Sender source, @NonNull final String reason) {
        final String incidentId = this.plugin.getPunishmentManager().nextIncidentId(context);
        final UnmuteEntity unmute = this.plugin.getPunishmentManager()
                .unmute(context, original, incidentId, source, this, reason);

        this.getPlayer().ifPresent(player -> player.sendMessage(
                Components.UNMUTE_REASON.build(unmute, source.getName())));
        return true;
    }

    /**
     * Warns the user.
     *
     * @param sender the sender
     * @param reason the reason
     * @return true if successful
     */
    public boolean warn(@NonNull final TaskContext context, @NonNull final Sender sender,
            @NonNull final String reason) {
        final String incidentId = this.plugin.getPunishmentManager().nextIncidentId(context);
        final WarnEntity warn =
                this.plugin.getPunishmentManager().warn(context, incidentId, sender, this, reason);

        this.getPlayer().ifPresent(
                player -> player.sendMessage(Components.WARN_REASON.build(warn, sender.getName())));
        return true;
    }

    @Override
    public boolean hasPermission(final @NonNull String permission) {
        return this.plugin.getPermissionHandler().hasPermission(this.uniqueId, permission);
    }

    @Override
    public boolean isPartOfGroup(@NonNull final String name) {
        return this.plugin.getPermissionHandler().isPartOfGroup(this.uniqueId, name);
    }

    @Override
    public @NonNull Collection<String> getGroups() {
        return this.plugin.getPermissionHandler().getGroups(this.uniqueId);
    }

    public boolean isPlayerOnline() {
        return this.plugin.getPlatformFactory().isPlayerOnline(this.uniqueId);
    }

    @NonNull
    public Optional<Player> getPlayer() {
        return this.plugin.getPlatformFactory().getPlayer(this.uniqueId);
    }

    /**
     * Gets the player instance for this object.
     * This might cause issues in async context.
     *
     * @return the player instance
     * @throws PlayerNotFoundException in case the player could not be found
     */
    @NonNull
    public Player getPlayerOrException() {
        return this.getPlayer().orElseThrow(() -> new PlayerNotFoundException(this.uniqueId));
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }

        final User other = (User) o;
        return this.uniqueId.equals(other.uniqueId);
    }

    @Override
    public String toString() {
        return "User(uuid=" + this.uniqueId + ")";
    }
}
