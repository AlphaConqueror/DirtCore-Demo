/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.dirtcraft.dirtcore.forge_1_12_2.loader.ForgeLoaderPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public abstract class MixinContainer {

    @Inject(at = @At("HEAD"), method = "slotClick", cancellable = true)
    public void doClick(final int slotId, final int dragType, final ClickType clickTypeIn,
            final EntityPlayer player, final CallbackInfoReturnable<ItemStack> cir) {
        if (slotId == -999 || slotId == -1) {
            return;
        }

        //noinspection DataFlowIssue
        final Container self = (Container) (Object) this;
        final List<Slot> slots = self.inventorySlots;
        final Slot slot = slots.get(slotId);
        final ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        boolean sendUpdate = false;

        if (slot != null) {
            builder.add(slot.getStack());

            // hotbar click, button must be hotbar slot
            if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9) {
                builder.add(player.inventory.getStackInSlot(dragType));
                sendUpdate = true;
            }
        }

        final boolean finalSendUpdate = sendUpdate;

        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchPlayerInventoryClick(player.getUniqueID(), player.getName(),
                    builder.build())) {
                // fix inventory de-sync
                if (finalSendUpdate) {
                    for (final IContainerListener crafter :
                            ((MixinContainerAccessor) this).getListeners()) {
                        crafter.sendAllContents(self, self.getInventory());
                    }
                }

                cir.setReturnValue(ItemStack.EMPTY);
            }
        });
    }
}
