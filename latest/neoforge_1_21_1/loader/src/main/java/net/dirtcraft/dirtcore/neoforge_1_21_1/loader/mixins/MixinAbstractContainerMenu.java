/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.loader.mixins;

import com.google.common.collect.ImmutableList;
import net.dirtcraft.dirtcore.neoforge_1_21_1.loader.NeoForgeLoaderPlugin;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @Inject(at = @At("HEAD"), method = "doClick", cancellable = true)
    public void doClick(final int pSlotId, final int pButton, final ClickType pClickType,
            final Player pPlayer, final CallbackInfo ci) {
        if (pSlotId == -999 || pSlotId == -1) {
            return;
        }

        final ImmutableList.Builder<ItemStack> builder = ImmutableList.<ItemStack>builder()
                .add(this.slots.get(pSlotId).getItem());

        if (pClickType == ClickType.SWAP) {
            builder.add(pPlayer.getInventory().getItem(pButton));
        }

        NeoForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchPlayerInventoryClick(pPlayer.getUUID(),
                    pPlayer.getName().getString(), builder.build())) {
                ci.cancel();
            }
        });
    }
}
