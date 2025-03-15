/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.gui.util;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.AbstractContainer;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.container.Container;
import net.dirtcraft.dirtcore.common.model.minecraft.gui.slot.Slot;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemLike;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.item.Material;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class PaginationGUI extends AbstractContainer {

    protected static final int ELEMENTS_PER_PAGE = 5 * Container.ROW_SIZE;
    protected static final int INDEX_PAGE_CURRENT = ELEMENTS_PER_PAGE + 4;
    protected static final int INDEX_PAGE_NEXT = INDEX_PAGE_CURRENT + 2;
    protected static final int INDEX_PAGE_PREVIOUS = INDEX_PAGE_CURRENT - 2;

    private static final BiFunction<Integer, Integer, ItemLike> PAGE_CURRENT =
            (currentPage, maxPage) -> ItemStack.builder(Material.PAGE_CURRENT).displayName(
                    Component.text()
                            .append(Component.text("Page: ", NamedTextColor.GOLD))
                            .append(Component.text(currentPage + 1, NamedTextColor.WHITE))
                            .append(Component.text('/', NamedTextColor.GRAY))
                            .append(Component.text(maxPage, NamedTextColor.WHITE)).build()).build();
    private static final Supplier<ItemLike> PAGE_NEXT = () -> ItemStack.builder(Material.PAGE_NEXT)
            .displayName(Component.text("Next Page >>>", NamedTextColor.GREEN)).build();
    private static final Supplier<ItemLike> PAGE_NEXT_NOT =
            () -> ItemStack.builder(Material.PAGE_NEXT).displayName(
                            Component.text("You are already on the last page.", NamedTextColor.RED))
                    .build();
    private static final Supplier<ItemLike> PAGE_PREVIOUS =
            () -> ItemStack.builder(Material.PAGE_PREVIOUS)
                    .displayName(Component.text("<<< Previous Page", NamedTextColor.GREEN)).build();
    private static final Supplier<ItemLike> PAGE_PREVIOUS_NOT =
            () -> ItemStack.builder(Material.PAGE_PREVIOUS).displayName(
                            Component.text("You are already on the first page.",
                                    NamedTextColor.RED))
                    .build();

    protected int currentPage = 0;
    protected int maxPage = 0;

    public PaginationGUI(@NonNull final DirtCorePlugin plugin, @NonNull final Component title,
            @NonNull final Player player) {
        super(plugin, title, Type.SIZE_9x6, player);
    }

    protected abstract int getCollectionSize();

    @NonNull
    protected abstract Slot<?> getSlot(int slotIndex, int elementIndex);

    @Override
    protected void onInit() {
        this.maxPage =
                (int) Math.max(1, Math.ceil((double) this.getCollectionSize() / ELEMENTS_PER_PAGE));
        this.renderPage();
    }

    protected void renderPage() {
        final int startingIndex = this.currentPage * ELEMENTS_PER_PAGE;

        for (int i = 0; i < ELEMENTS_PER_PAGE; i++) {
            final int elementIndex = startingIndex + i;

            if (elementIndex >= this.getCollectionSize()) {
                break;
            }

            this.setSlot(this.getSlot(i, elementIndex));
        }

        this.setSlot(Slot.ofChanging(INDEX_PAGE_PREVIOUS,
                        context -> 0 < this.currentPage ? PAGE_PREVIOUS.get() :
                                PAGE_PREVIOUS_NOT.get(),
                        context -> {
                            this.currentPage -= 1;
                            this.renderPage();
                        })
                .requires(context -> 0 < this.currentPage));
        this.setSlot(Slot.of(INDEX_PAGE_CURRENT,
                context -> PAGE_CURRENT.apply(this.currentPage, this.maxPage)));
        this.setSlot(Slot.ofChanging(INDEX_PAGE_NEXT,
                        context -> (this.currentPage + 1) < this.maxPage ? PAGE_NEXT.get()
                                : PAGE_NEXT_NOT.get(), context -> {
                            this.currentPage += 1;
                            this.renderPage();
                        })
                .requires(context -> (this.currentPage + 1) < this.maxPage));

        for (int i = ELEMENTS_PER_PAGE; i < this.type.size(); i++) {
            if (!this.slotAt(i).isPresent()) {
                this.setSlot(Slot.of(i, EMPTY));
            }
        }
    }
}
