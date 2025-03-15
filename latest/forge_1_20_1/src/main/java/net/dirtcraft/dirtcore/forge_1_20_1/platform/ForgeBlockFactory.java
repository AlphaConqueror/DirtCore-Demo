/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform;

import java.util.Objects;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.platform.minecraft.block.BlockFactory;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.util.ForgeBlock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ForgeBlockFactory extends BlockFactory<DirtCoreForgePlugin, ForgeBlock> {

    public ForgeBlockFactory(final DirtCoreForgePlugin plugin) {
        super(plugin);
    }

    @Override
    protected @NonNull String getIdentifier(@NonNull final ForgeBlock block) {
        //noinspection deprecation
        return block.getBlockState().getBlock().builtInRegistryHolder().key().location().toString();
    }

    @Override
    protected @NonNull String getMod(@NonNull final ForgeBlock block) {
        //noinspection deprecation
        return block.getBlockState().getBlock().builtInRegistryHolder().key().registry()
                .getNamespace();
    }

    @Override
    protected boolean isEmpty(@NonNull final ForgeBlock block) {
        return block.getBlockState().isAir();
    }

    @Override
    protected boolean hasPersistentData(@NonNull final ForgeBlock block) {
        return block.getBlockEntity().map(blockEntity -> !blockEntity.getPersistentData().isEmpty())
                .orElse(false);
    }

    @Override
    protected @Nullable String getPersistentDataAsString(@NonNull final ForgeBlock block) {
        return block.getBlockEntity().map(blockEntity -> blockEntity.getPersistentData().toString())
                .orElse(null);
    }

    @Override
    protected boolean persistentDataMatches(@NonNull final ForgeBlock block,
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
    protected boolean persistentDataPartiallyMatches(@NonNull final ForgeBlock block,
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

        if (!blockEntityOptional.isPresent()) {
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
