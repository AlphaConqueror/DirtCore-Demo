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

package net.dirtcraft.dirtcore.common.model.minecraft.gui.container;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.item.Material;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

/**
 * Simple implementation of {@link Container}.
 */
public abstract class AbstractContainer implements Container {

    /**
     * Represents an empty slot.
     */
    protected static final Function<@NonNull SlotContext, @NonNull ItemLike> EMPTY =
            context -> ItemStack.builder(Material.MENU_EMPTY).displayName(Component.empty())
                    .build();

    @NonNull
    protected final DirtCorePlugin plugin;
    @NonNull
    protected final Component title;
    @NonNull
    protected final Type type;
    @NonNull
    protected final Player player;
    @NonNull
    protected final Map<Integer, Slot<?>> slots = new HashMap<>();
    @Nullable
    protected SlotFactory factory;
    private boolean isInitComplete = false;

    public AbstractContainer(@NonNull final DirtCorePlugin plugin, @NonNull final Component title,
            @NonNull final Type type, @NonNull final Player player) {
        this.plugin = plugin;
        this.title = title;
        this.type = type;
        this.player = player;
    }

    protected abstract void onInit();

    @Override
    public @NonNull Component getTitle() {
        return this.title;
    }

    @Override
    public @NonNull Type getType() {
        return this.type;
    }

    @Override
    public @NonNull Player getPlayer() {
        return this.player;
    }

    @Override
    public void init(@NonNull final SlotFactory factory) {
        this.factory = factory;
        this.onInit();
        this.isInitComplete = true;
    }

    @NotNull
    @Override
    public Map<Integer, Slot<?>> getSlots() {
        return this.slots;
    }

    @Override
    public @NonNull Optional<Slot<?>> slotAt(final int index) {
        return Optional.ofNullable(this.slots.get(index));
    }

    @Override
    public void setSlot(@NonNull final Slot<?> slot) {
        this.slots.put(slot.getSlotId(), slot);

        if (this.isInitComplete) {
            this.updateSlot(slot);
        }
    }

    @Override
    public void updateSlot(@NonNull final Slot<?> slot) {
        if (this.factory == null) {
            throw new UnsupportedOperationException("no factory");
        }

        final SlotContext.Builder builder = SlotContext.builder(this.player);

        if (slot.getTaskContextRequirement()) {
            this.plugin.getStorage().performTask(
                    context -> slot.update(this.factory, builder.withTaskContext(context).build()));
        } else {
            slot.update(this.factory, builder.build());
        }
    }

    @Override
    public int x() {
        return this.type.getX();
    }

    @Override
    public int y() {
        return this.type.getY();
    }

    /**
     * Fills all unset slots with an item stack representing an empty slot.
     */
    protected void fillEmpty() {
        this.fillEmpty(0, this.type.size());
    }

    /**
     * Fills slots with empty items, starting with fromIndex, for amount.
     * If amount is zero, nothing happens.
     *
     * @param fromIndex the first slot id
     * @param amount    the amount
     * @throws IndexOutOfBoundsException for an illegal endpoint index value
     *                                   ({@code fromIndex < 0 || amount < 0 ||
     *                                   (fromIndex + amount) > grid size})
     */
    protected void fillEmpty(final int fromIndex, final int amount) {
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
        }

        if (amount < 0) {
            throw new IndexOutOfBoundsException("amount = " + amount);
        }

        final int end = fromIndex + amount;
        final int gridSize = this.type.size();

        if (end > gridSize) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") + amount(" + amount + ") > grid size(" + gridSize
                            + ")");
        }

        for (int i = fromIndex; i < end; i++) {
            if (!this.slotAt(i).isPresent()) {
                this.setEmpty(i);
            }
        }
    }

    /**
     * Fills the slot with an item stack representing an empty slot.
     *
     * @param slotId the slot id
     */
    protected void setEmpty(final int slotId) {
        this.setSlot(Slot.of(slotId, EMPTY));
    }
}
