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

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.WrappedMinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractEntitySelectorOptions<T extends AbstractEntitySelectorParser<?, ?,
        T>> {

    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Invalid or unknown entity type '%s'", o));
    public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Invalid or unknown game mode '%s'", o));
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Option '%s' isn't applicable here", o));
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE =
            new SimpleCommandExceptionType(new LiteralMessage("Level shouldn't be negative"));
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL =
            new SimpleCommandExceptionType(new LiteralMessage("Limit must be at least 1"));
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE =
            new SimpleCommandExceptionType(new LiteralMessage("Distance cannot be negative"));
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Invalid or unknown sort type '%s'", o));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION =
            new DynamicCommandExceptionType(o -> new LiteralMessage("Unknown option '%s'", o));
    private final Map<String, Option<T>> optionMap = new HashMap<>();

    @NonNull
    protected abstract Modifier<T> provideGameModeHandler();

    @NonNull
    protected abstract Modifier<T> provideTeamHandler();

    @NonNull
    protected abstract Modifier<T> provideTypeHandler();

    @NonNull
    protected abstract Modifier<T> provideTagHandler();

    @NonNull
    protected abstract Modifier<T> provideScoresHandler();

    @NonNull
    protected abstract Modifier<T> provideAdvancementsHandler();

    @NonNull
    protected abstract Modifier<T> provideCustomPredicateHandler();

    @NonNull
    public Modifier<T> get(final T parser, final String id,
            final int cursor) throws CommandSyntaxException {
        final Option<T> option = this.optionMap.get(id);

        if (option != null) {
            if (option.canUse(parser)) {
                return option.getModifier();
            }

            throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), id);
        }

        parser.getReader().setCursor(cursor);
        throw ERROR_UNKNOWN_OPTION.createWithContext(parser.getReader(), id);
    }

    public void suggestNames(final T parser, final SuggestionsBuilder builder) {
        final String s = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (final Map.Entry<String, Option<T>> entry : this.optionMap.entrySet()) {
            if (entry.getValue().canUse(parser) && entry.getKey().toLowerCase(Locale.ROOT)
                    .startsWith(s)) {
                builder.suggest(entry.getKey() + "=", entry.getValue().getDescription());
            }
        }
    }

    public void init() {
        if (this.optionMap.isEmpty()) {
            this.register("name", parser -> {
                final int i = parser.getReader().getCursor();
                final boolean flag = parser.shouldInvertValue();
                final String s = parser.getReader().readString();

                if (parser.hasNameNotEquals() && !flag) {
                    parser.getReader().setCursor(i);
                    throw ERROR_INAPPLICABLE_OPTION.createWithContext(parser.getReader(), "name");
                }

                if (flag) {
                    parser.setHasNameNotEquals(true);
                } else {
                    parser.setHasNameEquals(true);
                }

                parser.addPredicate(entity -> entity.getName().equals(s) != flag);
            }, parser -> !parser.hasNameEquals(), "Entity name");
            this.register("distance", parser -> {
                final int i = parser.getReader().getCursor();
                final MinMaxBounds.Doubles doubles =
                        MinMaxBounds.Doubles.fromReader(parser.getReader());

                if ((doubles.getMin() == null || !(doubles.getMin() < 0.0D)) && (
                        doubles.getMax() == null || !(doubles.getMax() < 0.0D))) {
                    parser.setDistance(doubles);
                    parser.setWorldLimited();
                } else {
                    parser.getReader().setCursor(i);
                    throw ERROR_RANGE_NEGATIVE.createWithContext(parser.getReader());
                }
            }, parser -> parser.getDistance().isAny(), "Distance to entity");
            this.register("level", parser -> {
                final int i = parser.getReader().getCursor();
                final MinMaxBounds.Integers integers =
                        MinMaxBounds.Integers.fromReader(parser.getReader());
                if ((integers.getMin() == null || integers.getMin() >= 0) && (
                        integers.getMax() == null || integers.getMax() >= 0)) {
                    parser.setLevel(integers);
                    parser.setIncludesEntities(false);
                } else {
                    parser.getReader().setCursor(i);
                    throw ERROR_LEVEL_NEGATIVE.createWithContext(parser.getReader());
                }
            }, parser -> parser.getLevel().isAny(), "Experience level");
            this.register("x", parser -> {
                parser.setWorldLimited();
                parser.setX(parser.getReader().readDouble());
            }, parser -> parser.getX() == null, "x position");
            this.register("y", parser -> {
                parser.setWorldLimited();
                parser.setY(parser.getReader().readDouble());
            }, parser -> parser.getY() == null, "y position");
            this.register("z", parser -> {
                parser.setWorldLimited();
                parser.setZ(parser.getReader().readDouble());
            }, (p_121403_) -> p_121403_.getZ() == null, "z position");
            this.register("dx", parser -> {
                parser.setWorldLimited();
                parser.setDeltaX(parser.getReader().readDouble());
            }, parser -> parser.getDeltaX() == null, "Entities between x and x + dx");
            this.register("dy", parser -> {
                parser.setWorldLimited();
                parser.setDeltaY(parser.getReader().readDouble());
            }, parser -> parser.getDeltaY() == null, "Entities between y and y + dy");
            this.register("dz", parser -> {
                parser.setWorldLimited();
                parser.setDeltaZ(parser.getReader().readDouble());
            }, parser -> parser.getDeltaZ() == null, "Entities between z and z + dz");
            this.register("x_rotation", parser -> parser.setRotX(
                            WrappedMinMaxBounds.fromReader(parser.getReader(), true,
                                    Mth::wrapDegrees)),
                    parser -> parser.getRotX() == WrappedMinMaxBounds.ANY, "Entity's x rotation");
            this.register("y_rotation", parser -> parser.setRotY(
                            WrappedMinMaxBounds.fromReader(parser.getReader(), true,
                                    Mth::wrapDegrees)),
                    parser -> parser.getRotY() == WrappedMinMaxBounds.ANY, "Entity's y rotation");
            this.register("limit", parser -> {
                        final int i = parser.getReader().getCursor();
                        final int j = parser.getReader().readInt();
                        if (j < 1) {
                            parser.getReader().setCursor(i);
                            throw ERROR_LIMIT_TOO_SMALL.createWithContext(parser.getReader());
                        }
                        parser.setMaxResults(j);
                        parser.setLimited(true);
                    }, parser -> !parser.isCurrentEntity() && !parser.isLimited(),
                    "Maximum number of entities to return");
            this.register("sort", parser -> {
                final int i = parser.getReader().getCursor();
                final String s = parser.getReader().readUnquotedString();

                parser.setSuggestions((builder, consumer) -> SharedSuggestionProvider.suggestFuture(
                        Arrays.asList("nearest", "furthest", "random", "arbitrary"), builder));

                final BiConsumer<Vec3, List<? extends Entity>> biconsumer;

                switch (s) {
                    case "nearest":
                        biconsumer = AbstractEntitySelectorParser.ORDER_NEAREST;
                        break;
                    case "furthest":
                        biconsumer = AbstractEntitySelectorParser.ORDER_FURTHEST;
                        break;
                    case "random":
                        biconsumer = AbstractEntitySelectorParser.ORDER_RANDOM;
                        break;
                    case "arbitrary":
                        biconsumer = AbstractEntitySelector.ORDER_ARBITRARY;
                        break;
                    default:
                        parser.getReader().setCursor(i);
                        throw ERROR_SORT_UNKNOWN.createWithContext(parser.getReader(), s);
                }

                parser.setOrder(biconsumer);
                parser.setSorted(true);
            }, parser -> !parser.isCurrentEntity() && !parser.isSorted(), "Sort the entities");
            this.register("gamemode", this.provideGameModeHandler(),
                    parser -> !parser.hasGamemodeEquals(), "Players with game mode");
            this.register("team", this.provideTeamHandler(), parser -> !parser.hasTeamEquals(),
                    "Entities on team");
            this.register("type", this.provideTypeHandler(), parser -> !parser.isTypeLimited(),
                    "Entities of type");
            this.register("tag", this.provideTagHandler(), parser -> this.areTagsSupported(),
                    "Entities with tag");
            this.register("scores", this.provideScoresHandler(), parser -> !parser.hasScores(),
                    "Entities with scores");
            this.register("advancements", this.provideAdvancementsHandler(),
                    parser -> !parser.hasAdvancements(), "Players with advancements");
            this.register("predicate", this.provideCustomPredicateHandler(),
                    parser -> this.areCustomPredicatesSupported(), "Custom predicate");
        }
    }

    protected void register(@NonNull final String id, @NonNull final Modifier<T> handler,
            @NonNull final Predicate<T> predicate, @NonNull final String tooltip) {
        this.optionMap.put(id, new Option<>(handler, predicate, tooltip));
    }

    protected boolean areTagsSupported() {
        return true;
    }

    protected boolean areCustomPredicatesSupported() {
        return true;
    }

    public static class Option<T extends AbstractEntitySelectorParser<?, ?, T>> {

        @NonNull
        private final Modifier<T> modifier;
        @NonNull
        private final Predicate<T> canUse;
        @NonNull
        private final Message description;

        public Option(@NonNull final Modifier<T> modifier, @NonNull final Predicate<T> canUse,
                @NonNull final String description) {
            this.modifier = modifier;
            this.canUse = canUse;
            this.description = new LiteralMessage(description);
        }

        @NonNull
        public Modifier<T> getModifier() {
            return this.modifier;
        }

        public boolean canUse(@NonNull final T parser) {
            return this.canUse.test(parser);
        }

        @NonNull
        public Message getDescription() {
            return this.description;
        }
    }

    public interface Modifier<T extends AbstractEntitySelectorParser<?, ?, T>> {

        void handle(@NonNull T parser) throws CommandSyntaxException;
    }
}
