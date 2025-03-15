/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.restrict.item;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictedEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "restriction_alternatives")
public class RestrictionAlternativeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected RestrictedEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String identifier;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    protected String persistentData;

    protected RestrictionAlternativeEntity() {}

    public RestrictionAlternativeEntity(@NonNull final RestrictedEntity original,
            @NonNull final ItemInfoProvider itemInfoProvider) {
        this.original = original;
        this.identifier = itemInfoProvider.getIdentifier();
        this.persistentData = itemInfoProvider.getPersistentDataAsString();
    }
}
