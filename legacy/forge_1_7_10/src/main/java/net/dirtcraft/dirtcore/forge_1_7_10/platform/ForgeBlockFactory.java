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

package net.dirtcraft.dirtcore.forge_1_7_10.platform;

import cpw.mods.fml.common.registry.GameRegistry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.dirtcraft.dirtcore.common.platform.minecraft.block.BlockFactory;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.dirtcraft.dirtcore.forge_1_7_10.util.ForgeUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeBlockFactory extends BlockFactory<DirtCoreForgePlugin, ForgeBlock> {

    public ForgeBlockFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NonNull String getIdentifier(@NonNull final ForgeBlock block) {
        final StringBuilder builder = new StringBuilder(
                GameRegistry.findUniqueIdentifierFor(block.getBlock()).toString());
        final int metadata = block.getMetadata();

        if (metadata > 0) {
            builder.append(ForgeUtils.IDENTIFIER_SEPARATOR)
                    .append(metadata);
        }

        return builder.toString();
    }

    @Override
    protected @NonNull String getMod(@NonNull final ForgeBlock block) {
        return GameRegistry.findUniqueIdentifierFor(block.getBlock()).modId;
    }

    @Override
    protected boolean isEmpty(@NonNull final ForgeBlock block) {
        final Block mcBlock = block.getBlock();
        return mcBlock == Blocks.air || mcBlock.getMaterial() == Material.air;
    }

    @Override
    protected boolean hasPersistentData(@NonNull final ForgeBlock block) {
        return block.getTileEntity().isPresent();
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final ForgeBlock block) {
        return block.getTileEntity()
                .map(tileEntity -> this.getTagForTileEntity(tileEntity).toString()).orElse(null);
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final ForgeBlock block,
            @Nullable final String s) {
        return block.getTileEntity().map(tileEntity -> {
            NBTTagCompound otherTag = null;

            if (s != null) {
                try {
                    final NBTBase tag = JsonToNBT.func_150315_a(s);

                    if (!(tag instanceof NBTTagCompound)) {
                        // tag is not a compound tag
                        this.getPlugin().getLogger().warn("Tag is not a compound tag: '{}'", s);
                        return false;
                    }

                    otherTag = (NBTTagCompound) tag;
                } catch (final Exception ignored) {
                    // tag could not be parsed
                    this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
                    return false;
                }
            }

            final NBTTagCompound ownTag = new NBTTagCompound();

            tileEntity.writeToNBT(ownTag);

            if (otherTag == null) {
                // can only be equal when own tag is empty
                return ownTag.hasNoTags();
            }

            return otherTag.equals(ownTag);
        }).orElse(false);
    }

    @Override
    protected boolean persistentDataPartiallyMatches(@NonNull final ForgeBlock block,
            @NonNull final String s) {
        final NBTTagCompound otherTag;

        try {
            final NBTBase tag = JsonToNBT.func_150315_a(s);

            if (!(tag instanceof NBTTagCompound)) {
                // tag is not a compound tag
                this.getPlugin().getLogger().warn("Tag is not a compound tag: '{}'", s);
                return false;
            }

            otherTag = (NBTTagCompound) tag;
        } catch (final Exception ignored) {
            // tag could not be parsed
            this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
            return false;
        }

        if (otherTag.hasNoTags()) {
            // contains all tags, since there are none
            return true;
        }

        final Optional<TileEntity> blockEntityOptional = block.getTileEntity();

        if (!blockEntityOptional.isPresent()) {
            return false;
        }

        final NBTTagCompound ownTag = new NBTTagCompound();

        blockEntityOptional.get().writeToNBT(ownTag);

        //noinspection unchecked
        for (final String key : (Set<String>) otherTag.getKeySet()) {
            final NBTBase t1 = ownTag.getTag(key);
            final NBTBase t2 = otherTag.getTag(key);

            if (!Objects.equals(t1, t2)) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    private NBTTagCompound getTagForTileEntity(@NonNull final TileEntity tileEntity) {
        final NBTTagCompound nbt = new NBTTagCompound();
        tileEntity.writeToNBT(nbt);
        return nbt;
    }
}
