/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.limit;

import java.util.Comparator;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "limited_block_entries")
public class LimitedBlockEntryEntity {

    @Transient
    public static final Comparator<LimitedBlockEntryEntity> COMPARATOR =
            Comparator.comparing(LimitedBlockEntryEntity::getWorld).thenComparingInt(
                    o -> Math.abs(o.getX()) + Math.abs(o.getY()) + Math.abs(o.getZ()));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Getter
    @ManyToOne
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected LimitedBlockEntity original;

    @Column(name = "unique_id", nullable = false)
    @Getter
    @NonNull
    protected String uniqueId;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String world;

    @Column(nullable = false)
    @Getter
    protected int x;

    @Column(nullable = false)
    @Getter
    protected int y;

    @Column(nullable = false)
    @Getter
    protected int z;

    protected LimitedBlockEntryEntity() {}

    public LimitedBlockEntryEntity(@NonNull final LimitedBlockEntity original,
            @NonNull final UUID uniqueId, @NonNull final World world, final int x, final int y,
            final int z) {
        this.original = original;
        this.uniqueId = uniqueId.toString();
        this.world = world.getIdentifier();
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @NonNull
    public BlockPos asBlockPos() {
        return BlockPos.of(this.x, this.y, this.z);
    }

    public int getChunkX() {
        return World.blockToChunkCoordinate(this.x);
    }

    public int getChunkZ() {
        return World.blockToChunkCoordinate(this.z);
    }
}
