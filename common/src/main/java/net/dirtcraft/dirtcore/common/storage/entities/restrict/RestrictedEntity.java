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

package net.dirtcraft.dirtcore.common.storage.entities.restrict;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.manager.restrict.RestrictionManager;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class RestrictedEntity implements DirtCoreEntity {

    @Transient
    protected static final BiFunction<String, String, Component> RESTRICT_REMOVE =
            (specifier, identifier) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to remove this restriction.",
                                    NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/restrict admin " + specifier + " remove " + identifier));
    @Transient
    private static final BiFunction<String, String, Component> ACTION_ADD =
            (specifier, identifier) -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add an action.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/restrict admin " + specifier + " edit " + identifier
                            + " action add "));
    @Transient
    private static final BiFunction<String, String, Component> ACTION_CLEAR =
            (specifier, identifier) -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to clear all actions.", NamedTextColor.RED))).clickEvent(
                    ClickEvent.runCommand("/restrict admin " + specifier + " edit " + identifier
                            + " action clear"));
    @Transient
    private static final BiFunction<String, String, Component> REASON_ADD =
            (specifier, identifier) -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a reason.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/restrict admin " + specifier + " edit " + identifier
                            + " reason set "));
    @Transient
    private static final BiFunction<String, String, Component> REASON_EDIT =
            (specifier, identifier) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the reason.", NamedTextColor.GOLD))).clickEvent(
                    ClickEvent.suggestCommand("/restrict admin " + specifier + " edit " + identifier
                            + " reason set "));
    @Transient
    private static final BiFunction<String, String, Component> REASON_REMOVE =
            (specifier, identifier) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to remove the reason.", NamedTextColor.RED))).clickEvent(
                    ClickEvent.runCommand("/restrict admin " + specifier + " edit " + identifier
                            + " reason remove"));
    @Transient
    private static final BiFunction<String, String, Component> WORLD_ACCESS_EDIT =
            (specifier, identifier) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the world access.", NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/restrict admin " + specifier + " edit " + identifier
                                    + " world access set "));
    @Transient
    private static final BiFunction<String, String, Component> WORLD_ADD =
            (specifier, identifier) -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a world.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/restrict admin " + specifier + " edit " + identifier
                            + " world add "));
    @Transient
    private static final BiFunction<String, String, Component> WORLD_CLEAR =
            (specifier, identifier) -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to clear all worlds.", NamedTextColor.RED))).clickEvent(
                    ClickEvent.runCommand("/restrict admin " + specifier + " edit " + identifier
                            + " world clear"));

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected long id;

    @Column(columnDefinition = "TEXT")
    @Getter
    @Nullable
    @Setter
    protected String reason;

    @Column(nullable = false)
    @NonNull
    protected String server;

    @Column(name = "access_control_type", nullable = false)
    @NonNull
    protected String accessControlType;

    @NonNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "original", orphanRemoval = true)
    protected Set<RestrictionWorldEntity> worlds;

    @NonNull
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "original", orphanRemoval = true)
    protected Set<RestrictionActionEntity> actions;

    protected RestrictedEntity() {}

    public abstract void onRender(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder);

    public void onRenderDetails(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        builder.add(Component.empty());
        this.onRender(sender, builder);
        builder.add(Component.empty());
    }

    public boolean isRestricted(@NonNull final World world) {
        return this.worlds.isEmpty() || this.getAccessControlType()
                .isRestricted(this.worlds, world);
    }

    public boolean isRestricted(final RestrictionManager.@NonNull Action action) {
        return this.actions.isEmpty() || this.actions.stream()
                .anyMatch(restrictionAction -> restrictionAction.getAction().equals(action.name()));
    }

    public RestrictionManager.@NonNull AccessControlType getAccessControlType() {
        return RestrictionManager.AccessControlType.valueOf(this.accessControlType);
    }

    public boolean setAccessControlType(
            final RestrictionManager.@NonNull AccessControlType accessControlType) {
        if (this.accessControlType.equalsIgnoreCase(accessControlType.name())) {
            return false;
        }

        this.accessControlType = accessControlType.name();
        return true;
    }

    @NonNull
    public List<String> getWorldNames() {
        return this.worlds.stream().map(RestrictionWorldEntity::getIdentifier).sorted()
                .collect(ImmutableCollectors.toList());
    }

    public boolean addWorld(@NonNull final TaskContext context, @NonNull final World world) {
        final String identifier = world.getIdentifier();

        if (this.worlds.stream().anyMatch(
                restrictionWorld -> restrictionWorld.getIdentifier().equals(identifier))) {
            // already contains world
            return false;
        }

        final RestrictionWorldEntity restrictionWorld = new RestrictionWorldEntity(this, world);

        context.session().persist(restrictionWorld);
        this.worlds.add(restrictionWorld);
        return true;
    }

    public void clearWorlds(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.worlds.forEach(session::remove);
        this.worlds.clear();
    }

    public boolean removeWorld(@NonNull final TaskContext context, @NonNull final World world) {
        final String identifier = world.getIdentifier();
        RestrictionWorldEntity restrictionWorld = null;

        for (final RestrictionWorldEntity rw : this.worlds) {
            if (rw.getIdentifier().equals(identifier)) {
                restrictionWorld = rw;
                break;
            }
        }

        if (restrictionWorld == null) {
            return false;
        }

        this.worlds.remove(restrictionWorld);
        context.session().remove(restrictionWorld);
        return true;
    }

    @NonNull
    public List<String> getActionNames() {
        return this.actions.stream().map(RestrictionActionEntity::getAction).sorted()
                .collect(ImmutableCollectors.toList());
    }

    public boolean addAction(@NonNull final TaskContext context,
            final RestrictionManager.@NonNull Action action) {
        final String identifier = action.name();

        if (this.actions.stream()
                .anyMatch(restrictionAction -> restrictionAction.getAction().equals(identifier))) {
            // already contains action
            return false;
        }

        final RestrictionActionEntity restrictionAction = new RestrictionActionEntity(this, action);

        context.session().persist(restrictionAction);
        this.actions.add(restrictionAction);
        return true;
    }

    public void clearActions(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.actions.forEach(session::remove);
        this.actions.clear();
    }

    public boolean removeAction(@NonNull final TaskContext context,
            final RestrictionManager.@NonNull Action action) {
        final String identifier = action.name();
        RestrictionActionEntity restrictionAction = null;

        for (final RestrictionActionEntity ra : this.actions) {
            if (ra.getAction().equals(identifier)) {
                restrictionAction = ra;
                break;
            }
        }

        if (restrictionAction == null) {
            return false;
        }

        this.actions.remove(restrictionAction);
        context.session().remove(restrictionAction);
        return true;
    }

    protected void onRender(@NonNull final Sender sender, @NonNull final String identifier,
            final ImmutableList.@NonNull Builder<Component> builder,
            @NonNull final String specifier, @NonNull final Permission adminEditReasonPermission,
            @NonNull final Permission adminEditWorldPermission,
            @NonNull final Permission adminEditActionPermission) {
        final TextComponent.Builder reasonBuilder = Component.text()
                .append(Component.text("Reason: ", NamedTextColor.GOLD));

        if (this.reason == null) {
            reasonBuilder.append(Component.text("No reason specified.", NamedTextColor.GRAY));

            if (sender.hasPermission(adminEditReasonPermission)) {
                reasonBuilder.appendSpace()
                        .append(REASON_ADD.apply(specifier, identifier));
            }
        } else {
            reasonBuilder.append(Component.text(this.reason));

            if (sender.hasPermission(adminEditReasonPermission)) {
                reasonBuilder.appendSpace()
                        .append(REASON_EDIT.apply(specifier, identifier))
                        .append(REASON_REMOVE.apply(specifier, identifier));
            }
        }

        builder.add(reasonBuilder.build());

        final RestrictionManager.AccessControlType accessType = this.getAccessControlType();
        final NamedTextColor accessColor = accessType.getColor();
        final TextComponent.Builder accessBuilder = Component.text()
                .append(Component.text("World Access: ", NamedTextColor.GOLD))
                .append(Component.text(this.accessControlType, accessColor));
        final boolean editWorldPermission = sender.hasPermission(adminEditWorldPermission);

        if (editWorldPermission) {
            accessBuilder.appendSpace()
                    .append(WORLD_ACCESS_EDIT.apply(specifier, identifier));
        }

        builder.add(accessBuilder.build());

        final TextComponent.Builder worldsBuilder = Component.text()
                .append(Component.text("Worlds: ", NamedTextColor.GOLD));

        if (this.worlds.isEmpty()) {
            worldsBuilder.append(Component.text(accessType.getDescriptor(), accessColor));

            if (editWorldPermission) {
                worldsBuilder.appendSpace()
                        .append(WORLD_ADD.apply(specifier, identifier));
            }

            builder.add(worldsBuilder.build());
        } else {
            if (editWorldPermission) {
                worldsBuilder.append(WORLD_ADD.apply(specifier, identifier))
                        .append(WORLD_CLEAR.apply(specifier, identifier));
            }

            builder.add(worldsBuilder.build());

            for (final String worldName : this.getWorldNames()) {
                builder.add(Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(worldName, accessColor).hoverEvent(
                                HoverEvent.showText(Component.text("Click to remove this world.",
                                        NamedTextColor.RED))).clickEvent(ClickEvent.runCommand(
                                "/restrict admin " + specifier + " edit " + identifier + " world "
                                        + "remove " + worldName))).build());
            }
        }

        final TextComponent.Builder actionsBuilder = Component.text()
                .append(Component.text("Actions: ", NamedTextColor.GOLD));

        if (this.actions.isEmpty()) {
            actionsBuilder.append(Component.text("All", NamedTextColor.RED));

            if (sender.hasPermission(adminEditActionPermission)) {
                actionsBuilder.appendSpace()
                        .append(ACTION_ADD.apply(specifier, identifier));
            }

            builder.add(actionsBuilder.build());
        } else {
            if (sender.hasPermission(adminEditActionPermission)) {
                actionsBuilder.append(ACTION_ADD.apply(specifier, identifier))
                        .append(ACTION_CLEAR.apply(specifier, identifier));
            }

            builder.add(actionsBuilder.build());

            for (final String actionName : this.getActionNames()) {
                builder.add(Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(actionName, NamedTextColor.RED).hoverEvent(
                                HoverEvent.showText(Component.text("Click to remove this action.",
                                        NamedTextColor.RED))).clickEvent(ClickEvent.runCommand(
                                "/restrict admin " + specifier + " edit " + identifier + " action "
                                        + "remove " + actionName))).build());
            }
        }
    }

    protected void init(@NonNull final DirtCorePlugin plugin) {
        this.reason = null;
        this.server = plugin.getServerIdentifier();
        this.accessControlType = RestrictionManager.AccessControlType.BLACKLIST.name();
        this.worlds = new HashSet<>();
        this.actions = new HashSet<>();
    }
}
