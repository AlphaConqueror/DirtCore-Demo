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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.gui.slot;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeSlotFactory implements SlotFactory {

    @NonNull
    private final DirtCoreForgePlugin plugin;
    @NonNull
    private final ForgeNOPContainer inventory;

    public ForgeSlotFactory(@NonNull final DirtCoreForgePlugin plugin,
            @NonNull final ForgeNOPContainer inventory) {
        this.plugin = plugin;
        this.inventory = inventory;
    }

    @Override
    public void updateSlot(@NonNull final SlotContext context, @NonNull final Slot<?> slot) {
        this.inventory.updateStack(slot.getSlotId(), this.plugin.getPlatformFactory()
                .transformItemStack(slot.getItemStack(this.plugin, context)));
    }
}
