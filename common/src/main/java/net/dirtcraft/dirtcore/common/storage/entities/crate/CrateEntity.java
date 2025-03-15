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

package net.dirtcraft.dirtcore.common.storage.entities.crate;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3i;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.content.CrateContentEntity;
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

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crates")
public class CrateEntity implements DirtCoreEntity {

    @Transient
    private static final Function<String, Component> CONTENT_ADD =
            crateName -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to add content.", NamedTextColor.GREEN)))
                    .clickEvent(ClickEvent.suggestCommand("/crate content " + crateName + " add "));
    @Transient
    private static final Function<String, Component> CONTENT_CLEAR =
            crateName -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to clear contents.", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/crate content " + crateName + " clear"));
    @Transient
    private static final BiFunction<String, Long, Component> CONTENT_REMOVE =
            (crateName, contentId) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to remove this crate content.",
                                    NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/crate content " + crateName + " remove " + contentId));

    @Transient
    private static final Function<String, Component> CRATE_DELETE =
            crateName -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to delete this crate.", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/crate delete " + crateName));

    @Transient
    private static final Function<String, Component> DISPLAY_NAME_EDIT =
            crateName -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the display name.", NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/crate edit " + crateName + " displayName "));
    @Transient
    private static final Function<String, Component> SHOULD_BROADCAST_EDIT =
            crateName -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the should broadcast flag.",
                            NamedTextColor.GOLD))).clickEvent(
                    ClickEvent.suggestCommand("/crate edit " + crateName + " shouldBroadcast "));

    @Transient
    private static final Function<String, Component> KEY_EDIT = crateName -> Components.EDIT.build()
            .hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the key.", NamedTextColor.GOLD)))
            .clickEvent(ClickEvent.suggestCommand("/crate key " + crateName + " set "));
    @Transient
    private static final Function<String, Component> KEY_SET = crateName -> Components.ADD.build()
            .hoverEvent(HoverEvent.showText(
                    Component.text("Click to set the key.", NamedTextColor.GREEN)))
            .clickEvent(ClickEvent.suggestCommand("/crate key " + crateName + " set "));
    @Transient
    private static final Function<String, Component> KEY_UNSET =
            crateName -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to unset the key.", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/crate key " + crateName + " unset"));

    @Transient
    private static final Function<String, Component> LOCATION_ADD =
            crateName -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a location.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand("/crate location add " + crateName + ' '));
    @Transient
    private static final Function<String, Component> LOCATION_CLEAR =
            crateName -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to clear locations.", NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand("/crate location clear " + crateName));
    @Transient
    private static final BiFunction<BlockPos, String, Component> LOCATION_REMOVE =
            (blockPos, worldIdentifier) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to remove this crate location.",
                                    NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/crate location remove " + blockPos.getX() + ' ' + blockPos.getY()
                                    + ' ' + blockPos.getZ() + ' ' + worldIdentifier));

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

    @Column(name = "should_broadcast", nullable = false)
    @Getter
    @Setter
    protected boolean shouldBroadcast;

    @Column(nullable = false)
    @NonNull
    protected String server;

    @Getter
    @OneToMany(mappedBy = "original", orphanRemoval = true)
    protected Set<CrateContentEntity> contents;

    @Getter
    @Nullable
    @OneToOne(orphanRemoval = true)
    protected CrateKeyItemEntity key;

    @Getter
    @OneToMany(mappedBy = "original", orphanRemoval = true)
    protected Set<CrateLocationEntity> locations;

    protected CrateEntity() {}

    public CrateEntity(@NonNull final DirtCorePlugin plugin, @NonNull final String name) {
        this.name = name;
        this.displayName = name;
        this.shouldBroadcast = false;
        this.server = plugin.getServerIdentifier();
        this.contents = new HashSet<>();
        this.locations = new HashSet<>();
    }

    public void onRenderDetails(@NonNull final DirtCorePlugin plugin, @NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder) {
        builder.add(Component.empty());

        final TextComponent.Builder titleBuilder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(Component.text(" Crate: ", NamedTextColor.GRAY))
                .append(Component.text(this.name, NamedTextColor.GOLD, TextDecoration.BOLD));

        if (sender.hasPermission(Permission.CRATE_DELETE)) {
            titleBuilder.appendSpace()
                    .append(CRATE_DELETE.apply(this.name));
        }

        builder.add(titleBuilder.build())
                .add(Component.empty());

        final TextComponent.Builder displayNameBuilder = Component.text()
                .append(Component.text("Display Name: ", NamedTextColor.GOLD))
                .append(this.getDisplayNameAsComponent());

        if (sender.hasPermission(Permission.CRATE_EDIT_DISPLAY_NAME)) {
            displayNameBuilder.appendSpace()
                    .append(DISPLAY_NAME_EDIT.apply(this.name));
        }

        builder.add(displayNameBuilder.build());

        final TextComponent.Builder shouldBroadcastBuilder = Component.text()
                .append(Component.text("Should broadcast?: ", NamedTextColor.GOLD))
                .append(this.getShouldBroadcastAsComponent());

        if (sender.hasPermission(Permission.CRATE_EDIT_SHOULD_BROADCAST)) {
            shouldBroadcastBuilder.appendSpace()
                    .append(SHOULD_BROADCAST_EDIT.apply(this.name));
        }

        builder.add(shouldBroadcastBuilder.build());

        final TextComponent.Builder keyBuilder = Component.text()
                .append(Component.text("Key: ", NamedTextColor.GOLD));

        if (this.key == null) {
            keyBuilder.append(Component.text("No key set.", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.CRATE_KEY_SET)) {
                keyBuilder.appendSpace()
                        .append(KEY_SET.apply(this.name));
            }
        } else {
            final Optional<ItemStack> itemStackOptional = this.key.asItemStack(plugin);

            if (!itemStackOptional.isPresent() || itemStackOptional.get().isEmpty()) {
                keyBuilder.append(Component.text(this.key.getIdentifier(), NamedTextColor.GRAY,
                        TextDecoration.STRIKETHROUGH).hoverEvent(
                        HoverEvent.showText(Component.text("Not an item.", NamedTextColor.RED))));
            } else {
                keyBuilder.append(itemStackOptional.get().asDisplayComponent(false));
            }

            if (sender.hasPermission(Permission.CRATE_KEY_SET)) {
                keyBuilder.appendSpace()
                        .append(KEY_EDIT.apply(this.name));
            }

            if (sender.hasPermission(Permission.CRATE_KEY_UNSET)) {
                keyBuilder.appendSpace()
                        .append(KEY_UNSET.apply(this.name));
            }
        }

        builder.add(keyBuilder.build());

        final TextComponent.Builder contentsBuilder = Component.text()
                .append(Component.text("Contents: ", NamedTextColor.GOLD));

        if (this.contents.isEmpty()) {
            contentsBuilder.append(Component.text("None", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.CRATE_CONTENT_ADD)) {
                contentsBuilder.appendSpace()
                        .append(CONTENT_ADD.apply(this.name));
            }

            builder.add(contentsBuilder.build());
        } else {
            if (sender.hasPermission(Permission.CRATE_CONTENT_ADD)) {
                contentsBuilder.appendSpace()
                        .append(CONTENT_ADD.apply(this.name));
            }

            if (sender.hasPermission(Permission.CRATE_CONTENT_CLEAR)) {
                contentsBuilder.appendSpace()
                        .append(CONTENT_CLEAR.apply(this.name));
            }

            builder.add(contentsBuilder.build());

            final boolean hasCrateContentRemovePermission =
                    sender.hasPermission(Permission.CRATE_CONTENT_REMOVE);

            for (final CrateContentEntity crateContent : this.getContentsSorted()) {
                final long crateContentId = crateContent.getId();
                final TextComponent.Builder contentBuilder = Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(crateContent.getContentAsComponent(plugin, this.getTotalTickets())
                                .clickEvent(ClickEvent.runCommand(
                                        "/crate content " + this.name + " details "
                                                + crateContentId)));

                if (hasCrateContentRemovePermission) {
                    contentBuilder.appendSpace()
                            .append(CONTENT_REMOVE.apply(this.name, crateContentId));
                }

                builder.add(contentBuilder.build());
            }
        }

        final TextComponent.Builder locationsBuilder = Component.text()
                .append(Component.text("Locations: ", NamedTextColor.GOLD));

        if (this.locations.isEmpty()) {
            locationsBuilder.append(Component.text("None", NamedTextColor.GRAY));

            if (sender.hasPermission(Permission.CRATE_LOCATION_ADD)) {
                locationsBuilder.appendSpace()
                        .append(LOCATION_ADD.apply(this.name));
            }

            builder.add(locationsBuilder.build());
        } else {
            if (sender.hasPermission(Permission.CRATE_LOCATION_ADD)) {
                locationsBuilder.appendSpace()
                        .append(LOCATION_ADD.apply(this.name));
            }

            if (sender.hasPermission(Permission.CRATE_LOCATION_CLEAR)) {
                locationsBuilder.appendSpace()
                        .append(LOCATION_CLEAR.apply(this.name));
            }

            builder.add(locationsBuilder.build());

            final boolean hasCrateLocationRemovePermission =
                    sender.hasPermission(Permission.CRATE_LOCATION_REMOVE);

            for (final CrateLocationEntity crateLocation : this.locations) {
                final String worldIdentifier = crateLocation.getWorld();
                final BlockPos blockPos = crateLocation.asBlockPos();
                final TextComponent.Builder contentBuilder = Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Components.WORLD_POSITION.build(plugin, worldIdentifier, blockPos));

                if (hasCrateLocationRemovePermission) {
                    contentBuilder.appendSpace()
                            .append(LOCATION_REMOVE.apply(blockPos, worldIdentifier));
                }

                builder.add(contentBuilder.build());
            }
        }

        builder.add(Component.empty());
    }

    @NonNull
    public Component getDisplayNameAsComponent() {
        return MessagingManager.MINIMESSAGE.deserialize(this.displayName);
    }

    @NonNull
    public String getDisplayNameUnformatted() {
        return MessagingManager.minimessageToUnformattedString(this.displayName).trim();
    }

    @NonNull
    public Component getShouldBroadcastAsComponent() {
        return this.shouldBroadcast ? Component.text("Yes", NamedTextColor.GREEN)
                : Component.text("No", NamedTextColor.RED);
    }

    @NonNull
    public CrateContentEntity addContent(@NonNull final TaskContext context, final int tickets,
            final int minAmount, final int maxAmount, @NonNull final ItemStack itemStack) {
        final CrateContentEntity crateContent =
                new CrateContentEntity(this, tickets, minAmount, maxAmount);
        context.session().persist(crateContent);
        crateContent.setItem(context, itemStack);
        this.contents.add(crateContent);
        return crateContent;
    }

    public void clearContents(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.contents.forEach(session::remove);
        this.contents.clear();
    }

    @NonNull
    public Stream<String> getContentIds() {
        return this.contents.stream().map(CrateContentEntity::getId).map(l -> Long.toString(l));
    }

    @NonNull
    public List<CrateContentEntity> getContentsSorted() {
        return this.contents.stream()
                .sorted(Comparator.comparingInt(CrateContentEntity::getTickets))
                .collect(ImmutableCollectors.toList());
    }

    public boolean removeContent(@NonNull final TaskContext context, final long id) {
        for (final CrateContentEntity content : this.contents) {
            if (content.getId() == id) {
                context.session().remove(content);
                this.contents.remove(content);
                return true;
            }
        }

        return false;
    }

    public int getTotalTickets() {
        return this.contents.stream().mapToInt(CrateContentEntity::getTickets).sum();
    }

    public void setKey(@NonNull final TaskContext context, @NonNull final ItemStack itemStack) {
        if (this.key == null) {
            final CrateKeyItemEntity crateKeyItem = new CrateKeyItemEntity(this, itemStack);
            context.session().persist(crateKeyItem);
            this.key = crateKeyItem;
        } else {
            this.key.update(itemStack);
            context.session().merge(this.key);
        }
    }

    public boolean unsetKey(@NonNull final TaskContext context) {
        if (this.key == null) {
            return false;
        }

        context.session().remove(this.key);
        this.key = null;
        return true;
    }

    public void addLocation(@NonNull final TaskContext context, @NonNull final World world,
            @NonNull final Vec3i vec3i) {
        final CrateLocationEntity crateLocation = new CrateLocationEntity(this, world, vec3i);
        this.locations.add(crateLocation);
        context.session().persist(crateLocation);
    }

    public void clearLocations(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.locations.forEach(session::remove);
        this.locations.clear();
    }

    public void removeLocation(@NonNull final TaskContext context,
            @NonNull final CrateLocationEntity crateLocation) {
        this.locations.remove(crateLocation);
        context.session().remove(crateLocation);
    }
}
