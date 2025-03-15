/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.restrict.mod;

import com.google.common.collect.ImmutableList;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictedEntity;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "restricted_mods")
public class RestrictedModEntity extends RestrictedEntity implements Comparable<RestrictedModEntity> {

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String identifier;

    protected RestrictedModEntity() {}

    public RestrictedModEntity(@NonNull final DirtCorePlugin plugin,
            @NonNull final String identifier) {
        this.init(plugin);
        this.identifier = identifier;
    }

    @Override
    public void onRender(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        builder.add(Component.text()
                        .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                        .append(Component.text(" Restricted mod: ", NamedTextColor.GRAY))
                        .append(Component.text(this.identifier, NamedTextColor.GOLD,
                                TextDecoration.BOLD))
                        .build())
                .add(Component.empty());
        this.onRender(sender, this.identifier, builder, "mod",
                Permission.RESTRICT_ADMIN_MOD_EDIT_REASON, Permission.RESTRICT_ADMIN_MOD_EDIT_WORLD,
                Permission.RESTRICT_ADMIN_MOD_EDIT_ACTION);
    }

    @Override
    public int compareTo(@NotNull final RestrictedModEntity other) {
        return this.identifier.compareTo(other.identifier);
    }
}
