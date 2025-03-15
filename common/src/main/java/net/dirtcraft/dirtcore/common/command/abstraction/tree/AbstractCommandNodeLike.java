/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.tree;

import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.Permission;

public abstract class AbstractCommandNodeLike<S extends Sender> implements CommandNodeLike<S> {

    protected final Predicate<S> requirement;
    protected final Permission requiredPermission;

    protected AbstractCommandNodeLike(final Predicate<S> requirement,
            final Permission requiredPermission) {
        this.requirement = requirement;
        this.requiredPermission = requiredPermission;
    }

    @Override
    public boolean canUse(final S source) {
        return this.requirement.test(source) && source.hasPermission(this.requiredPermission);
    }
}
