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

package net.dirtcraft.dirtcore.common.storage.entities.kit;

import com.google.common.collect.ImmutableList;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "kits")
public class KitEntity implements Comparable<KitEntity>, DirtCoreEntity {

    @Transient
    private static final Function<String, Component> KIT_DELETE =
            kitName -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to delete this kit.", NamedTextColor.RED)))/* .clickEvent(
                    ClickEvent.runCommand(KitCommand.COMMAND_KIT_ADMIN_DELETE.apply(kitName))) */;
    @Transient
    private static final Function<String, Component> KIT_EDIT_COOLDOWN =
            kitName -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the cooldown.", NamedTextColor.GOLD)))
            /* .clickEvent(
                    ClickEvent.suggestCommand(
                            KitCommand.COMMAND_KIT_ADMIN_EDIT_COOLDOWN.apply(kitName))) */;

    @Transient
    private static final Function<String, Component> KIT_EDIT_DISPLAY_NAME =
            kitName -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the display name.", NamedTextColor.GOLD)))
                    /* .clickEvent(ClickEvent.suggestCommand(
                            KitCommand.COMMAND_KIT_ADMIN_EDIT_DISPLAY_NAME.apply(kitName))) */;

    @Transient
    private static final Function<String, Component> KIT_EDIT_ITEMS = kitName -> Component.text()
            .style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
            .append(Component.text('['))
            .append(Component.text("Click to update items", NamedTextColor.GOLD))
            .append(Component.text(']')).hoverEvent(HoverEvent.showText(Component.text(
                    "Replaces the items in the kit with the items in your inventory.",
                    NamedTextColor.GRAY)))/* .clickEvent(
                    ClickEvent.runCommand(KitCommand.COMMAND_KIT_ADMIN_EDIT_ITEMS.apply(kitName))
                    ) */.build();

    @Transient
    private static final Function<String, Component> KIT_SHOW = kitName -> Component.text()
            .style(Style.style(NamedTextColor.DARK_GRAY, TextDecoration.ITALIC))
            .append(Component.text('['))
            .append(Component.text("Click to see items", NamedTextColor.GOLD))
            .append(Component.text(']')).hoverEvent(HoverEvent.showText(
                    Component.text("Opens a GUI showing the items.", NamedTextColor.GRAY)))
            /* .clickEvent(ClickEvent.runCommand(KitCommand.COMMAND_KIT_SHOW.apply(kitName)))
             */.build();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;

    @Column(nullable = false)
    @Getter
    @NonNull
    @Setter
    protected String name;

    @Column(name = "display_name", nullable = false)
    @NonNull
    @Setter
    protected String displayName;

    // cooldown in seconds; one time use if null
    @Column
    @Getter
    @Nullable
    protected Long cooldown;

    @Column(nullable = false)
    @NonNull
    protected String server;

    @Getter
    @OneToMany(mappedBy = "original", orphanRemoval = true)
    protected Set<KitClaimEntryEntity> claimEntries;

    @Getter
    @OneToMany(mappedBy = "original", orphanRemoval = true)
    @OrderBy
    protected SortedSet<KitItemEntity> items;

    protected KitEntity() {}

    public KitEntity(@NonNull final DirtCorePlugin plugin, @NonNull final String name) {
        this.name = name;
        this.displayName = name;
        this.cooldown = null;
        this.server = plugin.getServerIdentifier();
        this.claimEntries = new HashSet<>();
        this.items = new TreeSet<>();
    }

    public void onRenderDetails(@NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        builder.add(Component.empty());

        final TextComponent.Builder titleBuilder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(Component.text(" Kit: ", NamedTextColor.GRAY))
                .append(Component.text(this.name, NamedTextColor.GOLD, TextDecoration.BOLD));

        if (sender.hasPermission(Permission.KIT_ADMIN_DELETE)) {
            titleBuilder.appendSpace()
                    .append(KIT_DELETE.apply(this.name));
        }

        builder.add(titleBuilder.build())
                .add(Component.empty());

        final TextComponent.Builder displayNameBuilder = Component.text()
                .append(Component.text("Display Name: ", NamedTextColor.GOLD))
                .append(this.getDisplayNameAsComponent());

        if (sender.hasPermission(Permission.KIT_ADMIN_EDIT_DISPLAY_NAME)) {
            displayNameBuilder.appendSpace()
                    .append(KIT_EDIT_DISPLAY_NAME.apply(this.name));
        }

        builder.add(displayNameBuilder.build());

        final TextComponent.Builder cooldownBuilder = Component.text()
                .append(Component.text("Cooldown: ", NamedTextColor.GOLD));

        if (this.cooldown == null || this.cooldown < 0) {
            cooldownBuilder.append(Component.text("Only claimable once.", NamedTextColor.GRAY));
        } else if (this.cooldown == 0) {
            cooldownBuilder.append(Component.text("None", NamedTextColor.GRAY));
        } else {
            cooldownBuilder.append(
                    Component.text(FormatUtils.formatDateDiff(this.cooldown, false, false),
                            NamedTextColor.BLUE));
        }

        if (sender.hasPermission(Permission.KIT_ADMIN_EDIT_COOLDOWN)) {
            cooldownBuilder.appendSpace()
                    .append(KIT_EDIT_COOLDOWN.apply(this.name));
        }

        builder.add(cooldownBuilder.build());

        final boolean hasPermissionKitShow = sender.hasPermission(Permission.KIT_SHOW);

        if (hasPermissionKitShow) {
            builder.add(Component.empty())
                    .add(KIT_SHOW.apply(this.name));
        }

        if (sender.hasPermission(Permission.KIT_ADMIN_EDIT_ITEMS)) {
            if (!hasPermissionKitShow) {
                builder.add(Component.empty());
            }

            builder.add(KIT_EDIT_ITEMS.apply(this.name));
        }

        builder.add(Component.empty());
    }

    @NonNull
    public Component getDisplayNameAsComponent() {
        return MessagingManager.MINIMESSAGE.deserialize(this.displayName);
    }

    public void setCooldown(@NonNull final ChronoUnit unit, final long time) {
        this.cooldown = unit.getDuration().getSeconds() * time;
    }

    public void setOnlyClaimableOnce() {
        this.cooldown = null;
    }

    public void updateItems(@NonNull final TaskContext context,
            @NonNull final Collection<ItemStack> items) {
        this.items.clear();

        for (final ItemStack item : items) {
            final KitItemEntity kitItem = new KitItemEntity(this, item);
            context.session().persist(kitItem);
            this.items.add(kitItem);
        }
    }

    @Override
    public int compareTo(@NotNull final KitEntity other) {
        return Long.compare(this.id, other.id);
    }
}
