/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.storage.entities.restrict.item;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.ItemInfoProvider;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.restrict.RestrictedEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "restricted_items")
public class RestrictedItemEntity extends RestrictedEntity implements Comparable<RestrictedItemEntity> {

    public static final Comparator<RestrictedItemEntity> PERSISTENT_DATA_COMPARATOR = (o1, o2) -> {
        // sort to consider restrictions without persistent data first
        if (o1.getPersistentData() != null) {
            return 1;
        }

        if (o2.getPersistentData() != null) {
            return -1;
        }

        return 0;
    };

    @Transient
    private static final Function<String, Component> ALTERNATIVE_ADD =
            identifier -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to add an alternative.", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/restrict admin item edit " + identifier + " alternative add "));
    @Transient
    private static final Function<String, Component> ALTERNATIVE_CLEAR =
            identifier -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to clear all alternatives.", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/restrict admin item edit " + identifier + " alternative clear"));
    @Transient
    private static final Function<String, Component> PERSISTENT_DATA_EDIT =
            identifier -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the persistent data.",
                                    NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/restrict admin item edit " + identifier + " persistentData set "));
    @Transient
    private static final Function<String, Component> PERSISTENT_DATA_REMOVE =
            identifier -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to remove the persistent data.",
                                    NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/restrict admin item edit " + identifier + " persistentData remove"));
    @Transient
    private static final Function<String, Component> PERSISTENT_DATA_SET =
            identifier -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to set the persistent data.",
                                    NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/restrict admin item edit " + identifier + " persistentData set "));

    @Column(nullable = false)
    @Getter
    @NonNull
    protected String identifier;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String persistentData;

    @NonNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "original", orphanRemoval = true)
    protected Set<RestrictionAlternativeEntity> alternatives;

    protected RestrictedItemEntity() {}

    public RestrictedItemEntity(@NonNull final DirtCorePlugin plugin,
            @NonNull final ItemInfoProvider itemInfoProvider) {
        this.init(plugin);
        this.identifier = itemInfoProvider.getIdentifier();
        this.persistentData = null;
        this.alternatives = new HashSet<>();
    }

    @Override
    public void onRender(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        final TextComponent.Builder titleBuilder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(Component.text(" Restricted item: ", NamedTextColor.GRAY))
                .append(Component.text(this.identifier, NamedTextColor.GOLD, TextDecoration.BOLD));
        final TextComponent.Builder persistenttDataBuilder = Component.text()
                .append(Component.text("Persistent Data: ", NamedTextColor.GOLD));
        String uniqueIdentifier = this.identifier;

        if (this.persistentData == null) {
            persistenttDataBuilder.append(Component.text("None", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.RESTRICT_ADMIN_ITEM_EDIT_PERSISTENT_DATA)) {
                persistenttDataBuilder.appendSpace()
                        .append(PERSISTENT_DATA_SET.apply(uniqueIdentifier));
            }
        } else {
            uniqueIdentifier += this.persistentData;
            persistenttDataBuilder.append(Component.text(this.persistentData, NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.RESTRICT_ADMIN_ITEM_EDIT_PERSISTENT_DATA)) {
                persistenttDataBuilder.appendSpace()
                        .append(PERSISTENT_DATA_EDIT.apply(uniqueIdentifier))
                        .append(PERSISTENT_DATA_REMOVE.apply(uniqueIdentifier));
            }
        }

        if (sender.hasPermission(Permission.RESTRICT_ADMIN_ITEM_REMOVE)) {
            titleBuilder.appendSpace()
                    .append(RESTRICT_REMOVE.apply("item", uniqueIdentifier));
        }

        builder.add(titleBuilder.build())
                .add(Component.empty())
                .add(persistenttDataBuilder.build());

        final TextComponent.Builder alternativesBuilder = Component.text()
                .append(Component.text("Alternatives: ", NamedTextColor.GOLD));

        if (this.alternatives.isEmpty()) {
            alternativesBuilder.append(
                    Component.text("No alternatives specified.", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.RESTRICT_ADMIN_ITEM_EDIT_ALTERNATIVE)) {
                alternativesBuilder.appendSpace()
                        .append(ALTERNATIVE_ADD.apply(uniqueIdentifier));
            }

            builder.add(alternativesBuilder.build());
        } else {
            if (sender.hasPermission(Permission.RESTRICT_ADMIN_ITEM_EDIT_ALTERNATIVE)) {
                alternativesBuilder.append(ALTERNATIVE_ADD.apply(uniqueIdentifier))
                        .append(ALTERNATIVE_CLEAR.apply(uniqueIdentifier));
            }

            builder.add(alternativesBuilder.build());

            for (final String alternativeName : this.getAlternativeNames()) {
                builder.add(Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(alternativeName, NamedTextColor.GRAY).hoverEvent(
                                HoverEvent.showText(
                                        Component.text("Click to remove this alternative.",
                                                NamedTextColor.RED))).clickEvent(
                                ClickEvent.runCommand("/restrict admin item edit " + this.identifier
                                        + " alternative remove " + alternativeName))).build());
            }
        }

        this.onRender(sender, uniqueIdentifier, builder, "item",
                Permission.RESTRICT_ADMIN_ITEM_EDIT_REASON,
                Permission.RESTRICT_ADMIN_ITEM_EDIT_WORLD,
                Permission.RESTRICT_ADMIN_ITEM_EDIT_ACTION);
    }

    @NonNull
    public List<String> getAlternativeNames() {
        return this.alternatives.stream().map(alternative -> {
            String s = alternative.getIdentifier();
            final String persistentData = alternative.getPersistentData();

            if (persistentData != null) {
                s += persistentData;
            }

            return s;
        }).sorted().collect(ImmutableCollectors.toList());
    }

    public boolean addAlternative(@NonNull final TaskContext context,
            @NonNull final ItemInfoProvider itemInfoProvider) {
        final String identifier = itemInfoProvider.getIdentifier();
        final String persistentData = itemInfoProvider.getPersistentDataAsString();

        if (this.alternatives.stream().anyMatch(
                restrictionAlternative -> restrictionAlternative.getIdentifier().equals(identifier)
                        && (Objects.equals(restrictionAlternative.getPersistentData(),
                        persistentData)))) {
            // already contains alternative
            return false;
        }

        final RestrictionAlternativeEntity restrictionAlternative =
                new RestrictionAlternativeEntity(this, itemInfoProvider);

        context.session().persist(restrictionAlternative);
        this.alternatives.add(restrictionAlternative);
        return true;
    }

    public void clearAlternatives(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.alternatives.forEach(session::remove);
        this.alternatives.clear();
    }

    public boolean removeAlternative(@NonNull final TaskContext context,
            @NonNull final ItemInfoProvider itemInfoProvider) {
        final String identifier = itemInfoProvider.getIdentifier();
        final String persistentData = itemInfoProvider.getPersistentDataAsString();
        RestrictionAlternativeEntity restrictionAlternative = null;

        for (final RestrictionAlternativeEntity ra : this.alternatives) {
            if (ra.getIdentifier().equals(identifier) && (Objects.equals(ra.getPersistentData(),
                    persistentData))) {
                restrictionAlternative = ra;
                break;
            }
        }

        if (restrictionAlternative == null) {
            return false;
        }

        this.alternatives.remove(restrictionAlternative);
        context.session().remove(restrictionAlternative);
        return true;
    }

    @Override
    public int compareTo(@NotNull final RestrictedItemEntity other) {
        final int i = this.identifier.compareTo(other.identifier);

        if (i == 0) {
            if (this.persistentData == null) {
                return -1;
            } else if (other.persistentData == null) {
                return 1;
            }

            return this.persistentData.compareTo(other.persistentData);
        }

        return i;
    }
}
