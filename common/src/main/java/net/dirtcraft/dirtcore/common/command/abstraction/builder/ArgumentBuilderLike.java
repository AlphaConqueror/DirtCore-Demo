/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.builder;

import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.util.Permission;

public interface ArgumentBuilderLike<S> {

    Predicate<S> getRequirement();

    Permission getRequiredPermission();
}
