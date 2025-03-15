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

package net.dirtcraft.dirtcore.common.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.dirtcraft.dirtcore.common.config.adapter.ConfigurationAdapter;
import net.dirtcraft.dirtcore.common.config.key.ConfigKey;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dirtcraft.dirtcore.common.util.VoteReward;
import net.dirtcraft.storageutils.StorageCredentials;
import net.dirtcraft.storageutils.StorageType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.doubleKey;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.intKey;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.key;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.longKey;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.notReloadable;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.range;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.stringKey;
import static net.dirtcraft.dirtcore.common.config.key.ConfigKeyFactory.stringListKey;

public final class ConfigKeys {

    /*
     * GLOBAL SECTION
     */

    /**
     * The network name.
     */
    public static final ConfigKey<String> NETWORK_NAME = stringKey("network-name", "Network Name");

    /**
     * The network icon.
     */
    public static final ConfigKey<String> NETWORK_ICON = stringKey("network-icon",
            "https://static.wikia.nocookie.net/logopedia/images/a/ab/Minecraft_2009_icon"
                    + ".svg/revision/latest?cb=20240128230646");

    /**
     * The server name.
     */
    public static final ConfigKey<String> SERVER_NAME = stringKey("server-name", "Server Name");

    /**
     * The server identifier, the short form of the server name.
     * Used to tie DB entries to a server.
     */
    public static final ConfigKey<String> SERVER_IDENTIFIER = stringKey("server-identifier", "");

    /**
     * The server icon.
     */
    public static final ConfigKey<String> SERVER_ICON = stringKey("server-icon",
            "https://static.wikia.nocookie.net/logopedia/images/a/ab/Minecraft_2009_icon"
                    + ".svg/revision/latest?cb=20240128230646");

    /**
     * The terminal icon.
     */
    public static final ConfigKey<String> TERMINAL_ICON = stringKey("terminal-icon",
            "https://upload.wikimedia.org/wikipedia/commons/b/b3/Terminalicon2.png");

    /**
     * The link to the store.
     */
    public static final ConfigKey<String> STORE_LINK =
            stringKey("store-link", "https://example.com");

    /*
     * COMMAND SECTION
     */

    /**
     * The entity zap command will ask for confirmation if amount of entities to be discarded is
     * greater than this value. -1 will disable confirmation.
     */
    public static final ConfigKey<Integer> COMMAND_ENTITY_ZAP_CONFIRM_ABOVE =
            range(intKey("command.entity-zap.confirm-above", 0), -1, Integer.MAX_VALUE);

    /**
     * A list of entity identifiers to exclude from mass removals via entity zap.
     * They are not excluded when specifying a type using --type.
     */
    public static final ConfigKey<List<String>> COMMAND_ENTITY_ZAP_MASS_REMOVAL_EXCLUSIONS =
            stringListKey("command.entity-zap.mass-removal-exclusions", Collections.emptyList());

    /**
     * The max range used in the entity zap command.
     */
    public static final ConfigKey<Double> COMMAND_ENTITY_ZAP_MAX_RANGE = notReloadable(
            range(doubleKey("command.entity-zap.max-range", 500d), 0d, Double.MAX_VALUE));

    /**
     * A list of text to be broadcast when someone pays their respect.
     * Supports MiniMessage.
     * <p>
     * {sender_name} placeholder for the sender name
     * {player_name} placeholder for the player name
     */
    public static final ConfigKey<List<String>> COMMAND_PAY_RESPECT =
            stringListKey("command.pay-respect", ImmutableList.of(
                    "{sender_name} plants a tree for {player_name}. Then chops it down for wood.",
                    "{sender_name} buries {player_name}'s stuff in a chest. Well, most of it.",
                    "{sender_name} builds {player_name} a tiny graveyard... with only two blocks "
                            + "of dirt.",
                    "{sender_name} promises to avenge {player_name}... after finishing this build.",
                    "{sender_name} renames their pet pig to ‘In Memory of {player_name}’.",
                    "{sender_name} gives {player_name} the highest honor, a Minecraft sign with a"
                            + " typo.",
                    "{sender_name} declares {player_name}'s death a conspiracy by the Endermen.",
                    "{sender_name} builds a redstone contraption in {player_name}'s honor. It "
                            + "doesn’t work.",
                    "{sender_name} blames {player_name}'s death on lag... and the admins agree.",
                    "{sender_name} leaves a cake at {player_name}'s grave. It's missing a slice.",
                    "{sender_name} places a bed in the Nether for {player_name}'s ultimate nap.",
                    "{sender_name} throws a potato in {player_name}'s name. It’s what they would "
                            + "have wanted.",
                    "{sender_name} builds a lava fountain to honor {player_name}. Half the base "
                            + "burns down.",
                    "{sender_name} dedicates their next death to {player_name}. It happens almost"
                            + " immediately.",
                    "{sender_name} names a creeper ‘{player_name}’ and lets it roam free. Bad "
                            + "idea."));

    /**
     * A list of text to be broadcast when someone pays their respect.
     * This is a list without player. Supports MiniMessage.
     * <p>
     * {sender_name} placeholder for the sender name
     */
    public static final ConfigKey<List<String>> COMMAND_PAY_RESPECT_NO_PLAYER =
            stringListKey("command.pay-respect-no-player", ImmutableList.of(
                    "{sender_name} holds a moment of silence, but only because they forgot what "
                            + "they were supposed to say.",
                    "{sender_name} wonders if there's any loot left behind.",
                    "{sender_name} plants a tree to remember... or maybe it's just a sapling. "
                            + "Hard to tell.",
                    "{sender_name} puts a cake on the grave, but someone eats it before anyone "
                            + "can see."));

    /**
     * A list of text to be broadcast when someone pays their respect.
     * This is a list for a sender itself. Supports MiniMessage.
     * <p>
     * {sender_name} placeholder for the sender name
     */
    public static final ConfigKey<List<String>> COMMAND_PAY_RESPECT_SELF =
            stringListKey("command.pay-respect-self",
                    ImmutableList.of("{sender_name} offers condolences... to themselves. Awkward.",
                            "{sender_name} sighs, 'Well, this is awkward...'",
                            "{sender_name} thinks, 'Guess I can’t blame anyone but myself.'",
                            "{sender_name} places a flower on their grave... somehow.",
                            "{sender_name} yells, 'Who wrote <italic>that</italic> on my "
                                    + "tombstone?!'",
                            "{sender_name} groans, 'Being dead is so inconvenient...'",
                            "{sender_name} wonders if ghosts get PTO.",
                            "{sender_name} tries to haunt their own grave for dramatic effect.",
                            "{sender_name} jokes, 'At least I don’t have to pay rent anymore.'",
                            "{sender_name} smirks, 'I’d make a great ghost, honestly.'",
                            "{sender_name} mutters, 'I died doing what I loved... being bad at "
                                    + "Minecraft.'"));

    /*
     * VOTE SECTION
     */

    /**
     * The supported vote links.
     */
    public static final ConfigKey<List<String>> VOTE_LINKS =
            stringListKey("vote.links", Collections.emptyList());

    /**
     * The commands to execute when someone claims a vote.
     * {player_name} placeholder for the player name
     * {player_uuid} placeholder for the player unique id
     */
    public static final ConfigKey<List<String>> VOTE_CLAIM_COMMANDS =
            stringListKey("vote.claim-commands",
                    Collections.singletonList("balance {player_name} add 400"));

    /**
     * The free inventory space per 'claim-commands' reward procedure.
     */
    public static final ConfigKey<Integer> VOTE_CLAIM_FREE_INVENTORY_SPACE =
            intKey("vote.claim-free-inventory-space", 0);

    /**
     * The commands to execute when someone reaches a claimed vote rewards milestone.
     * {player_name} placeholder for the player name
     * {player_uuid} placeholder for the player unique id
     * <p>
     * Keys will be sorted in descending order afterwards.
     * <p>
     * Claimed vote rewards count will be checked for being a factor of a key.
     * The commands of the first match will be executed.
     */
    public static final ConfigKey<Map<Integer, VoteReward>> VOTE_CLAIMED_REWARDS_EXTRA_COMMANDS =
            key((config, path, def) -> {
                final Map<Integer, VoteReward> commandsMap =
                        config.getMap(path, ImmutableMap.of()).entrySet().stream()
                                .collect(HashMap::new,
                                        (map, entry) -> consumeVoteRewardMap(config, path, map,
                                                entry), Map::putAll);
                // make immutable
                return ImmutableMap.copyOf(commandsMap);
            }, "vote.claimed-rewards-extra-commands", ImmutableMap.of());

    /**
     * The commands to execute when someone reaches a vote streak milestone.
     * {player_name} placeholder for the player name
     * {player_uuid} placeholder for the player unique id
     * <p>
     * Keys will be sorted in descending order afterwards.
     * <p>
     * Streak count will be checked for being a factor of a key.
     * The commands of the first match will be executed.
     */
    public static final ConfigKey<Map<Integer, VoteReward>> VOTE_STREAK_EXTRA_REWARD_COMMANDS =
            key((config, path, def) -> {
                final Map<Integer, VoteReward> commandsMap =
                        config.getMap(path, ImmutableMap.of()).entrySet().stream()
                                .collect(HashMap::new,
                                        (map, entry) -> consumeVoteRewardMap(config, path, map,
                                                entry), Map::putAll);
                // make immutable
                return ImmutableMap.copyOf(commandsMap);
            }, "vote.streak-extra-reward-commands", ImmutableMap.of());

    /*
     * DISCORD SECTION
     */

    /**
     * The token for the discord bot.
     */
    public static final ConfigKey<String> DISCORD_TOKEN =
            notReloadable(stringKey("discord.token", ""));

    /**
     * The custom status of the discord bot. Leave empty for no status.
     */
    public static final ConfigKey<String> DISCORD_STATUS =
            notReloadable(stringKey("discord.status", ""));

    /**
     * The intents for the discord bot, by their ids.
     * see: <a href="https://discord.com/developers/docs/events/gateway#list-of-intents">...</a>
     */
    public static final ConfigKey<Set<GatewayIntent>> DISCORD_INTENTS =
            notReloadable(key((config, path, def) -> {
                // make a map of offset -> GatewayIntent
                final Map<Integer, GatewayIntent> map = new HashMap<>();

                for (GatewayIntent intent : GatewayIntent.values()) {
                    map.put(intent.getOffset(), intent);
                }

                final Set<GatewayIntent> intents =
                        config.getIntList(path, ImmutableList.of()).stream()
                                .collect(HashSet::new, (set, i) -> {
                                    // add to map if offset matches
                                    if (map.containsKey(i)) {
                                        set.add(map.get(i));
                                    }
                                }, HashSet::addAll);
                return ImmutableSet.copyOf(intents);
            }, "discord.intents", ImmutableSet.of(GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.SCHEDULED_EVENTS)));

    /**
     * The guild id of the discord bot.
     */
    public static final ConfigKey<Long> DISCORD_GUILD_ID = longKey("discord.guild-id", 0L);

    /**
     * The game channel the discord bot should broadcast to.
     */
    public static final ConfigKey<Long> DISCORD_GAME_CHANNEL_ID =
            longKey("discord.game-channel-id", 0L);

    /**
     * The admin log channel the discord bot should broadcast to.
     */
    public static final ConfigKey<Long> DISCORD_ADMIN_LOG_CHANNEL_ID =
            longKey("discord.admin-log-channel-id", 0L);

    /**
     * The staff log channel the discord bot should broadcast to.
     */
    public static final ConfigKey<Long> DISCORD_STAFF_LOG_CHANNEL_ID =
            longKey("discord.staff-log-channel-id", 0L);

    /**
     * The id of the role given upon successful verification.
     */
    public static final ConfigKey<Long> DISCORD_VERIFICATION_ROLE_ID =
            longKey("discord.verification-role-id", 0L);

    /**
     * The permissions of each role or user. The permissions of users have priority.
     * Possible permissions are: {@link DiscordPermission}
     */
    public static final ConfigKey<Map<Long, Set<Permission>>> DISCORD_PERMISSIONS =
            key((config, path, def) -> {
                final Map<Long, Set<Permission>> permissionsMap =
                        config.getMap(path, ImmutableMap.of()).entrySet().stream()
                                .collect(HashMap::new,
                                        (map, entry) -> consumePermissionMap(config, path, map,
                                                entry), Map::putAll);
                // make immutable
                return ImmutableMap.copyOf(permissionsMap);
            }, "discord.permissions", ImmutableMap.of());

    /**
     * The default color of the generated discord embeds.
     */
    public static final ConfigKey<String> DISCORD_EMBED_COLOR =
            stringKey("discord.embed-color", "#000000");

    /**
     * The link to the discord server.
     */
    public static final ConfigKey<String> DISCORD_LINK =
            stringKey("discord.link", "https://example.com");

    /**
     * The emoji displayed upon player death.
     */
    public static final ConfigKey<String> DISCORD_EMOJIS_DEATH =
            stringKey("discord.emojis.death", ":skull:");

    /**
     * The emoji displayed upon player join.
     */
    public static final ConfigKey<String> DISCORD_EMOJIS_JOIN =
            stringKey("discord.emojis.join", ":green_circle:");

    /**
     * The emoji displayed when players open crates.
     */
    public static final ConfigKey<String> DISCORD_EMOJIS_KEY =
            stringKey("discord.emojis.key", ":fireworks:");

    /**
     * The emoji displayed upon player leave.
     */
    public static final ConfigKey<String> DISCORD_EMOJIS_LEAVE =
            stringKey("discord.emojis.leave", ":red_circle:");

    /*
     * PUNISHMENT SECTION
     */

    /**
     * The commands blocked for muted users. Supports the start of the command line or regular
     * expressions. Command line is always in lowercase.
     */
    public static final ConfigKey<List<String>> PUNISHMENT_MUTE_BLOCKED_COMMANDS =
            stringListKey("mute-blocked-commands", Collections.singletonList("me"));

    /*
     * ADVANCEMENT SECTION
     */

    /**
     * The advancement keys that should not be dispatched in the PlayerAchievementEvent.
     * Supports regular expressions. Backslashes need to be escaped: '\' -> '\\'
     * Currently only supported by Bukkit.
     */
    public static final ConfigKey<List<String>> BLACKLISTED_ADVANCEMENTS =
            stringListKey("blacklisted-advancements",
                    Collections.singletonList("(adventure\\/)?root"));

    /*
     * CONNECTION SECTION
     */

    /**
     * The amount of times we should try to reconnect upon losing connection.
     */
    public static final ConfigKey<Integer> CONNECTION_RETRIES_UPON_CONNECTION_LOSS =
            range(intKey("connection.retries-upon-connection-loss", 10), 0, Integer.MAX_VALUE - 1);

    /**
     * The amount of times we should try to perform a task when running into a deadlock.
     */
    public static final ConfigKey<Integer> CONNECTION_RETRIES_UPON_DEADLOCK =
            range(intKey("connection.retries-upon-deadlock", 20), 0, Integer.MAX_VALUE - 1);

    /*
     * STORAGE SECTION
     */

    /**
     * The name of the storage method being used
     */
    public static final ConfigKey<StorageType> STORAGE_METHOD = notReloadable(
            key((config, path, def) -> StorageType.parse(config.getString(path, def.getDriver()),
                    def), "storage-method", StorageType.MARIADB));

    /**
     * The database settings, username, password, etc. for use by any database
     */
    public static final ConfigKey<StorageCredentials> DATABASE_VALUES =
            notReloadable(key((config) -> {
                int maxPoolSize = config.getInteger("data.pool-settings.maximum-pool-size",
                        config.getInteger("data.pool-settings.pool-size", 10));
                int minIdle = config.getInteger("data.pool-settings.minimum-idle", maxPoolSize);
                int maxLifetime = config.getInteger("data.pool-settings.maximum-lifetime", 1800000);
                int keepAliveTime = config.getInteger("data.pool-settings.keepalive-time", 0);
                int connectionTimeout =
                        config.getInteger("data.pool-settings.connection-timeout", 5000);
                Map<String, String> props = ImmutableMap.copyOf(
                        config.getStringMap("data.pool-settings.properties", ImmutableMap.of()));

                return new StorageCredentials(config.getString("data.address", "localhost"),
                        config.getString("data.database", "dirtcore"),
                        config.getString("data.username", "root"),
                        config.getString("data.password", ""), maxPoolSize, minIdle, maxLifetime,
                        keepAliveTime, connectionTimeout, props);
            }));

    /**
     * The prefix for any SQL tables
     */
    public static final ConfigKey<String> SQL_TABLE_PREFIX =
            notReloadable(stringKey("data.table-prefix", "dirtcore_"));

    /**
     * A list of the keys defined in this class.
     */
    private static final List<ConfigKey<?>> KEYS = KeyedConfiguration.initialise(ConfigKeys.class);

    private static void consumeVoteRewardMap(@NonNull final ConfigurationAdapter config,
            @NonNull final String path, @NonNull final Map<Integer, VoteReward> map,
            final Map.@NonNull Entry<String, Object> entry) {
        final Object value = entry.getValue();

        try {
            // check if key is an integer first
            final int key = Integer.parseInt(entry.getKey());

            if (!(value instanceof Map)) {
                throw new IllegalArgumentException("Expected a map.");
            }

            final Map<?, ?> innerMap = (Map<?, ?>) value;

            if (innerMap.size() != 1) {
                throw new IllegalArgumentException("Map is not size 1.");
            }

            final Map.Entry<?, ?> innerEntry = innerMap.entrySet().iterator().next();
            final Object innerKey = innerEntry.getKey();

            if (!(innerKey instanceof String)) {
                throw new IllegalArgumentException("Inner key is not a string.");
            }

            final String innerString = (String) innerKey;
            final int space = Integer.parseInt(innerString);
            final Object innerValue = innerEntry.getValue();

            if (!(innerValue instanceof List)) {
                throw new IllegalArgumentException("Inner value is not a list.");
            }

            final List<?> innerList = (List<?>) innerValue;
            final ImmutableList.Builder<String> builder = ImmutableList.builder();

            for (final Object object : innerList) {
                if (object instanceof String) {
                    builder.add((String) object);
                }
            }

            final List<String> commands = builder.build();

            if (key >= 0 && space >= 0 && !commands.isEmpty()) {
                map.put(key, VoteReward.of(space, commands));
            }
        } catch (final IllegalArgumentException e) {
            config.getLogger().warn("Skipping map entry for '{}': {}", path, e.getMessage());
        }
    }

    private static void consumePermissionMap(@NonNull final ConfigurationAdapter config,
            @NonNull final String path, @NonNull final Map<Long, Set<Permission>> map,
            final Map.@NonNull Entry<String, Object> entry) {
        final Object value = entry.getValue();

        try {
            // check if key is a long first
            final long key = Long.parseLong(entry.getKey());

            if (!(value instanceof Collection)) {
                throw new IllegalArgumentException("Expected a collection.");
            }

            final Collection<?> innerCollection = (Collection<?>) value;
            final ImmutableSet.Builder<Permission> builder = ImmutableSet.builder();

            for (final Object o : innerCollection) {
                if (!(o instanceof String)) {
                    throw new IllegalArgumentException("Value in collection is not a string.");
                }

                final String s = (String) o;

                try {
                    final Permission permission =
                            DiscordPermission.fromString(s.toLowerCase(Locale.ROOT));

                    if (permission != DiscordPermission.NONE) {
                        builder.add(permission);
                    }
                } catch (final IllegalArgumentException ignored) {}
            }

            final Set<Permission> permissions = builder.build();

            if (!permissions.isEmpty()) {
                map.put(key, permissions);
            }
        } catch (final IllegalArgumentException e) {
            config.getLogger().warn("Skipping map entry for '{}': {}", path, e.getMessage());
        }
    }

    public static List<ConfigKey<?>> getKeys() {
        return KEYS;
    }
}
