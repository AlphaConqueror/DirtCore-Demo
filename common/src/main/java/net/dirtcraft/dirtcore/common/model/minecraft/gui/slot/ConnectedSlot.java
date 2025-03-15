/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot;

import java.util.function.Consumer;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConnectedSlot extends ChangingSlot {

    @NonNull
    private final ConnectedSlotHandler connectedSlotHandler;

    protected ConnectedSlot(final int slotId,
            @NonNull final Function<@NonNull SlotContext, @NonNull ItemLike> itemLikeFunction,
            @NonNull final Consumer<@NonNull SlotContext> contextConsumer,
            @NonNull final ConnectedSlotHandler connectedSlotHandler) {
        super(slotId, itemLikeFunction, contextConsumer);
        this.connectedSlotHandler = connectedSlotHandler;
        // add this instance to the slot handler
        this.connectedSlotHandler.addSlot(this);
    }

    @Override
    public void onClick(@NonNull final SlotFactory factory, @NonNull final SlotContext context) {
        super.onClick(factory, context);
        this.connectedSlotHandler.updateOthers(factory, context, this);
        this.connectedSlotHandler.onPostClick(context);
    }

    @Override
    protected ConnectedSlot getThis() {
        return this;
    }
}
