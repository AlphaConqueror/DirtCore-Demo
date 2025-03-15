/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item;

import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface ItemResult extends ItemLike, ItemInfoProvider {

    void setPersistentDataAsString(@Nullable String s) throws CommandSyntaxException;
}
