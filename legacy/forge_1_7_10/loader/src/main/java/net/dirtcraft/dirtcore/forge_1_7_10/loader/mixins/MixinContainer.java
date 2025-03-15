/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader.mixins;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.dirtcraft.dirtcore.forge_1_7_10.loader.ForgeLoaderPlugin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public abstract class MixinContainer {

    @Inject(at = @At("HEAD"), method = "slotClick", cancellable = true)
    @SuppressWarnings("unchecked")
    public void doClick(final int slotId, final int clickedButton, final int mode,
            final EntityPlayer player, final CallbackInfoReturnable<ItemStack> cir) {
        if (slotId == -999 || slotId == -1) {
            return;
        }

        //noinspection DataFlowIssue
        final Container self = (Container) (Object) this;
        final List<Slot> slots = (List<Slot>) self.inventorySlots;
        final Slot slot = slots.get(slotId);
        final ImmutableList.Builder<ItemStack> builder = ImmutableList.builder();
        boolean sendUpdate = false;

        if (slot != null) {
            final ItemStack itemStack = slot.getStack();

            if (itemStack != null) {
                builder.add(itemStack);
            }

            // hotbar click, button must be hotbar slot
            if (mode == 2 && slot.canTakeStack(player) && clickedButton >= 0 && clickedButton < 9) {
                final ItemStack hotbarItemStack = player.inventory.getStackInSlot(clickedButton);

                if (hotbarItemStack != null) {
                    builder.add(hotbarItemStack);
                }

                sendUpdate = true;
            }
        }

        final boolean finalSendUpdate = sendUpdate;

        ForgeLoaderPlugin.getEventDispatcher().ifPresent(eventDispatcher -> {
            if (eventDispatcher.dispatchPlayerInventoryClick(player.getUniqueID(),
                    player.getCommandSenderName(), builder.build())) {
                // fix inventory de-sync
                if (finalSendUpdate) {
                    for (final ICrafting crafter :
                            (List<ICrafting>) ((MixinContainerAccessor) this).getCrafters()) {
                        crafter.updateCraftingInventory(self, self.getInventory());
                    }
                }

                cir.setReturnValue(null);
            }
        });
    }
}
