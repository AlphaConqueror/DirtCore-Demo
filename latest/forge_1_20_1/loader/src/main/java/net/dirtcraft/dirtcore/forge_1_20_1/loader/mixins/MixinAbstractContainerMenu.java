/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.loader.mixins;

import com.google.common.collect.ImmutableList;
import net.dirtcraft.dirtcore.forge_1_20_1.loader.ForgeLoaderPlugin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class MixinAbstractContainerMenu {

    @Inject(at = @At("HEAD"), method = "doClick", cancellable = true)
    public void doClick(final int pSlotId, final int pButton, final ClickType pClickType,
            final Player pPlayer, final CallbackInfo ci) {
        if (pSlotId == -999 || pSlotId == -1) {
            return;
        }

        //noinspection DataFlowIssue
        final AbstractContainerMenu self = (AbstractContainerMenu) (Object) this;
        final ImmutableList.Builder<ItemStack> builder = ImmutableList.<ItemStack>builder()
                .add(self.slots.get(pSlotId).getItem());

        if (pClickType == ClickType.SWAP) {
            builder.add(pPlayer.getInventory().getItem(pButton));
        }

        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchPlayerInventoryClick(pPlayer.getUUID(),
                    pPlayer.getName().getString(), builder.build())) {
                ci.cancel();
            }
        });
    }
}
