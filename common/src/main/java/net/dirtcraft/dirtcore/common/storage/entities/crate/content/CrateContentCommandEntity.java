/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.crate.content;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NonNull;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crate_content_commands")
public class CrateContentCommandEntity implements Comparable<CrateContentCommandEntity>,
        DirtCoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    protected long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected CrateContentEntity original;

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String command;

    protected CrateContentCommandEntity() {}

    public CrateContentCommandEntity(@NonNull final CrateContentEntity original,
            @NonNull final String command) {
        this.original = original;
        this.command = command;
    }

    @Override
    public int compareTo(@NotNull final CrateContentCommandEntity other) {
        return Long.compare(this.id, other.id);
    }
}
