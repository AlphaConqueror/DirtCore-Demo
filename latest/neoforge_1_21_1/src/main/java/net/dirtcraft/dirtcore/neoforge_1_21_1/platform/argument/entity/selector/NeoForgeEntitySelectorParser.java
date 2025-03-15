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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.selector;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelector;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.minecraft.critereon.WrappedMinMaxBounds;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.AABB;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NeoForgeEntitySelectorParser extends AbstractEntitySelectorParser<DirtCoreNeoForgePlugin, NeoForgeEntitySelector, NeoForgeEntitySelectorParser> {

    private final DirtCoreNeoForgePlugin plugin;
    @Nullable
    private EntityType<?> type;

    public NeoForgeEntitySelectorParser(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader, final boolean single, final boolean playersOnly) {
        super(plugin, reader, single, playersOnly);
        this.plugin = plugin;
    }

    public NeoForgeEntitySelectorParser(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader, final boolean allowSelectors, final boolean single,
            final boolean playersOnly) {
        super(plugin, reader, allowSelectors, single, playersOnly);
        this.plugin = plugin;
    }

    public void addPlatformPredicate(final Predicate<Entity> predicate) {
        this.predicate = this.predicate.and(this.toPlatformPredicate(predicate));
    }

    @Override
    public AbstractEntitySelector parse() throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.suggestions = this::suggestNameOrSelector;

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_SELECTOR_START) {
            if (!this.allowSelectors) {
                throw ERROR_SELECTORS_NOT_ALLOWED.createWithContext(this.reader);
            }

            this.reader.skip();
            this.parseSelector();
        } else {
            this.parseNameOrUUID();
        }

        this.finalizePredicates();
        return this.getSelector();
    }

    @Override
    public boolean isTypeLimited() {
        return this.type != null;
    }

    @Override
    protected @NonNull NeoForgeEntitySelectorOptions getEntitySelectorOptions() {
        return this.plugin.getEntitySelectorOptions();
    }

    @Override
    protected @NonNull NeoForgeEntitySelectorParser getThis() {
        return this;
    }

    public void limitToType(final EntityType<?> pType) {
        this.type = pType;
    }

    @NonNull
    private NeoForgeEntitySelector getSelector() {
        final AABB aabb;

        if (this.deltaX == null && this.deltaY == null && this.deltaZ == null) {
            if (this.distance.getMax() != null) {
                final double d0 = this.distance.getMax();
                aabb = new AABB(-d0, -d0, -d0, d0 + 1.0D, d0 + 1.0D, d0 + 1.0D);
            } else {
                aabb = null;
            }
        } else {
            aabb = this.createAabb(this.deltaX == null ? 0.0D : this.deltaX,
                    this.deltaY == null ? 0.0D : this.deltaY,
                    this.deltaZ == null ? 0.0D : this.deltaZ);
        }

        final Function<Vec3, Vec3> function;

        if (this.x == null && this.y == null && this.z == null) {
            function = vec3 -> vec3;
        } else {
            function = vec3 -> Vec3.from(this.x == null ? vec3.x : this.x,
                    this.y == null ? vec3.y : this.y, this.z == null ? vec3.z : this.z);
        }

        return new NeoForgeEntitySelector(this.plugin, this.maxResults, this.includesEntities,
                this.worldLimited, this.predicate, this.distance, function, aabb, this.order,
                this.currentEntity, this.playerName, this.entityUniqueId, this.type,
                this.usesSelectors);
    }

    private AABB createAabb(final double pSizeX, final double pSizeY, final double pSizeZ) {
        final boolean flag = pSizeX < 0.0D;
        final boolean flag1 = pSizeY < 0.0D;
        final boolean flag2 = pSizeZ < 0.0D;
        final double d0 = flag ? pSizeX : 0.0D;
        final double d1 = flag1 ? pSizeY : 0.0D;
        final double d2 = flag2 ? pSizeZ : 0.0D;
        final double d3 = (flag ? 0.0D : pSizeX) + 1.0D;
        final double d4 = (flag1 ? 0.0D : pSizeY) + 1.0D;
        final double d5 = (flag2 ? 0.0D : pSizeZ) + 1.0D;

        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    @NonNull
    private Predicate<net.dirtcraft.dirtcore.common.model.minecraft.Entity> toPlatformPredicate(
            final Predicate<Entity> predicate) {
        return entity -> predicate.test(this.plugin.getPlatformFactory().transformEntity(entity));
    }

    private void parseSelector() throws CommandSyntaxException {
        this.usesSelectors = true;
        this.suggestions = this::suggestSelector;

        if (!this.reader.canRead()) {
            throw ERROR_MISSING_SELECTOR_TYPE.createWithContext(this.reader);
        }

        final int i = this.reader.getCursor();
        final char c0 = this.reader.read();

        if (c0 == SELECTOR_NEAREST_PLAYER) {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_NEAREST;
            this.limitToType(EntityType.PLAYER);
        } else if (c0 == SELECTOR_ALL_PLAYERS) {
            this.maxResults = AbstractEntitySelector.INFINITE;
            this.includesEntities = false;
            this.order = AbstractEntitySelector.ORDER_ARBITRARY;
            this.limitToType(EntityType.PLAYER);
        } else if (c0 == SELECTOR_RANDOM_PLAYERS) {
            this.maxResults = 1;
            this.includesEntities = false;
            this.order = ORDER_RANDOM;
            this.limitToType(EntityType.PLAYER);
        } else if (c0 == SELECTOR_CURRENT_ENTITY) {
            this.maxResults = 1;
            this.includesEntities = true;
            this.currentEntity = true;
        } else {
            if (c0 != SELECTOR_ALL_ENTITIES) {
                this.reader.setCursor(i);
                throw ERROR_UNKNOWN_SELECTOR_TYPE.createWithContext(this.reader,
                        SYNTAX_SELECTOR_START + c0);
            }

            this.maxResults = AbstractEntitySelector.INFINITE;
            this.includesEntities = true;
            this.order = AbstractEntitySelector.ORDER_ARBITRARY;
            this.predicate = this.toPlatformPredicate(Entity::isAlive);
        }

        this.suggestions = this::suggestOpenOptions;

        if (this.reader.canRead() && this.reader.peek() == SYNTAX_OPTIONS_START) {
            this.reader.skip();
            this.suggestions = this::suggestOptionsKeyOrClose;
            this.parseOptions();
        }
    }

    private void finalizePredicates() {
        if (this.rotX != WrappedMinMaxBounds.ANY) {
            this.addPlatformPredicate(this.createRotationPredicate(this.rotX, Entity::getXRot));
        }

        if (this.rotY != WrappedMinMaxBounds.ANY) {
            this.addPlatformPredicate(this.createRotationPredicate(this.rotY, Entity::getYRot));
        }

        if (!this.level.isAny()) {
            this.addPlatformPredicate(entity -> entity instanceof Player && this.level.matches(
                    ((Player) entity).experienceLevel));
        }
    }

    @NonNull
    private Predicate<Entity> createRotationPredicate(
            @NonNull final WrappedMinMaxBounds angleBounds,
            @NonNull final ToDoubleFunction<Entity> angleFunction) {
        final double d0 =
                Mth.wrapDegrees(angleBounds.getMin() == null ? 0.0F : angleBounds.getMin());
        final double d1 =
                Mth.wrapDegrees(angleBounds.getMax() == null ? 359.0F : angleBounds.getMax());

        return entity -> {
            final double d2 = Mth.wrapDegrees(angleFunction.applyAsDouble(entity));

            if (d0 > d1) {
                return d2 >= d0 || d2 <= d1;
            }

            return d2 >= d0 && d2 <= d1;
        };
    }
}
