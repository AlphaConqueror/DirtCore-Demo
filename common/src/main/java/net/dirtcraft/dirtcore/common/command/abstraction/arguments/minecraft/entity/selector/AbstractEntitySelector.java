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

import java.util.List;
import java.util.function.BiConsumer;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractEntitySelector {

    public static final int INFINITE = Integer.MAX_VALUE;
    public static final BiConsumer<Vec3, List<? extends Entity>> ORDER_ARBITRARY =
            (vec3, list) -> {};
    protected final int maxResults;
    protected final boolean includesEntities;
    protected final boolean worldLimited;
    protected final boolean currentEntity;
    protected final boolean usesSelector;

    public AbstractEntitySelector(final int maxResults, final boolean includesEntities,
            final boolean worldLimited, final boolean currentEntity, final boolean usesSelector) {
        this.maxResults = maxResults;
        this.includesEntities = includesEntities;
        this.worldLimited = worldLimited;
        this.currentEntity = currentEntity;
        this.usesSelector = usesSelector;
    }

    public static boolean canUseSelectors(@NonNull final Sender sender) {
        return sender.hasPermission(Permission.COMMAND_SELECTOR);
    }

    @NonNull
    public abstract Entity findSingleEntity(
            @NonNull final Sender sender) throws CommandSyntaxException;

    @NonNull
    public abstract List<? extends Entity> findEntities(
            @NonNull final Sender sender) throws CommandSyntaxException;

    @NonNull
    public abstract Player findSinglePlayer(
            @NonNull final Sender sender) throws CommandSyntaxException;

    @NonNull
    public abstract List<Player> findPlayers(
            @NonNull final Sender sender) throws CommandSyntaxException;

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean includesEntities() {
        return this.includesEntities;
    }

    public boolean isSelfSelector() {
        return this.currentEntity;
    }

    public boolean isWorldLimited() {
        return this.worldLimited;
    }

    public boolean usesSelector() {
        return this.usesSelector;
    }

    protected void checkPermissions(@NonNull final Sender sender) throws CommandSyntaxException {
        if (this.usesSelector && !canUseSelectors(sender)) {
            throw AbstractEntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }
}
