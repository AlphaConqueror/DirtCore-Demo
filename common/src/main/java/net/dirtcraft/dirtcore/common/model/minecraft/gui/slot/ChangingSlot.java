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

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot;

import java.util.function.Consumer;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.Sound;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.AbstractSlot;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChangingSlot extends AbstractSlot<ChangingSlot> {

    @NonNull
    protected final Consumer<@NonNull SlotContext> contextConsumer;

    protected ChangingSlot(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction,
            @NonNull final Consumer<@NonNull SlotContext> contextConsumer) {
        super(slotId, itemLikeFunction);
        this.contextConsumer = contextConsumer;
    }

    @Override
    public void onClick(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        this.contextConsumer.accept(context);
        this.update(factory, context);

        final TaskContext taskContext = context.getTaskContext();
        final Runnable runnable = () -> context.getPlayer().playSound(Sound.GUI_SUCCESS);

        if (taskContext == null) {
            runnable.run();
        } else {
            taskContext.queue(runnable);
        }
    }

    @Override
    protected ChangingSlot getThis() {
        return this;
    }
}
