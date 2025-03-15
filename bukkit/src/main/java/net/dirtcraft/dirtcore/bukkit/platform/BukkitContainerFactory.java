package net.dirtcraft.dirtcore.bukkit.platform;

import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.container.ContainerFactory;
import net.dirtcraft.dirtcore.common.platform.minecraft.gui.slot.Slot;
import org.bukkit.event.inventory.ClickType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class BukkitContainerFactory implements ContainerFactory<ClickType> {

    private final DirtCoreBukkitPlugin plugin;

    public BukkitContainerFactory(final DirtCoreBukkitPlugin plugin) {this.plugin = plugin;}

    @Override
    public Slot.@NonNull ClickType parseClickType(@NotNull final ClickType clickType) {
        switch (clickType) {
            case LEFT:
                return Slot.ClickType.LEFT;
            case RIGHT:
                return Slot.ClickType.RIGHT;
            default:
                return Slot.ClickType.UNKNOWN;
        }
    }
}
