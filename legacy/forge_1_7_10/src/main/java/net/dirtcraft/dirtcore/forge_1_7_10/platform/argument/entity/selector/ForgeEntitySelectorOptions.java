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

package net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.entity.selector;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorOptions;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeSharedSuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.world.WorldSettings;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeEntitySelectorOptions extends AbstractEntitySelectorOptions<ForgeEntitySelectorParser> {

    private static final SimpleCommandExceptionType ERROR_INVALID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));

    @NonNull
    private final DirtCoreForgePlugin plugin;

    public ForgeEntitySelectorOptions(@NonNull final DirtCoreForgePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideGameModeHandler() {
        return parser -> {
            parser.setSuggestions((builder, consumer) -> {
                String s1 = builder.getRemaining().toLowerCase(Locale.ROOT);
                boolean flag1 = !parser.hasGamemodeNotEquals();
                boolean flag2 = true;

                if (!s1.isEmpty()) {
                    if (s1.charAt(0) == AbstractEntitySelectorParser.SYNTAX_NOT) {
                        flag1 = false;
                        s1 = s1.substring(1);
                    } else {
                        flag2 = false;
                    }
                }

                for (final WorldSettings.GameType gameType : WorldSettings.GameType.values()) {
                    if (gameType.getName().toLowerCase(Locale.ROOT).startsWith(s1)) {
                        if (flag2) {
                            builder.suggest(
                                    AbstractEntitySelectorParser.SYNTAX_NOT + gameType.getName());
                        }

                        if (flag1) {
                            builder.suggest(gameType.getName());
                        }
                    }
                }

                return builder.buildFuture();
            });

            final int i = parser.getReader().getCursor();
            final boolean flag = parser.shouldInvertValue();

            if (parser.hasGamemodeNotEquals() && !flag) {
                parser.getReader().setCursor(i);
                throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "gamemode");
            }

            final String s = parser.getReader().readUnquotedString();
            final WorldSettings.GameType gametype = this.getGameTypeByName(s);

            if (gametype == null) {
                parser.getReader().setCursor(i);
                throw ERROR_GAME_MODE_INVALID.createWithContext(parser.getReader(), s);
            }

            parser.setIncludesEntities(false);
            parser.addPredicate(entity -> {
                if (!(entity instanceof EntityPlayerMP)) {
                    return false;
                }

                final WorldSettings.GameType gameType =
                        ((EntityPlayerMP) entity).theItemInWorldManager.getGameType();
                return flag == (gameType != gametype);
            });

            if (flag) {
                parser.setHasGamemodeNotEquals(true);
            } else {
                parser.setHasGamemodeEquals(true);
            }
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideTeamHandler() {
        return parser -> {
            final boolean flag = parser.shouldInvertValue();
            final String s = parser.getReader().readUnquotedString();

            parser.addPlatformPredicate(entity -> {
                if (!(entity instanceof EntityLivingBase)) {
                    return false;
                }

                final Team team = ((EntityLivingBase) entity).getTeam();
                final String s1 = team == null ? "" : team.getRegisteredName();
                return s1.equals(s) != flag;
            });

            if (flag) {
                parser.setHasTeamNotEquals(true);
            } else {
                parser.setHasTeamEquals(true);
            }
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideTypeHandler() {
        return parser -> {
            parser.setSuggestions((builder, consumer) -> {
                //noinspection unchecked
                final Set<String> entitiesAsString = ImmutableSet.<String>builder()
                        .addAll((Set<String>) EntityList.func_151515_b())
                        .add("Player").build();

                ForgeSharedSuggestionProvider.suggest(entitiesAsString.stream()
                        .map(s -> AbstractEntitySelectorParser.SYNTAX_NOT + s)
                        .collect(ImmutableCollectors.toSet()), builder);

                if (!parser.isTypeLimitedInversely()) {
                    ForgeSharedSuggestionProvider.suggest(entitiesAsString, builder);
                }

                return builder.buildFuture();
            });

            final int i = parser.getReader().getCursor();
            final boolean flag = parser.shouldInvertValue();

            if (parser.isTypeLimitedInversely() && !flag) {
                parser.getReader().setCursor(i);
                throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "type");
            }

            if (flag) {
                parser.setTypeLimitedInversely();
            }

            final StringReader reader = parser.getReader();
            final int start = reader.getCursor();

            while (reader.canRead() && this.isAllowedInEntityClass(reader.peek())) {
                reader.skip();
            }

            final String s = reader.getString().substring(start, reader.getCursor());
            Class<? extends Entity> entityType = null;

            if (s.equals("Player")) {
                entityType = EntityPlayer.class;
            } else {
                final Object object = EntityList.stringToClassMapping.get(s);

                if (object instanceof Class<?>) {
                    final Class<?> clazz = (Class<?>) object;

                    if (Entity.class.isAssignableFrom(clazz)) {
                        //noinspection unchecked
                        entityType = (Class<? extends Entity>) clazz;
                    }
                }
            }

            if (entityType == null) {
                reader.setCursor(i);
                throw ERROR_ENTITY_TYPE_INVALID.createWithContext(reader, s);
            }

            if (EntityPlayer.class.isAssignableFrom(entityType) && !flag) {
                parser.setIncludesEntities(false);
            }

            final Class<? extends Entity> finalEntityType = entityType;

            parser.addPlatformPredicate(
                    entity -> Objects.equals(finalEntityType, entity.getClass()) != flag);

            if (!flag) {
                parser.limitToType(entityType);
            }
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideTagHandler() {
        return parser -> {
            throw new UnsupportedOperationException("Tags are not supported.");
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideScoresHandler() {
        return parser -> {
            final StringReader reader = parser.getReader();
            final Map<String, MinMaxBounds.Integers> map = new HashMap<>();

            reader.expect('{');
            reader.skipWhitespace();

            while (reader.canRead() && reader.peek() != '}') {
                reader.skipWhitespace();

                final String s = reader.readUnquotedString();

                reader.skipWhitespace();
                reader.expect('=');
                reader.skipWhitespace();

                final MinMaxBounds.Integers integers = MinMaxBounds.Integers.fromReader(reader);

                map.put(s, integers);
                reader.skipWhitespace();

                if (reader.canRead() && reader.peek() == ',') {
                    reader.skip();
                }
            }

            reader.expect('}');

            if (!map.isEmpty()) {
                parser.addPlatformPredicate(
                        entity -> this.plugin.getBootstrap().getServer().map(server -> {
                            final Scoreboard scoreboard = server.getEntityWorld().getScoreboard();
                            final String s1 = entity.getCommandSenderName();

                            for (final Map.Entry<String, MinMaxBounds.Integers> entry :
                                    map.entrySet()) {
                                final ScoreObjective objective =
                                        scoreboard.getObjective(entry.getKey());

                                if (objective == null) {
                                    return false;
                                }

                                if (!this.hasPlayerScore(scoreboard, s1, objective)) {
                                    return false;
                                }

                                final Score score =
                                        this.getOrCreatePlayerScore(scoreboard, s1, objective);
                                final int i = score.getScorePoints();

                                if (!entry.getValue().matches(i)) {
                                    return false;
                                }
                            }

                            return true;
                        }).orElse(false));
            }

            parser.setHasScores(true);
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideAdvancementsHandler() {
        return parser -> {
            final StringReader reader = parser.getReader();
            final Map<Achievement, Predicate<StatisticsFile>> map = new HashMap<>();

            reader.expect('{');
            reader.skipWhitespace();

            while (reader.canRead() && reader.peek() != '}') {
                reader.skipWhitespace();

                final Achievement achievement = this.readAchievement(reader);

                reader.skipWhitespace();
                reader.expect('=');
                reader.skipWhitespace();

                final boolean flag = reader.readBoolean();

                map.put(achievement,
                        statisticsFile -> statisticsFile.hasAchievementUnlocked(achievement)
                                && statisticsFile.canUnlockAchievement(achievement) == flag);
                reader.skipWhitespace();

                if (reader.canRead() && reader.peek() == ',') {
                    reader.skip();
                }
            }

            reader.expect('}');

            if (!map.isEmpty()) {
                parser.addPlatformPredicate(
                        entity -> this.plugin.getBootstrap().getServer().map(server -> {
                            if (!(entity instanceof EntityPlayerMP)) {
                                return false;
                            }

                            final EntityPlayerMP player = (EntityPlayerMP) entity;

                            for (final Map.Entry<Achievement, Predicate<StatisticsFile>> entry :
                                    map.entrySet()) {
                                if (!entry.getValue().test(player.getStatFile())) {
                                    return false;
                                }
                            }

                            return true;
                        }).orElse(false));
                parser.setIncludesEntities(false);
            }

            parser.setHasAdvancements(true);
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideCustomPredicateHandler() {
        return parser -> {
            throw new UnsupportedOperationException("Custom predicates are not supported.");
        };
    }

    @Override
    protected boolean areTagsSupported() {
        return false;
    }

    private WorldSettings.@Nullable GameType getGameTypeByName(@NonNull final String name) {
        for (final WorldSettings.GameType gametype : WorldSettings.GameType.values()) {
            if (gametype.name().toLowerCase(Locale.ROOT).equals(name)) {
                return gametype;
            }
        }

        return null;
    }

    private boolean isAllowedInEntityClass(final char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_'
                || c == ':' || c == '.' || c == '-';
    }

    private boolean hasPlayerScore(@NonNull final Scoreboard scoreboard,
            @NonNull final String pName, final ScoreObjective pObjective) {
        //noinspection unchecked
        final Map<ScoreObjective, Score> map =
                (Map<ScoreObjective, Score>) scoreboard.func_96510_d(pName);
        return map.get(pObjective) != null;
    }

    @NonNull
    private Score getOrCreatePlayerScore(final Scoreboard scoreboard, final String pUsername,
            final ScoreObjective pObjective) {
        //noinspection unchecked
        final Map<ScoreObjective, Score> map =
                (Map<ScoreObjective, Score>) scoreboard.func_96510_d(pUsername);

        return map.computeIfAbsent(pObjective, (p_83487_) -> {
            final Score score = new Score(scoreboard, p_83487_, pUsername);
            score.setScorePoints(0);
            return score;
        });
    }

    @NonNull
    private Achievement readAchievement(final StringReader pReader) throws CommandSyntaxException {
        final int i = pReader.getCursor();

        while (pReader.canRead() && this.isAllowedInStatBase(pReader.peek())) {
            pReader.skip();
        }

        final String s = pReader.getString().substring(i, pReader.getCursor());
        //noinspection unchecked
        final Optional<Achievement> achievement =
                ((List<Achievement>) AchievementList.achievementList).stream()
                        .filter(a -> a.statId.equals(s)).findFirst();

        if (!achievement.isPresent()) {
            pReader.setCursor(i);
            throw ERROR_INVALID.createWithContext(pReader);
        }

        return achievement.get();
    }

    private boolean isAllowedInStatBase(final char c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '.';
    }
}
