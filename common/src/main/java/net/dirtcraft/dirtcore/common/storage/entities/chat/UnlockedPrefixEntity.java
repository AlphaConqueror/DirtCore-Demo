/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.chat;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "unlocked_prefixes")
public class UnlockedPrefixEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false, length = 36)
    @NonNull
    protected String unique_id;

    @Column(name = "prefix_name", nullable = false)
    @Getter
    @NonNull
    protected String prefixName;

    protected UnlockedPrefixEntity() {}

    public UnlockedPrefixEntity(@NonNull final UUID uniqueId, @NonNull final String prefixName) {
        this.unique_id = uniqueId.toString();
        this.prefixName = prefixName;
    }
}
