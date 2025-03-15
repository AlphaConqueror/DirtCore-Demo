/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
