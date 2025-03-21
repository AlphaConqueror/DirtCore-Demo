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

import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.Permissible;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An enumeration of the permissions required to execute DirtCore commands.
 */
@SuppressWarnings("SpellCheckingInspection")
@Getter
public enum Permission {

    NONE(null),
    BAN("ban"),
    BAN_IP("banip"),
    CHAT_MARKER("chatmarker"),
    CHAT_MARKER_ADMIN(CHAT_MARKER, "admin"),
    CHAT_MARKER_ADMIN_GRANT(CHAT_MARKER_ADMIN, "grant"),
    CHAT_MARKER_ADMIN_REVOKE(CHAT_MARKER_ADMIN, "revoke"),
    CHAT_MARKER_ADMIN_SET(CHAT_MARKER_ADMIN, "set"),
    CHAT_MARKER_ADMIN_UNSET(CHAT_MARKER_ADMIN, "unset"),
    CHAT_MARKER_LIST(CHAT_MARKER, "list"),
    CHAT_MARKER_LIST_OTHERS(CHAT_MARKER_LIST, "others"),
    CHAT_MARKER_LIST_OWN(CHAT_MARKER_LIST, "own"),
    CHAT_MARKER_SET(CHAT_MARKER, "set"),
    CHAT_MARKER_UNSET(CHAT_MARKER, "unset"),
    CHAT_MARKERS("chatmarkers"),
    CHANNELS("channels"),
    COMMAND_SELECTOR("command.selector"),
    CRATE("crate"),
    CRATE_CONTENT(CRATE, "content"),
    CRATE_CONTENT_ADD(CRATE_CONTENT, "add"),
    CRATE_CONTENT_CLEAR(CRATE_CONTENT, "clear"),
    CRATE_CONTENT_DETAILS(CRATE_CONTENT, "details"),
    CRATE_CONTENT_EDIT(CRATE_CONTENT, "edit"),
    CRATE_CONTENT_EDIT_COMMANDS(CRATE_CONTENT_EDIT, "commands"),
    CRATE_CONTENT_REMOVE(CRATE_CONTENT, "remove"),
    CRATE_CREATE(CRATE, "create"),
    CRATE_DELETE(CRATE, "delete"),
    CRATE_DETAILS(CRATE, "details"),
    CRATE_EDIT(CRATE, "edit"),
    CRATE_EDIT_DISPLAY_NAME(CRATE_EDIT, "displayname"),
    CRATE_EDIT_NAME(CRATE_EDIT, "name"),
    CRATE_EDIT_SHOULD_BROADCAST(CRATE_EDIT, "shouldbroadcast"),
    CRATE_KEY(CRATE, "key"),
    CRATE_KEY_GIVE(CRATE_KEY, "give"),
    CRATE_KEY_SET(CRATE_KEY, "set"),
    CRATE_KEY_UNSET(CRATE_KEY, "unset"),
    CRATE_LOCATION(CRATE, "location"),
    CRATE_LOCATION_ADD(CRATE_LOCATION, "add"),
    CRATE_LOCATION_CLEAR(CRATE_LOCATION, "clear"),
    CRATE_LOCATION_REMOVE(CRATE_LOCATION, "remove"),
    DISCORD("discord"),
    ECONOMY("economy"),
    ECONOMY_BALANCE(ECONOMY, "balance"),
    ECONOMY_BALANCE_ADD(ECONOMY_BALANCE, "add"),
    ECONOMY_BALANCE_OTHERS(ECONOMY_BALANCE, "others"),
    ECONOMY_BALANCE_REMOVE(ECONOMY_BALANCE, "remove"),
    ECONOMY_BALANCE_SET(ECONOMY_BALANCE, "set"),
    ECONOMY_LEADERBOARD(ECONOMY, "leaderboard"),
    ECONOMY_PAY(ECONOMY, "pay"),
    ECONOMY_WORTH(ECONOMY, "worth"),
    ECONOMY_WORTH_REMOVE(ECONOMY_WORTH, "remove"),
    ECONOMY_WORTH_SET(ECONOMY_WORTH, "set"),
    ENTITY_ZAP("entityzap"),
    ENTITY_ZAP_CHUNK(ENTITY_ZAP, "chunk"),
    ENTITY_ZAP_FORCE(ENTITY_ZAP, "force"),
    ENTITY_ZAP_RANGE(ENTITY_ZAP, "range"),
    HISTORY("history"),
    HISTORY_OTHERS(HISTORY, "others"),
    HISTORY_OWN(HISTORY, "own"),
    JOIN_MESSAGE("joinmessage"),
    JOIN_MESSAGE_ADMIN(JOIN_MESSAGE, "admin"),
    JOIN_MESSAGE_ADMIN_SET(JOIN_MESSAGE_ADMIN, "set"),
    JOIN_MESSAGE_ADMIN_PREVIEW(JOIN_MESSAGE_ADMIN, "preview"),
    JOIN_MESSAGE_ADMIN_UNSET(JOIN_MESSAGE_ADMIN, "unset"),
    KICK("kick"),
    KIT("kit"),
    KIT_ADMIN(KIT, "admin"),
    KIT_ADMIN_CREATE(KIT_ADMIN, "create"),
    KIT_ADMIN_COOLDOWNS(KIT_ADMIN, "cooldowns"),
    KIT_ADMIN_COOLDOWNS_CLEAR(KIT_ADMIN_COOLDOWNS, "clear"),
    KIT_ADMIN_COOLDOWNS_CLEAR_ALL(KIT_ADMIN_COOLDOWNS, "clearall"),
    KIT_ADMIN_COOLDOWNS_WIPE(KIT_ADMIN_COOLDOWNS, "wipe"),
    KIT_ADMIN_COOLDOWNS_WIPE_ALL(KIT_ADMIN_COOLDOWNS, "wipeall"),
    KIT_ADMIN_DELETE(KIT_ADMIN, "delete"),
    KIT_ADMIN_DETAILS(KIT_ADMIN, "details"),
    KIT_ADMIN_EDIT(KIT_ADMIN, "edit"),
    KIT_ADMIN_EDIT_COOLDOWN(KIT_ADMIN_EDIT, "cooldown"),
    KIT_ADMIN_EDIT_DISPLAY_NAME(KIT_ADMIN_EDIT, "displayname"),
    KIT_ADMIN_EDIT_ITEMS(KIT_ADMIN_EDIT, "items"),
    KIT_ADMIN_EDIT_NAME(KIT_ADMIN_EDIT, "name"),
    KIT_CLAIM(KIT, "claim"),
    KIT_LIST(KIT, "list"),
    KIT_LIST_OTHERS(KIT_LIST, "others"),
    KIT_LIST_OWN(KIT_LIST, "own"),
    KIT_SHOW(KIT, "show"),
    KITS("kits"),
    LEAVE_MESSAGE("leavemessage"),
    LEAVE_MESSAGE_ADMIN(LEAVE_MESSAGE, "admin"),
    LEAVE_MESSAGE_ADMIN_SET(LEAVE_MESSAGE_ADMIN, "set"),
    LEAVE_MESSAGE_ADMIN_PREVIEW(LEAVE_MESSAGE_ADMIN, "preview"),
    LEAVE_MESSAGE_ADMIN_UNSET(LEAVE_MESSAGE_ADMIN, "unset"),
    LIMIT("limit"),
    LIMIT_ADMIN(LIMIT, "admin"),
    LIMIT_ADMIN_ADD(LIMIT_ADMIN, "add"),
    LIMIT_ADMIN_BYPASS(LIMIT_ADMIN, "bypass"),
    LIMIT_ADMIN_EDIT(LIMIT_ADMIN, "edit"),
    LIMIT_ADMIN_EDIT_REASON(LIMIT_ADMIN_EDIT, "reason"),
    LIMIT_ADMIN_EDIT_RULE(LIMIT_ADMIN_EDIT, "rule"),
    LIMIT_ADMIN_ENTRIES(LIMIT_ADMIN, "entries"),
    LIMIT_ADMIN_ENTRY(LIMIT_ADMIN, "entry"),
    LIMIT_ADMIN_REMOVE(LIMIT_ADMIN, "remove"),
    LIMIT_DETAILS(LIMIT, "details"),
    LIMIT_ENTRIES(LIMIT, "entries"),
    LIMIT_ENTRIES_OTHERS(LIMIT_ENTRIES, "others"),
    LIMIT_ENTRIES_OWN(LIMIT_ENTRIES, "own"),
    LIMIT_LIST(LIMIT, "list"),
    LIMIT_LIST_OTHERS(LIMIT_LIST, "others"),
    LIMIT_LIST_OWN(LIMIT_LIST, "own"),
    LOG_NOTIFY_STAFF("log.notify.staff"),
    LOG_NOTIFY_ADMIN("log.notify.admin"),
    MESSAGE("message"),
    MUTE("mute"),
    PAY_RESPECT("payrespect"),
    PREFIX("prefix"),
    PREFIX_ADMIN(PREFIX, "admin"),
    PREFIX_ADMIN_GRANT(PREFIX_ADMIN, "grant"),
    PREFIX_ADMIN_REVOKE(PREFIX_ADMIN, "revoke"),
    PREFIX_ADMIN_SET(PREFIX_ADMIN, "set"),
    PREFIX_ADMIN_UNSET(PREFIX_ADMIN, "unset"),
    PREFIX_LIST(PREFIX, "list"),
    PREFIX_LIST_OTHERS(PREFIX_LIST, "others"),
    PREFIX_LIST_OWN(PREFIX_LIST, "own"),
    PREFIX_SET(PREFIX, "set"),
    PREFIX_UNSET(PREFIX, "unset"),
    PREFIXES("prefixes"),
    PROFILE("profile"),
    PROFILE_ENTITIES(PROFILE, "entities"),
    PUNISHMENT_EXEMPT("punishment.exempt"),
    RESTRICT("restrict"),
    RESTRICT_ADMIN(RESTRICT, "admin"),
    RESTRICT_ADMIN_BYPASS(RESTRICT_ADMIN, "bypass"),
    RESTRICT_ADMIN_ITEM(RESTRICT_ADMIN, "item"),
    RESTRICT_ADMIN_ITEM_ADD(RESTRICT_ADMIN_ITEM, "add"),
    RESTRICT_ADMIN_ITEM_EDIT(RESTRICT_ADMIN_ITEM, "edit"),
    RESTRICT_ADMIN_ITEM_EDIT_ACTION(RESTRICT_ADMIN_ITEM_EDIT, "action"),
    RESTRICT_ADMIN_ITEM_EDIT_ALTERNATIVE(RESTRICT_ADMIN_ITEM_EDIT, "alternative"),
    RESTRICT_ADMIN_ITEM_EDIT_PERSISTENT_DATA(RESTRICT_ADMIN_ITEM_EDIT, "persistentdata"),
    RESTRICT_ADMIN_ITEM_EDIT_REASON(RESTRICT_ADMIN_ITEM_EDIT, "reason"),
    RESTRICT_ADMIN_ITEM_EDIT_WORLD(RESTRICT_ADMIN_ITEM_EDIT, "world"),
    RESTRICT_ADMIN_ITEM_REMOVE(RESTRICT_ADMIN_ITEM, "remove"),
    RESTRICT_ADMIN_MOD(RESTRICT_ADMIN, "item"),
    RESTRICT_ADMIN_MOD_ADD(RESTRICT_ADMIN_MOD, "add"),
    RESTRICT_ADMIN_MOD_EDIT(RESTRICT_ADMIN_MOD, "edit"),
    RESTRICT_ADMIN_MOD_EDIT_ACTION(RESTRICT_ADMIN_MOD_EDIT, "action"),
    RESTRICT_ADMIN_MOD_EDIT_ALTERNATIVE(RESTRICT_ADMIN_MOD_EDIT, "alternative"),
    RESTRICT_ADMIN_MOD_EDIT_PERSISTENT_DATA(RESTRICT_ADMIN_MOD_EDIT, "persistentdata"),
    RESTRICT_ADMIN_MOD_EDIT_REASON(RESTRICT_ADMIN_MOD_EDIT, "reason"),
    RESTRICT_ADMIN_MOD_EDIT_WORLD(RESTRICT_ADMIN_MOD_EDIT, "world"),
    RESTRICT_ADMIN_MOD_REMOVE(RESTRICT_ADMIN_MOD, "remove"),
    RESTRICT_DETAILS(RESTRICT, "details"),
    RESTRICT_DETAILS_ITEMS(RESTRICT_DETAILS, "items"),
    RESTRICT_DETAILS_MODS(RESTRICT_DETAILS, "mods"),
    RESTRICT_LIST(RESTRICT, "list"),
    RESTRICT_LIST_ITEMS(RESTRICT_LIST, "items"),
    RESTRICT_LIST_MODS(RESTRICT_LIST, "mods"),
    SOCIALSPY("socialspy"),
    STAFF("staff"),
    STORE("store"),
    TELEPORT("teleport"),
    TELEPORT_OTHERS(TELEPORT, "others"),
    TEMPBAN("tempban"),
    TEMPMUTE("tempmute"),
    TIME("time"),
    UNBAN("unban"),
    UNBAN_IP("unbanip"),
    UNMUTE("unmute"),
    UNVERIFY("unverify"),
    VERIFY("verify"),
    VOTE("vote"),
    VOTE_CLAIM(VOTE, "claim"),
    VOTE_STATS(VOTE, "stats"),
    VOTE_STATS_OTHERS(VOTE_STATS, "others"),
    VOTE_STATS_OWN(VOTE_STATS, "own"),
    WARN("warn");

    private static final String ROOT = DirtCorePlugin.MOD_ID + '.';
    private final String permission;

    Permission(final String node) {
        this.permission = ROOT + node;
    }

    Permission(final Permission parent, final String node) {
        this.permission = parent.permission + '.' + node;
    }

    public boolean isAuthorized(@NonNull final Permissible permissible) {
        return permissible.hasPermission(this);
    }
}
