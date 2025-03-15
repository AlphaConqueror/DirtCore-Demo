/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1;

import java.util.List;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.event.EventDispatcher;
import net.dirtcraft.dirtcore.common.loader.event.LoaderEventDispatcher;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ForgeEventDispatcher extends EventDispatcher implements LoaderEventDispatcher<BlockState, ItemStack, Level> {

    private final DirtCoreForgePlugin plugin;

    public ForgeEventDispatcher(final AbstractEventBus<?> eventBus,
            final DirtCoreForgePlugin plugin) {
        super(eventBus);
        this.plugin = plugin;
    }

    @Override
    public void dispatchBlockChangeEvent(final BlockState oldBlockState,
            final BlockState newBlockState, final int flags, final Level level, final int x,
            final int y, final int z) {
        final Block oldBlock =
                this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(oldBlockState));
        final Block newBlock =
                this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(newBlockState));
        final World world = this.plugin.getPlatformFactory().wrapWorld(level);

        this.dispatchBlockChangeEvent(oldBlock, newBlock, flags, world, x, y, z);
    }

    @Override
    public boolean dispatchBlockPushReactionEvent(final BlockState blockState) {
        final Block block = this.plugin.getPlatformFactory().wrapBlock(ForgeBlock.of(blockState));
        return this.dispatchBlockPushReactionEvent(false, block);
    }

    @Override
    public boolean dispatchPlayerInventoryClick(final UUID uniqueId, final String username,
            final List<ItemStack> list) {
        for (final ItemStack itemStack : list) {
            if (this.dispatchPlayerInventoryClick(false, uniqueId, username,
                    this.plugin.getPlatformFactory().wrapItemStack(itemStack))) {
                return true;
            }
        }

        return false;
    }
}
