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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.entity.selector;

import com.google.common.collect.ImmutableSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorOptions;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
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

                for (final GameType gameType : GameType.values()) {
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
            final GameType gametype = this.getGameTypeByName(s);

            if (gametype == null) {
                parser.getReader().setCursor(i);
                throw ERROR_GAME_MODE_INVALID.createWithContext(parser.getReader(), s);
            }

            parser.setIncludesEntities(false);
            parser.addPredicate(entity -> {
                if (!(entity instanceof EntityPlayerMP)) {
                    return false;
                }

                final GameType gameType =
                        ((EntityPlayerMP) entity).interactionManager.getGameType();
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

                final Team team = entity.getTeam();
                final String s1 = team == null ? "" : team.getName();
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
                final Set<String> entitiesAsString = ImmutableSet.<String>builder()
                        .addAll(EntityList.getEntityNameList().stream()
                                .map(ResourceLocation::toString).iterator())
                        .add("player").build();

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
                final Class<? extends Entity> entityClass =
                        EntityList.getClass(new ResourceLocation(s));

                if (entityClass != null) {
                    entityType = entityClass;
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
                            final String s1 = entity.getName();

                            for (final Map.Entry<String, MinMaxBounds.Integers> entry :
                                    map.entrySet()) {
                                final ScoreObjective objective =
                                        scoreboard.getObjective(entry.getKey());

                                if (objective == null) {
                                    return false;
                                }

                                if (scoreboard.entityHasObjective(s1, objective)) {
                                    return false;
                                }

                                final Score score = scoreboard.getOrCreateScore(s1, objective);
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
            final Map<ResourceLocation, Predicate<AdvancementProgress>> map = new HashMap<>();

            reader.expect('{');
            reader.skipWhitespace();

            while (reader.canRead() && reader.peek() != '}') {
                reader.skipWhitespace();

                final ResourceLocation resourceLocation = ForgeUtils.read(reader);

                reader.skipWhitespace();
                reader.expect('=');
                reader.skipWhitespace();

                if (reader.canRead() && reader.peek() == '{') {
                    final Map<String, Predicate<CriterionProgress>> map1 = new HashMap<>();

                    reader.skipWhitespace();
                    reader.expect('{');
                    reader.skipWhitespace();

                    while (reader.canRead() && reader.peek() != '}') {
                        reader.skipWhitespace();

                        final String s = reader.readUnquotedString();

                        reader.skipWhitespace();
                        reader.expect('=');
                        reader.skipWhitespace();

                        final boolean flag1 = reader.readBoolean();

                        map1.put(s, (p_175186_) -> p_175186_.isObtained() == flag1);
                        reader.skipWhitespace();

                        if (reader.canRead() && reader.peek() == ',') {
                            reader.skip();
                        }
                    }

                    reader.skipWhitespace();
                    reader.expect('}');
                    reader.skipWhitespace();
                    map.put(resourceLocation, (p_175169_) -> {
                        for (final Map.Entry<String, Predicate<CriterionProgress>> entry :
                                map1.entrySet()) {
                            final CriterionProgress criterionprogress =
                                    p_175169_.getCriterionProgress(entry.getKey());

                            if (criterionprogress == null || !entry.getValue()
                                    .test(criterionprogress)) {
                                return false;
                            }
                        }

                        return true;
                    });
                } else {
                    final boolean flag = reader.readBoolean();
                    map.put(resourceLocation, (p_175183_) -> p_175183_.isDone() == flag);
                }

                reader.skipWhitespace();

                if (reader.canRead() && reader.peek() == ',') {
                    reader.skip();
                }
            }

            reader.expect('}');

            if (!map.isEmpty()) {
                parser.addPlatformPredicate(entity -> {
                    final MinecraftServer server = entity.getServer();

                    if (server == null || !(entity instanceof EntityPlayerMP)) {
                        return false;
                    }

                    final EntityPlayerMP player = (EntityPlayerMP) entity;
                    final PlayerAdvancements playerAdvancements = player.getAdvancements();
                    final AdvancementManager serverAdvancementManager =
                            server.getAdvancementManager();

                    for (final Map.Entry<ResourceLocation, Predicate<AdvancementProgress>> entry
                            : map.entrySet()) {
                        final Advancement advancement =
                                serverAdvancementManager.getAdvancement(entry.getKey());

                        if (advancement == null || !entry.getValue()
                                .test(playerAdvancements.getProgress(advancement))) {
                            return false;
                        }
                    }

                    return true;
                });

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

    private @Nullable GameType getGameTypeByName(@NonNull final String name) {
        for (final GameType gametype : GameType.values()) {
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
}
