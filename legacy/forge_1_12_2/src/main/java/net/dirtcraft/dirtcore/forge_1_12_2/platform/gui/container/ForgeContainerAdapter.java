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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.container;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot.ForgeNOPContainer;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot.ForgeNOPSlot;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot.ForgeSlotDelegate;
import net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot.ForgeSlotFactory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class ForgeContainerAdapter extends net.minecraft.inventory.Container {

    @NonNull
    private final DirtCoreForgePlugin plugin;
    @NonNull
    private final Container container;
    @NonNull
    private final ForgeNOPContainer inventory;
    @NonNull
    private final ForgeSlotFactory factory;
    @NonNull
    private final InventoryPlayer playerInventory;

    public ForgeContainerAdapter(@NonNull final DirtCoreForgePlugin plugin,
            @NonNull final Container container, @NonNull final InventoryPlayer playerInventory) {
        this.plugin = plugin;
        this.container = container;
        this.inventory = new ForgeNOPContainer(
                plugin.getPlatformFactory().componentToJson(container.getTitle()), true,
                container.getType().size());
        this.factory = new ForgeSlotFactory(this.plugin, this.inventory);
        this.playerInventory = playerInventory;
    }

    public void init() {
        this.container.init(this.factory);
        this.container.getSlots().forEach((index, slot) -> this.container.updateSlot(slot));

        for (int y = 0; y < this.container.y(); y++) {
            for (int x = 0; x < this.container.x(); x++) {
                this.addSlotToContainer(
                        new ForgeSlotDelegate(this.inventory, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }

        final int i = (this.container.y() - 4) * 18;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlotToContainer(
                        new ForgeNOPSlot(this.playerInventory, x + y * 9 + 9, 8 + x * 18,
                                103 + y * 18 + i));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlotToContainer(new ForgeNOPSlot(this.playerInventory, x, 8 + x * 18, 161 + i));
        }
    }

    @Override
    public @NonNull ItemStack transferStackInSlot(@NotNull final EntityPlayer player,
            final int index) {
        this.onClick(index, Slot.ClickType.UNKNOWN, player);
        return ItemStack.EMPTY;
    }

    @NotNull
    @Override
    public ItemStack slotClick(final int slotId, final int dragType,
            @NotNull final ClickType clickTypeIn, @NotNull final EntityPlayer player) {
        this.onClick(slotId, this.parseClickType(dragType), player);
        return ItemStack.EMPTY;
    }

    @Override
    public void onContainerClosed(@NonNull final EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        this.container.onClose();
    }

    @Override
    public boolean canInteractWith(@NonNull final EntityPlayer player) {
        return true;
    }

    @NonNull
    public ForgeNOPContainer getContainer() {
        return this.inventory;
    }

    private void onClick(final int slotId, final Slot.@NonNull ClickType clickType,
            @NotNull final EntityPlayer player) {
        this.container.slotAt(slotId).ifPresent(slot -> {
            final SlotContext.Builder builder = SlotContext.builder(
                            this.plugin.getPlatformFactory().wrapPlayer((EntityPlayerMP) player))
                    .withClickType(clickType);

            if (slot.getTaskContextRequirement()) {
                this.plugin.getStorage().performTask(context -> slot.click(this.factory,
                        builder.withTaskContext(context).build()));
            } else {
                slot.click(this.factory, builder.build());
            }
        });
    }

    private Slot.@NonNull ClickType parseClickType(final int ignored) {
        return Slot.ClickType.UNKNOWN;
    }
}
