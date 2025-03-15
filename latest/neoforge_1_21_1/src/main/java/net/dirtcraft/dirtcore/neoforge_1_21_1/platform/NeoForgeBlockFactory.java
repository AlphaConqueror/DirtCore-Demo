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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform;

import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.platform.minecraft.block.BlockFactory;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NeoForgeBlockFactory extends BlockFactory<DirtCoreNeoForgePlugin, NeoForgeBlock> {

    public NeoForgeBlockFactory(final DirtCoreNeoForgePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NonNull String getIdentifier(@NonNull final NeoForgeBlock block) {
        //noinspection deprecation
        return block.getBlockState().getBlock().builtInRegistryHolder().key().location().toString();
    }

    @Override
    protected @NonNull String getMod(@NonNull final NeoForgeBlock block) {
        //noinspection deprecation
        return block.getBlockState().getBlock().builtInRegistryHolder().key().registry()
                .getNamespace();
    }

    @Override
    protected boolean isEmpty(@NonNull final NeoForgeBlock block) {
        return block.getBlockState().isAir();
    }

    @Override
    protected boolean hasPersistentData(@NonNull final NeoForgeBlock block) {
        return block.getBlockEntity().map(blockEntity -> !blockEntity.getPersistentData().isEmpty())
                .orElse(false);
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final NeoForgeBlock block) {
        return block.getBlockEntity().map(blockEntity -> blockEntity.getPersistentData().toString())
                .orElse(null);
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final NeoForgeBlock block,
            @Nullable final String s) {
        return block.getBlockEntity().map(blockEntity -> {
            CompoundTag otherTag = null;

            if (s != null) {
                try {
                    otherTag = TagParser.parseTag(s);
                } catch (final Exception ignored) {
                    // tag could not be parsed
                    this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
                    return false;
                }
            }

            final CompoundTag ownTag = blockEntity.getPersistentData();

            if (otherTag == null) {
                // can only be equal when own tag is empty
                return ownTag.isEmpty();
            }

            return otherTag.equals(ownTag);
        }).orElse(false);
    }

    @Override
    protected boolean persistentDataPartiallyMatches(@NonNull final NeoForgeBlock block,
            @NonNull final String s) {
        final CompoundTag otherTag;

        try {
            otherTag = TagParser.parseTag(s);
        } catch (final Exception ignored) {
            // tag could not be parsed
            this.getPlugin().getLogger().warn("Could not parse tag: '{}'", s);
            return false;
        }

        if (otherTag.isEmpty()) {
            // contains all tags, since there are none
            return true;
        }

        final Optional<BlockEntity> blockEntityOptional = block.getBlockEntity();

        if (blockEntityOptional.isEmpty()) {
            return false;
        }

        final CompoundTag ownTag = blockEntityOptional.get().getPersistentData();

        for (final String key : otherTag.getAllKeys()) {
            final Tag t1 = ownTag.get(key);
            final Tag t2 = otherTag.get(key);

            if (!Objects.equals(t1, t2)) {
                return false;
            }
        }

        return true;
    }
}
