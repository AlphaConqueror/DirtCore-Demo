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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConnectedSlotHandler {

    @NonNull
    private final Set<ConnectedSlot> slots = new HashSet<>();
    @NonNull
    private final Consumer<SlotContext> postClickConsumer;

    public ConnectedSlotHandler(@NonNull final Consumer<SlotContext> postClickConsumer) {
        this.postClickConsumer = postClickConsumer;
    }

    @NonNull
    public Set<ConnectedSlot> getSlots() {
        return this.slots;
    }

    public void addSlot(@NonNull final ConnectedSlot slot) {
        this.slots.add(slot);
    }

    public void updateOthers(@NonNull final SlotFactory factory, @NonNull final SlotContext context,
            @NonNull final ConnectedSlot slot) {
        this.slots.stream().filter(s -> !s.equals(slot))
                .forEach(connectedSlot -> connectedSlot.update(factory, context));
    }

    public void onPostClick(@NonNull final SlotContext context) {
        this.postClickConsumer.accept(context);
    }
}
