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

import java.util.Map;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.SlotFactory;
import net.kyori.adventure.text.Component;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Wrapper interface to represent a container within the common container implementations.
 */
public interface Container {

    /**
     * The default row size.
     */
    int ROW_SIZE = 9;

    /**
     * Gets the title.
     *
     * @return the title
     */
    @NonNull Component getTitle();

    /**
     * Gets the type.
     *
     * @return the type
     */
    @NonNull Type getType();

    /**
     * Gets the player.
     *
     * @return the player
     */
    @NonNull Player getPlayer();

    /**
     * Inits the GUI.
     *
     * @param factory the factory
     */
    void init(@NonNull SlotFactory factory);

    /**
     * Gets the slots.
     *
     * @return the slots
     */
    @NonNull Map<Integer, Slot<?>> getSlots();

    /**
     * Gets the slot at index, if present.
     *
     * @param index the index
     * @return the optional slot
     */
    @NonNull Optional<Slot<?>> slotAt(int index);

    /**
     * Sets a slot.
     *
     * @param slot the slot
     */
    void setSlot(@NonNull Slot<?> slot);

    /**
     * Updates a slot.
     * Calls {@link Slot#update(SlotFactory, SlotContext)} upon update.
     *
     * @param slot the slot
     */
    void updateSlot(@NonNull Slot<?> slot);

    /**
     * Called upon close of the container.
     */
    default void onClose() {}

    /**
     * Gets the horizontal size of the container.
     *
     * @return the horizontal size
     */
    default int x() {
        return this.getType().getX();
    }

    /**
     * Gets the vertical size of the container.
     *
     * @return the vertical size
     */
    default int y() {
        return this.getType().getY();
    }

    /**
     * The enum Type.
     */
    enum Type {
        /**
         * Width 9, height 1.
         */
        SIZE_9x1(1),
        /**
         * Width 9, height 2.
         */
        SIZE_9x2(2),
        /**
         * Width 9, height 3.
         */
        SIZE_9x3(3),
        /**
         * Width 9, height 4.
         */
        SIZE_9x4(4),
        /**
         * Width 9, height 5.
         */
        SIZE_9x5(5),
        /**
         * Width 9, height 6.
         */
        SIZE_9x6(6);

        /**
         * The height.
         */
        final int y;

        Type(final int y) {
            this.y = y;
        }

        /**
         * Gets the type by needed size.
         *
         * @param slots the amount of slots
         * @return the type
         * @throws IllegalArgumentException if the amount of slots is too big
         */
        @NonNull
        public static Type getTypeByNeededSize(final int slots) {
            // types are sorted by size by default
            for (final Type type : values()) {
                if (slots <= type.size()) {
                    return type;
                }
            }

            throw new IllegalArgumentException("Too many slots.");
        }

        /**
         * Gets the width.
         *
         * @return the width
         */
        public int getX() {
            return ROW_SIZE;
        }

        /**
         * Gets the height.
         *
         * @return the height
         */
        public int getY() {
            return this.y;
        }

        /**
         * Gets the total size.
         *
         * @return the size
         */
        public int size() {
            return this.getX() * this.getY();
        }
    }
}
