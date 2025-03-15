/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.builder;

import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.util.Permission;

public abstract class AbstractArgumentBuilderLike<S extends Sender,
        T extends ArgumentBuilderLike<S>> implements ArgumentBuilderLike<S> {

    protected Predicate<S> requirement = s -> true;
    protected Permission requiredPermission = Permission.NONE;

    protected abstract T getThis();

    public T requires(final Predicate<S> requirement) {
        this.requirement = requirement;
        return this.getThis();
    }

    @Override
    public Predicate<S> getRequirement() {
        return this.requirement;
    }

    @Override
    public Permission getRequiredPermission() {
        return this.requiredPermission;
    }

    public T requiresPermission(final Permission permission) {
        this.requiredPermission = permission;
        return this.getThis();
    }
}
