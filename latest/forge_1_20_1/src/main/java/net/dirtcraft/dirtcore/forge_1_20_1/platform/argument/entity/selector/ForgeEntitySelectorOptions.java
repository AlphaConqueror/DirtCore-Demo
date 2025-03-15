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

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.entity.selector;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorOptions;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEntitySelectorOptions extends AbstractEntitySelectorOptions<ForgeEntitySelectorParser> {

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
            final GameType gametype = GameType.byName(s, null);

            if (gametype == null) {
                parser.getReader().setCursor(i);
                throw ERROR_GAME_MODE_INVALID.createWithContext(parser.getReader(), s);
            }

            parser.setIncludesEntities(false);
            parser.addPredicate(entity -> {
                if (!(entity instanceof ServerPlayer)) {
                    return false;
                }

                final GameType gameType = ((ServerPlayer) entity).gameMode.getGameModeForPlayer();
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
                if (!(entity instanceof LivingEntity)) {
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

    @SuppressWarnings("deprecation")
    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideTypeHandler() {
        return parser -> {
            parser.setSuggestions((builder, consumer) -> {
                ForgeSharedSuggestionProvider.suggestResource(
                        BuiltInRegistries.ENTITY_TYPE.keySet(), builder,
                        String.valueOf(AbstractEntitySelectorParser.SYNTAX_NOT));
                ForgeSharedSuggestionProvider.suggestResource(
                        BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), builder,
                        new String(
                                new char[] {AbstractEntitySelectorParser.SYNTAX_NOT,
                                        AbstractEntitySelectorParser.SYNTAX_TAG}));

                if (!parser.isTypeLimitedInversely()) {
                    ForgeSharedSuggestionProvider.suggestResource(
                            BuiltInRegistries.ENTITY_TYPE.keySet(), builder);
                    ForgeSharedSuggestionProvider.suggestResource(
                            BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location),
                            builder, String.valueOf(AbstractEntitySelectorParser.SYNTAX_TAG));
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

            if (parser.isTag()) {
                final TagKey<EntityType<?>> tagkey =
                        TagKey.create(Registries.ENTITY_TYPE, ForgeUtils.read(parser.getReader()));
                parser.addPlatformPredicate(entity -> entity.getType().is(tagkey) != flag);
            } else {
                final ResourceLocation resourcelocation = ForgeUtils.read(parser.getReader());
                final EntityType<?> entityType =
                        BuiltInRegistries.ENTITY_TYPE.getOptional(resourcelocation)
                                .orElseThrow(() -> {
                                    parser.getReader().setCursor(i);
                                    return ERROR_ENTITY_TYPE_INVALID.createWithContext(
                                            parser.getReader(), resourcelocation.toString());
                                });

                if (Objects.equals(EntityType.PLAYER, entityType) && !flag) {
                    parser.setIncludesEntities(false);
                }

                parser.addPlatformPredicate(
                        entity -> Objects.equals(entityType, entity.getType()) != flag);

                if (!flag) {
                    parser.limitToType(entityType);
                }
            }
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideTagHandler() {
        return parser -> {
            final boolean flag = parser.shouldInvertValue();
            final String s = parser.getReader().readUnquotedString();

            parser.addPlatformPredicate(entity -> {
                if ("".equals(s)) {
                    return entity.getTags().isEmpty() != flag;
                }

                return entity.getTags().contains(s) != flag;
            });
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
                parser.addPlatformPredicate(entity -> {
                    final MinecraftServer server = entity.getServer();

                    if (server == null) {
                        return false;
                    }

                    final Scoreboard scoreboard = server.getScoreboard();
                    final String s1 = entity.getScoreboardName();

                    for (final Map.Entry<String, MinMaxBounds.Integers> entry : map.entrySet()) {
                        final Objective objective = scoreboard.getObjective(entry.getKey());

                        if (objective == null) {
                            return false;
                        }

                        if (!scoreboard.hasPlayerScore(s1, objective)) {
                            return false;
                        }

                        final Score score = scoreboard.getOrCreatePlayerScore(s1, objective);
                        final int i = score.getScore();

                        if (!entry.getValue().matches(i)) {
                            return false;
                        }
                    }

                    return true;
                });
            }

            parser.setHasScores(true);
        };
    }

    @Override
    protected @NonNull Modifier<ForgeEntitySelectorParser> provideAdvancementsHandler() {
        return parser -> {
            final StringReader stringreader = parser.getReader();
            final Map<ResourceLocation, Predicate<AdvancementProgress>> map = new HashMap<>();
            stringreader.expect('{');
            stringreader.skipWhitespace();

            while (stringreader.canRead() && stringreader.peek() != '}') {
                stringreader.skipWhitespace();

                final ResourceLocation resourceLocation = ForgeUtils.read(stringreader);

                stringreader.skipWhitespace();
                stringreader.expect('=');
                stringreader.skipWhitespace();

                if (stringreader.canRead() && stringreader.peek() == '{') {
                    final Map<String, Predicate<CriterionProgress>> map1 = new HashMap<>();

                    stringreader.skipWhitespace();
                    stringreader.expect('{');
                    stringreader.skipWhitespace();

                    while (stringreader.canRead() && stringreader.peek() != '}') {
                        stringreader.skipWhitespace();

                        final String s = stringreader.readUnquotedString();

                        stringreader.skipWhitespace();
                        stringreader.expect('=');
                        stringreader.skipWhitespace();

                        final boolean flag1 = stringreader.readBoolean();

                        map1.put(s, (p_175186_) -> p_175186_.isDone() == flag1);
                        stringreader.skipWhitespace();

                        if (stringreader.canRead() && stringreader.peek() == ',') {
                            stringreader.skip();
                        }
                    }

                    stringreader.skipWhitespace();
                    stringreader.expect('}');
                    stringreader.skipWhitespace();
                    map.put(resourceLocation, (p_175169_) -> {
                        for (final Map.Entry<String, Predicate<CriterionProgress>> entry :
                                map1.entrySet()) {
                            final CriterionProgress criterionprogress =
                                    p_175169_.getCriterion(entry.getKey());

                            if (criterionprogress == null || !entry.getValue()
                                    .test(criterionprogress)) {
                                return false;
                            }
                        }

                        return true;
                    });
                } else {
                    final boolean flag = stringreader.readBoolean();
                    map.put(resourceLocation, (p_175183_) -> p_175183_.isDone() == flag);
                }

                stringreader.skipWhitespace();

                if (stringreader.canRead() && stringreader.peek() == ',') {
                    stringreader.skip();
                }
            }

            stringreader.expect('}');

            if (!map.isEmpty()) {
                parser.addPlatformPredicate(entity -> {
                    final MinecraftServer server = entity.getServer();

                    if (server == null || !(entity instanceof ServerPlayer)) {
                        return false;
                    }

                    final ServerPlayer player = (ServerPlayer) entity;
                    final PlayerAdvancements playerAdvancements = player.getAdvancements();
                    final ServerAdvancementManager serverAdvancementManager =
                            server.getAdvancements();

                    for (final Map.Entry<ResourceLocation, Predicate<AdvancementProgress>> entry
                            : map.entrySet()) {
                        final Advancement advancement =
                                serverAdvancementManager.getAdvancement(entry.getKey());

                        if (advancement == null || !entry.getValue()
                                .test(playerAdvancements.getOrStartProgress(advancement))) {
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
            final boolean flag = parser.shouldInvertValue();
            final ResourceLocation resourceLocation = ForgeUtils.read(parser.getReader());

            parser.addPlatformPredicate(entity -> {
                final Level level = entity.level();

                if (!(level instanceof ServerLevel)) {
                    return false;
                }

                final ServerLevel serverLevel = (ServerLevel) level;
                final LootItemCondition lootItemCondition = serverLevel.getServer().getLootData()
                        .getElement(LootDataType.PREDICATE, resourceLocation);

                if (lootItemCondition == null) {
                    return false;
                }

                final LootParams lootParams = (new LootParams.Builder(serverLevel)).withParameter(
                                LootContextParams.THIS_ENTITY, entity)
                        .withParameter(LootContextParams.ORIGIN, entity.position())
                        .create(LootContextParamSets.SELECTOR);
                final LootContext lootContext = (new LootContext.Builder(lootParams)).create(null);

                lootContext.pushVisitedElement(LootContext.createVisitedEntry(lootItemCondition));

                return flag ^ lootItemCondition.test(lootContext);
            });
        };
    }
}
