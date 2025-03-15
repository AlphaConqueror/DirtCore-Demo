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
