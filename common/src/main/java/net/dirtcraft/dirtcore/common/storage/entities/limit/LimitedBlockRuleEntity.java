/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.limit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.manager.limit.LimitManager;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "limited_block_rules")
public class LimitedBlockRuleEntity implements DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @ManyToOne
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected LimitedBlockEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String rule;

    @Column(nullable = false)
    @Getter
    @Setter
    protected long amount;

    protected LimitedBlockRuleEntity() {}

    public LimitedBlockRuleEntity(@NonNull final LimitedBlockEntity original,
            final LimitManager.@NonNull Rule rule, final long amount) {
        this.original = original;
        this.rule = rule.name();
        this.amount = amount;
    }
}
