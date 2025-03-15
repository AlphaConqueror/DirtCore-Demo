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

package net.dirtcraft.dirtcore.forge_1_12_2;

import java.util.List;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.event.AbstractEventBus;
import net.dirtcraft.dirtcore.common.event.EventDispatcher;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerLogoutEvent;
import net.dirtcraft.dirtcore.forge_1_12_2.api.event.ForgeLoaderEventDispatcher;
import net.dirtcraft.dirtcore.forge_1_12_2.api.util.ForgeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ForgeEventDispatcher extends EventDispatcher implements ForgeLoaderEventDispatcher<ForgeBlock, ItemStack, World> {

    private final DirtCoreForgePlugin plugin;

    public ForgeEventDispatcher(final AbstractEventBus<?> eventBus,
            final DirtCoreForgePlugin plugin) {
        super(eventBus);
        this.plugin = plugin;
    }

    @Override
    public void dispatchBlockChangeEvent(final ForgeBlock oldMcBlock, final ForgeBlock newMcBlock,
            final int flags, final World mcWorld, final int x, final int y, final int z) {
        final net.dirtcraft.dirtcore.common.model.minecraft.Block oldBlock =
                this.plugin.getPlatformFactory().wrapBlock(oldMcBlock);
        final net.dirtcraft.dirtcore.common.model.minecraft.Block newBlock =
                this.plugin.getPlatformFactory().wrapBlock(newMcBlock);
        final net.dirtcraft.dirtcore.common.model.minecraft.World world =
                this.plugin.getPlatformFactory().wrapWorld(mcWorld);

        this.dispatchBlockChangeEvent(oldBlock, newBlock, flags, world, x, y, z);
    }

    @Override
    public boolean dispatchBlockPushReactionEvent(final ForgeBlock mcBlock) {
        final net.dirtcraft.dirtcore.common.model.minecraft.Block block =
                this.plugin.getPlatformFactory().wrapBlock(mcBlock);
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

    @Override
    public boolean dispatchPlayerBlockPlace(final boolean initialState, final UUID uniqueId,
            final String username, final ForgeBlock mcBlock, final World mcWorld, final int x,
            final int y, final int z, final boolean isFakePlayer) {
        final net.dirtcraft.dirtcore.common.model.minecraft.Block block =
                this.plugin.getPlatformFactory().wrapBlock(mcBlock);
        final net.dirtcraft.dirtcore.common.model.minecraft.World world =
                this.plugin.getPlatformFactory().wrapWorld(mcWorld);

        return this.dispatchPlayerBlockPlace(initialState, uniqueId, username, block, world, x, y,
                z, isFakePlayer);
    }

    @Override
    public void dispatchPlayerLogout(final UUID uniqueId, final String username) {
        // this need to be posted sync due to the ban check upon logout
        this.postSync(PlayerLogoutEvent.class, uniqueId, username);
    }
}
