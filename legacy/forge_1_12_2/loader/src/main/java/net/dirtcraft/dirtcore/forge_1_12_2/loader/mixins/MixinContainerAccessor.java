/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import java.util.List;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Container.class)
public interface MixinContainerAccessor {

    @Accessor("listeners")
    List<IContainerListener> getListeners();
}
