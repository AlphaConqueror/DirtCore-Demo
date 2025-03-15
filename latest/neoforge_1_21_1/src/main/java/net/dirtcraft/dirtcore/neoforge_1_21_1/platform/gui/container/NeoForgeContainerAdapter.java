/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.container;

import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.context.SlotContext;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot.NeoForgeNOPContainer;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot.NeoForgeNOPSlot;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot.NeoForgeSlotDelegate;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.gui.slot.NeoForgeSlotFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class NeoForgeContainerAdapter extends AbstractContainerMenu {

    @NonNull
    private final DirtCoreNeoForgePlugin plugin;
    @NonNull
    private final Container container;
    @NonNull
    private final NeoForgeNOPContainer inventory;
    @NonNull
    private final NeoForgeSlotFactory factory;
    @NonNull
    private final Inventory playerInventory;

    public NeoForgeContainerAdapter(@NonNull final DirtCoreNeoForgePlugin plugin,
            @NonNull final Container container, final int containerId,
            @NonNull final Inventory playerInventory) {
        super(parseMenuType(container.getType()), containerId);

        this.plugin = plugin;
        this.container = container;
        this.inventory = new NeoForgeNOPContainer(container.getType().size());
        this.factory = new NeoForgeSlotFactory(this.plugin, this.inventory);
        this.playerInventory = playerInventory;
    }

    private static MenuType<ChestMenu> parseMenuType(final Container.@NonNull Type type) {
        return switch (type) {
            case SIZE_9x1 -> MenuType.GENERIC_9x1;
            case SIZE_9x2 -> MenuType.GENERIC_9x2;
            case SIZE_9x3 -> MenuType.GENERIC_9x3;
            case SIZE_9x4 -> MenuType.GENERIC_9x4;
            case SIZE_9x5 -> MenuType.GENERIC_9x5;
            case SIZE_9x6 -> MenuType.GENERIC_9x6;
        };
    }

    public void init() {
        // TODO: Maybe make changes to the context
        this.container.init(this.factory);
        this.container.getSlots().forEach((index, slot) -> this.container.updateSlot(slot));

        for (int y = 0; y < this.container.y(); y++) {
            for (int x = 0; x < this.container.x(); x++) {
                this.addSlot(new NeoForgeSlotDelegate(this.inventory, x + y * 9, 8 + x * 18,
                        18 + y * 18));
            }
        }

        final int i = (this.container.y() - 4) * 18;

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new NeoForgeNOPSlot(this.playerInventory, x + y * 9 + 9, 8 + x * 18,
                        103 + y * 18 + i));
            }
        }

        for (int x = 0; x < 9; x++) {
            this.addSlot(new NeoForgeNOPSlot(this.playerInventory, x, 8 + x * 18, 161 + i));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(final @NotNull Player pPlayer, final int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public void clicked(final int pSlotId, final int pButton, final @NotNull ClickType pClickType,
            final @NotNull Player pPlayer) {
        this.container.slotAt(pSlotId).ifPresent(slot -> {
            final SlotContext.Builder builder = SlotContext.builder(
                            this.plugin.getPlatformFactory().wrapPlayer((ServerPlayer) pPlayer))
                    .withClickType(this.parseClickType(pClickType));

            if (slot.getTaskContextRequirement()) {
                this.plugin.getStorage().performTask(context -> slot.click(this.factory,
                        builder.withTaskContext(context).build()));
            } else {
                slot.click(this.factory, builder.build());
            }
        });
    }

    @Override
    public void removed(final @NotNull Player pPlayer) {
        super.removed(pPlayer);
        this.container.onClose();
    }

    @Override
    public boolean stillValid(final @NotNull Player pPlayer) {
        return true;
    }

    private Slot.@NonNull ClickType parseClickType(@NotNull final ClickType ignored) {
        return Slot.ClickType.UNKNOWN;
    }
}
