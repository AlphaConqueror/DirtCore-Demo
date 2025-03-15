/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.restrict;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.manager.restrict.RestrictionManager;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "restriction_actions")
public class RestrictionActionEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected RestrictedEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String action;

    protected RestrictionActionEntity() {}

    public RestrictionActionEntity(@NonNull final RestrictedEntity original,
            final RestrictionManager.@NonNull Action action) {
        this.original = original;
        this.action = action.name();
    }
}
