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

import com.google.common.primitives.Doubles;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.DynamicCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.MinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.WrappedMinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class AbstractEntitySelectorParser<P extends DirtCorePlugin,
        T extends AbstractEntitySelector, U extends AbstractEntitySelectorParser<P, T, U>> {

    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_OPTIONS =
            new SimpleCommandExceptionType(new LiteralMessage("Expected end of options"));
    public static final DynamicCommandExceptionType ERROR_EXPECTED_OPTION_VALUE =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Expected value for option '%s'", o));
    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid name or UUID"));
    public static final SimpleCommandExceptionType ERROR_MISSING_SELECTOR_TYPE =
            new SimpleCommandExceptionType(new LiteralMessage("Missing selector type"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED =
            new SimpleCommandExceptionType(new LiteralMessage("Selector not allowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_SELECTOR_TYPE =
            new DynamicCommandExceptionType(
                    o -> new LiteralMessage("Unknown selector type '%s'", o));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_FURTHEST =
            (vec3, list) -> list.sort(
                    (o1, o2) -> Doubles.compare(o2.distanceToSqr(vec3), o1.distanceToSqr(vec3)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_NEAREST =
            (vec3, list) -> list.sort(
                    (o1, o2) -> Doubles.compare(o1.distanceToSqr(vec3), o2.distanceToSqr(vec3)));
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_RANDOM =
            (p_121264_, p_121265_) -> Collections.shuffle(p_121265_);
    public static final char SYNTAX_NOT = '!';
    public static final char SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR = '=';
    public static final char SYNTAX_SELECTOR_START = '@';
    public static final char SYNTAX_TAG = '#';
    protected static final char SELECTOR_ALL_ENTITIES = 'e';
    protected static final char SELECTOR_ALL_PLAYERS = 'a';
    protected static final char SELECTOR_CURRENT_ENTITY = 's';
    protected static final char SELECTOR_NEAREST_PLAYER = 'p';
    protected static final char SELECTOR_RANDOM_PLAYERS = 'r';
    protected static final char SYNTAX_OPTIONS_END = ']';
    protected static final char SYNTAX_OPTIONS_SEPARATOR = ',';
    protected static final char SYNTAX_OPTIONS_START = '[';
    protected final P plugin;
    protected final StringReader reader;
    protected final boolean allowSelectors;
    protected final boolean single;
    protected final boolean playersOnly;
    protected int maxResults;
    protected boolean includesEntities;
    protected boolean worldLimited;
    protected MinMaxBounds.Doubles distance = MinMaxBounds.Doubles.ANY;
    protected MinMaxBounds.Integers level = MinMaxBounds.Integers.ANY;
    @Nullable
    protected Double x;
    @Nullable
    protected Double y;
    @Nullable
    protected Double z;
    @Nullable
    protected Double deltaX;
    @Nullable
    protected Double deltaY;
    @Nullable
    protected Double deltaZ;
    protected WrappedMinMaxBounds rotX = WrappedMinMaxBounds.ANY;
    protected WrappedMinMaxBounds rotY = WrappedMinMaxBounds.ANY;
    protected Predicate<Entity> predicate = entity -> true;
    protected BiConsumer<Vec3, List<? extends Entity>> order =
            AbstractEntitySelector.ORDER_ARBITRARY;
    protected boolean currentEntity;
    @Nullable
    protected String playerName;
    protected int startPosition;
    @Nullable
    protected UUID entityUniqueId;
    protected BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>,
            CompletableFuture<Suggestions>>
            suggestions = SuggestionsBuilder.SUGGEST_NOTHING_BI;
    protected boolean hasNameEquals;
    protected boolean hasNameNotEquals;
    protected boolean isLimited;
    protected boolean isSorted;
    protected boolean hasGamemodeEquals;
    protected boolean hasGamemodeNotEquals;
    protected boolean hasTeamEquals;
    protected boolean hasTeamNotEquals;
    protected boolean typeInverse;
    protected boolean hasScores;
    protected boolean hasAdvancements;
    protected boolean usesSelectors;

    public AbstractEntitySelectorParser(final P plugin, final StringReader reader,
            final boolean single, final boolean playersOnly) {
        this(plugin, reader, true, single, playersOnly);
    }

    public AbstractEntitySelectorParser(final P plugin, final StringReader reader,
            final boolean allowSelectors, final boolean single, final boolean playersOnly) {
        this.plugin = plugin;
        this.reader = reader;
        this.allowSelectors = allowSelectors;
        this.single = single;
        this.playersOnly = playersOnly;
    }

    public abstract AbstractEntitySelector parse() throws CommandSyntaxException;

    public abstract boolean isTypeLimited();

    @NonNull
    protected abstract AbstractEntitySelectorOptions<U> getEntitySelectorOptions();

    @NonNull
    protected abstract U getThis();

    public void parseOptions() throws CommandSyntaxException {
        this.suggestions = this::suggestOptionsKey;
        this.reader.skipWhitespace();

        while (true) {
            if (this.reader.canRead() && this.reader.peek() != SYNTAX_OPTIONS_END) {
                this.reader.skipWhitespace();

                final int i = this.reader.getCursor();
                final String s = this.reader.readString();
                final AbstractEntitySelectorOptions.Modifier<U> modifier =
                        this.getEntitySelectorOptions().get(this.getThis(), s, i);

                this.reader.skipWhitespace();

                if (!this.reader.canRead()
                        || this.reader.peek() != SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR) {
                    this.reader.setCursor(i);
                    throw ERROR_EXPECTED_OPTION_VALUE.createWithContext(this.reader, s);
                }

                this.reader.skip();
                this.reader.skipWhitespace();
                this.suggestions = SuggestionsBuilder.SUGGEST_NOTHING_BI;
                modifier.handle(this.getThis());
                this.reader.skipWhitespace();
                this.suggestions = this::suggestOptionsNextOrClose;

                if (!this.reader.canRead()) {
                    continue;
                }

                if (this.reader.peek() == SYNTAX_OPTIONS_SEPARATOR) {
                    this.reader.skip();
                    this.suggestions = this::suggestOptionsKey;
                    continue;
                }

                if (this.reader.peek() != SYNTAX_OPTIONS_END) {
                    throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
                }
            }

            if (this.reader.canRead()) {
                this.reader.skip();
                this.suggestions = SuggestionsBuilder.SUGGEST_NOTHING_BI;
                return;
            }

            throw ERROR_EXPECTED_END_OF_OPTIONS.createWithContext(this.reader);
        }
    }

    public boolean shouldInvertValue() {
        this.reader.skipWhitespace();

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_NOT) {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }

        return false;
    }

    public boolean isTag() {
        this.reader.skipWhitespace();

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_TAG) {
            this.reader.skip();
            this.reader.skipWhitespace();
            return true;
        }

        return false;
    }

    public StringReader getReader() {
        return this.reader;
    }

    public void addPredicate(final Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(predicate);
    }

    public void setWorldLimited() {
        this.worldLimited = true;
    }

    public MinMaxBounds.Doubles getDistance() {
        return this.distance;
    }

    public void setDistance(final MinMaxBounds.Doubles pDistance) {
        this.distance = pDistance;
    }

    public MinMaxBounds.Integers getLevel() {
        return this.level;
    }

    public void setLevel(final MinMaxBounds.Integers pLevel) {
        this.level = pLevel;
    }

    public WrappedMinMaxBounds getRotX() {
        return this.rotX;
    }

    public void setRotX(final WrappedMinMaxBounds pRotX) {
        this.rotX = pRotX;
    }

    public WrappedMinMaxBounds getRotY() {
        return this.rotY;
    }

    public void setRotY(final WrappedMinMaxBounds pRotY) {
        this.rotY = pRotY;
    }

    @Nullable
    public Double getX() {
        return this.x;
    }

    public void setX(final double pX) {
        this.x = pX;
    }

    @Nullable
    public Double getY() {
        return this.y;
    }

    public void setY(final double pY) {
        this.y = pY;
    }

    @Nullable
    public Double getZ() {
        return this.z;
    }

    public void setZ(final double pZ) {
        this.z = pZ;
    }

    @Nullable
    public Double getDeltaX() {
        return this.deltaX;
    }

    public void setDeltaX(final double pDeltaX) {
        this.deltaX = pDeltaX;
    }

    @Nullable
    public Double getDeltaY() {
        return this.deltaY;
    }

    public void setDeltaY(final double pDeltaY) {
        this.deltaY = pDeltaY;
    }

    @Nullable
    public Double getDeltaZ() {
        return this.deltaZ;
    }

    public void setDeltaZ(final double pDeltaZ) {
        this.deltaZ = pDeltaZ;
    }

    public void setMaxResults(final int pMaxResults) {
        this.maxResults = pMaxResults;
    }

    public void setIncludesEntities(final boolean pIncludesEntities) {
        this.includesEntities = pIncludesEntities;
    }

    public BiConsumer<Vec3, List<? extends Entity>> getOrder() {
        return this.order;
    }

    public void setOrder(final BiConsumer<Vec3, List<? extends Entity>> pOrder) {
        this.order = pOrder;
    }

    public boolean isCurrentEntity() {
        return this.currentEntity;
    }

    public void setSuggestions(
            final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>,
                    CompletableFuture<Suggestions>> pSuggestionHandler) {
        this.suggestions = pSuggestionHandler;
    }

    public CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> consumer) {
        return this.suggestions.apply(builder.createOffset(this.reader.getCursor()), consumer);
    }

    public boolean hasNameEquals() {
        return this.hasNameEquals;
    }

    public void setHasNameEquals(final boolean pHasNameEquals) {
        this.hasNameEquals = pHasNameEquals;
    }

    public boolean hasNameNotEquals() {
        return this.hasNameNotEquals;
    }

    public void setHasNameNotEquals(final boolean pHasNameNotEquals) {
        this.hasNameNotEquals = pHasNameNotEquals;
    }

    public boolean isLimited() {
        return this.isLimited;
    }

    public void setLimited(final boolean pIsLimited) {
        this.isLimited = pIsLimited;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public void setSorted(final boolean pIsSorted) {
        this.isSorted = pIsSorted;
    }

    public boolean hasGamemodeEquals() {
        return this.hasGamemodeEquals;
    }

    public void setHasGamemodeEquals(final boolean pHasGamemodeEquals) {
        this.hasGamemodeEquals = pHasGamemodeEquals;
    }

    public boolean hasGamemodeNotEquals() {
        return this.hasGamemodeNotEquals;
    }

    public void setHasGamemodeNotEquals(final boolean pHasGamemodeNotEquals) {
        this.hasGamemodeNotEquals = pHasGamemodeNotEquals;
    }

    public boolean hasTeamEquals() {
        return this.hasTeamEquals;
    }

    public void setHasTeamEquals(final boolean pHasTeamEquals) {
        this.hasTeamEquals = pHasTeamEquals;
    }

    public boolean hasTeamNotEquals() {
        return this.hasTeamNotEquals;
    }

    public void setHasTeamNotEquals(final boolean pHasTeamNotEquals) {
        this.hasTeamNotEquals = pHasTeamNotEquals;
    }

    public void setTypeLimitedInversely() {
        this.typeInverse = true;
    }

    public boolean isTypeLimitedInversely() {
        return this.typeInverse;
    }

    public boolean hasScores() {
        return this.hasScores;
    }

    public void setHasScores(final boolean pHasScores) {
        this.hasScores = pHasScores;
    }

    public boolean hasAdvancements() {
        return this.hasAdvancements;
    }

    public void setHasAdvancements(final boolean pHasAdvancements) {
        this.hasAdvancements = pHasAdvancements;
    }

    protected void fillSelectorSuggestions(final SuggestionsBuilder builder) {
        builder.suggest("@p", new LiteralMessage("Nearest player"));
        builder.suggest("@r", new LiteralMessage("Random player"));
        builder.suggest("@s", new LiteralMessage("Current entity"));

        if (!this.single) {
            builder.suggest("@a", new LiteralMessage("All players"));

            if (!this.playersOnly) {
                builder.suggest("@e", new LiteralMessage("All entities"));
            }
        }

        this.fillSelectorSuggestionsExtra(builder);
    }

    protected void fillSelectorSuggestionsExtra(final SuggestionsBuilder builder) {}

    protected void parseNameOrUUID() throws CommandSyntaxException {
        if (this.reader.canRead()) {
            this.suggestions = this::suggestName;
        }

        final int i = this.reader.getCursor();
        final String s = this.reader.readString();

        try {
            this.entityUniqueId = UUID.fromString(s);
            this.includesEntities = true;
        } catch (final IllegalArgumentException illegalargumentexception) {
            if (s.isEmpty() || s.length() > 16) {
                this.reader.setCursor(i);
                throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
            }

            this.includesEntities = false;
            this.playerName = s;
        }

        this.maxResults = 1;
    }

    protected CompletableFuture<Suggestions> suggestNameOrSelector(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> consumer) {
        consumer.accept(builder);

        if (this.allowSelectors) {
            this.fillSelectorSuggestions(builder);
        }

        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestName(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> consumer) {
        final SuggestionsBuilder suggestionsbuilder = builder.createOffset(this.startPosition);
        consumer.accept(suggestionsbuilder);
        return builder.add(suggestionsbuilder).buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestSelector(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> ignored) {
        final SuggestionsBuilder suggestionsbuilder = builder.createOffset(builder.getStart() - 1);

        this.fillSelectorSuggestions(suggestionsbuilder);
        builder.add(suggestionsbuilder);

        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestOpenOptions(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> ignored) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_START));
        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestOptionsKeyOrClose(
            final SuggestionsBuilder builder, final Consumer<SuggestionsBuilder> ignored) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_END));
        this.getEntitySelectorOptions().suggestNames(this.getThis(), builder);
        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestOptionsKey(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> ignored) {
        this.getEntitySelectorOptions().suggestNames(this.getThis(), builder);
        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestOptionsNextOrClose(
            final SuggestionsBuilder builder, final Consumer<SuggestionsBuilder> ignored) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_SEPARATOR));
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_END));
        return builder.buildFuture();
    }

    protected CompletableFuture<Suggestions> suggestEquals(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> ignored) {
        builder.suggest(String.valueOf(SYNTAX_OPTIONS_KEY_VALUE_SEPARATOR));
        return builder.buildFuture();
    }
}
