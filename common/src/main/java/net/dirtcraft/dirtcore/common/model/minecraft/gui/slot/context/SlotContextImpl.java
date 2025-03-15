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

package net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context;

import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.user.UserSettingsEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SlotContextImpl implements SlotContext {

    @NonNull
    private final Player player;
    private final Slot.@Nullable ClickType clickType;
    @Nullable
    private TaskContext context;
    @Nullable
    private UserSettingsEntity userSettings;

    protected SlotContextImpl(@NonNull final Player player,
            final Slot.@Nullable ClickType clickType, @Nullable final TaskContext context,
            @Nullable final UserSettingsEntity userSettings) {
        this.player = player;
        this.clickType = clickType;
        this.context = context;
        this.userSettings = userSettings;
    }

    @Override
    public @NonNull Player getPlayer() {
        return this.player;
    }

    @Override
    public Slot.@Nullable ClickType getClickType() {
        return this.clickType;
    }

    @Override
    public @Nullable TaskContext getTaskContext() {
        return this.context;
    }

    @Override
    public void setTaskContext(@NonNull final TaskContext context) {
        this.context = context;
    }

    @Override
    public @NonNull TaskContext getTaskContextOrException() throws AssertionError {
        if (this.context == null) {
            throw new AssertionError();
        }

        return this.context;
    }

    @Override
    public @NonNull UserSettingsEntity getOrCreateUserSettings() {
        if (this.userSettings == null) {
            this.userSettings = this.getTaskContextOrException().session()
                    .get(UserSettingsEntity.class, this.player.getUniqueId().toString());
        }

        return this.userSettings;
    }

    @Override
    public @NonNull UserSettingsEntity getUserSettingsOrException() throws AssertionError {
        if (this.userSettings == null) {
            throw new AssertionError();
        }

        return this.userSettings;
    }

    @Override
    public void setUserSettings(@NonNull final UserSettingsEntity userSettings) {
        this.userSettings = userSettings;
    }

    public static class BuilderImpl implements Builder {

        @NonNull
        private final Player player;
        private Slot.@Nullable ClickType clickType;
        @Nullable
        private TaskContext context;
        @Nullable
        private UserSettingsEntity userSettings;

        protected BuilderImpl(@NonNull final Player player) {
            this.player = player;
        }

        @Override
        public @NonNull Builder withClickType(final Slot.@NonNull ClickType clickType) {
            this.clickType = clickType;
            return this;
        }

        @Override
        public @NonNull Builder withTaskContext(@NonNull final TaskContext context) {
            this.context = context;
            return this;
        }

        @Override
        public @NonNull Builder withUserSettings(@NonNull final UserSettingsEntity userSettings) {
            this.userSettings = userSettings;
            return this;
        }

        @Override
        public @NonNull SlotContext build() {
            return new SlotContextImpl(this.player, this.clickType, this.context,
                    this.userSettings);
        }
    }
}
