package net.dirtcraft.dirtcore.bukkit.platform.container;

import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.bukkit.platform.BukkitContainerFactory;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.container.Container;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitContainerAdapter implements Listener {

    @NonNull
    private final DirtCoreBukkitPlugin plugin;
    @NonNull
    private final BukkitContainerFactory factory;
    @NonNull
    private final Container container;
    @NonNull
    private final Inventory inventory;

    public BukkitContainerAdapter(@NonNull final DirtCoreBukkitPlugin plugin,
            @NonNull final BukkitContainerFactory factory, @NonNull final Container container) {
        this.plugin = plugin;
        this.factory = factory;
        this.container = container;
        this.inventory = Bukkit.createInventory(null, container.size(), container.getTitle()
                .map(title -> this.plugin.getPlatformFactory().transformToString(title))
                .orElse(""));
    }

    public void init(@NonNull final Player player) {
        this.container.init(player);

        this.container.getSlots()
                .forEach((index, slot) -> this.inventory.setItem(index, slot.getItemStack()));
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inventory)) {
            this.container.slotAt(event.getSlot())
                    .ifPresent(slot -> slot.onClick(this.factory.parseClickType(event.getClick())));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (event.getInventory().equals(this.inventory)) {
            event.setCancelled(true);
        }
    }
}
