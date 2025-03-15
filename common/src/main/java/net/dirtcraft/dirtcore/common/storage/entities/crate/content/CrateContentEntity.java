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

package net.dirtcraft.dirtcore.common.storage.entities.crate.content;

import com.google.common.collect.ImmutableList;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.DirtCoreEntity;
import net.dirtcraft.dirtcore.common.storage.entities.crate.CrateEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.Permission;
import net.dirtcraft.dirtcore.common.util.TriFunction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.Session;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.jetbrains.annotations.NotNull;

@Entity
@Table(name = DirtCoreEntity.TABLE_PREFIX + "crate_contents")
public class CrateContentEntity implements Comparable<CrateContentEntity>, DirtCoreEntity {

    @Transient
    private static final BiFunction<String, Long, Component> COMMAND_ADD =
            (crateName, contentId) -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add a command.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " "
                                    + "command add "));
    @Transient
    private static final BiFunction<String, Long, Component> COMMAND_CLEAR =
            (crateName, contentId) -> Components.CLEAR.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to clear all command.", NamedTextColor.RED))).clickEvent(
                    ClickEvent.runCommand("/crate content " + crateName + " edit " + contentId + " "
                            + "command clear"));
    @Transient
    private static final TriFunction<String, Long, Long, Component> COMMAND_REMOVE =
            (crateName, contentId, commandId) -> Components.CLEAR.build().hoverEvent(
                            HoverEvent.showText(
                                    Component.text("Click to remove this command.",
                                            NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/crate content " + crateName + " edit " + contentId + " "
                                    + "command remove " + commandId));

    @Transient
    private static final BiFunction<String, Long, Component> CONTENT_REMOVE =
            (crateName, contentId) -> Components.REMOVE.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to remove this crate content.",
                                    NamedTextColor.RED)))
                    .clickEvent(ClickEvent.runCommand(
                            "/crate content " + crateName + " remove " + contentId));

    @Transient
    private static final BiFunction<String, Long, Component> GIVE_ITEM_EDIT =
            (crateName, contentId) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit if the item should be given.",
                            NamedTextColor.GOLD))).clickEvent(ClickEvent.suggestCommand(
                    "/crate content " + crateName + " edit " + contentId + " giveItem "));

    @Transient
    private static final BiFunction<String, Long, Component> ITEM_ADD =
            (crateName, contentId) -> Components.ADD.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to add the item.", NamedTextColor.GREEN))).clickEvent(
                    ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " item "));
    @Transient
    private static final BiFunction<String, Long, Component> ITEM_EDIT =
            (crateName, contentId) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                    Component.text("Click to edit the item.", NamedTextColor.GOLD))).clickEvent(
                    ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " item "));

    @Transient
    private static final BiFunction<String, Long, Component> MAX_AMOUNT_EDIT =
            (crateName, contentId) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the max. amount of items.",
                                    NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " maxAmount "));

    @Transient
    private static final BiFunction<String, Long, Component> MIN_AMOUNT_EDIT =
            (crateName, contentId) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the min. amount of items.",
                                    NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " minAmount "));

    @Transient
    private static final BiFunction<String, Long, Component> TICKETS_EDIT =
            (crateName, contentId) -> Components.EDIT.build().hoverEvent(HoverEvent.showText(
                            Component.text("Click to edit the amount of tickets.",
                                    NamedTextColor.GOLD)))
                    .clickEvent(ClickEvent.suggestCommand(
                            "/crate content " + crateName + " edit " + contentId + " tickets "));

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    protected long id;

    @ManyToOne
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    protected CrateEntity original;

    @Column(nullable = false)
    @Getter
    @Setter
    protected int tickets;

    @Column(name = "min_amount", nullable = false)
    @Getter
    @Setter
    protected int minAmount;

    @Column(name = "max_amount", nullable = false)
    @Getter
    @Setter
    protected int maxAmount;

    @Column(name = "give_item", nullable = false)
    @Getter
    @Setter
    protected boolean giveItem;

    @Getter
    @Nullable
    @OneToOne(mappedBy = "original", orphanRemoval = true)
    protected CrateContentItemEntity item;

    @Getter
    @NonNull
    @OneToMany(mappedBy = "original", orphanRemoval = true)
    protected Set<CrateContentCommandEntity> commands;

    protected CrateContentEntity() {}

    public CrateContentEntity(@NonNull final CrateEntity original, final int tickets,
            final int minAmount, final int maxAmount) {
        this.original = original;
        this.tickets = tickets;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.giveItem = true;
        this.commands = new HashSet<>();
    }

    public void onRenderDetails(@NonNull final DirtCorePlugin plugin, @NonNull final Sender sender,
            final ImmutableList.@NonNull Builder<Component> builder, final int totalTickets) {
        builder.add(Component.empty());

        final String crateName = this.original.getName();
        final TextComponent.Builder titleBuilder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_GRAY, TextDecoration.BOLD))
                .append(Component.text(" Crate Content: ", NamedTextColor.GRAY))
                .append(Component.text()
                        .append(Component.text(crateName, NamedTextColor.GOLD, TextDecoration.BOLD))
                        .append(Component.text()
                                .append(Component.text('#'))
                                .append(Component.text(this.id,
                                        Style.style(TextDecoration.BOLD)))));

        if (sender.hasPermission(Permission.CRATE_CONTENT_REMOVE)) {
            titleBuilder.appendSpace()
                    .append(CONTENT_REMOVE.apply(crateName, this.id));
        }

        builder.add(titleBuilder.build())
                .add(Component.empty());

        final TextComponent.Builder itemBuilder = Component.text()
                .append(Component.text("Item: ", NamedTextColor.GOLD));
        final boolean hasContentEditPermission =
                sender.hasPermission(Permission.CRATE_CONTENT_EDIT);

        if (this.item == null) {
            itemBuilder.append(Component.text("No item set.", NamedTextColor.RED));

            if (hasContentEditPermission) {
                itemBuilder.appendSpace()
                        .append(ITEM_ADD.apply(crateName, this.id));
            }
        } else {
            final Optional<ItemStack> itemStackOptional = this.item.asItemStack(plugin);

            if (!itemStackOptional.isPresent() || itemStackOptional.get().isEmpty()) {
                itemBuilder.append(Component.text(this.item.getIdentifier(), NamedTextColor.GRAY,
                        TextDecoration.STRIKETHROUGH).hoverEvent(
                        HoverEvent.showText(Component.text("Not an item.", NamedTextColor.RED))));
            } else {
                itemBuilder.append(itemStackOptional.get().asDisplayComponent(false));
            }

            if (hasContentEditPermission) {
                itemBuilder.appendSpace()
                        .append(ITEM_EDIT.apply(crateName, this.id));
            }
        }

        builder.add(itemBuilder.build());

        final TextComponent.Builder ticketsBuilder = Component.text()
                .append(Component.text("Tickets: ", NamedTextColor.GOLD))
                .append(Component.text().color(NamedTextColor.GRAY)
                        .append(Component.text(this.tickets))
                        .append(Component.text(" out of ", NamedTextColor.GOLD))
                        .append(Component.text(totalTickets)));

        if (hasContentEditPermission) {
            ticketsBuilder.appendSpace()
                    .append(TICKETS_EDIT.apply(crateName, this.id));
        }

        builder.add(ticketsBuilder.build());

        final TextComponent.Builder minAmountBuilder = Component.text()
                .append(Component.text("Min. Amount: ", NamedTextColor.GOLD))
                .append(Component.text(this.minAmount, NamedTextColor.GRAY));

        if (hasContentEditPermission) {
            minAmountBuilder.appendSpace()
                    .append(MIN_AMOUNT_EDIT.apply(crateName, this.id));
        }

        builder.add(minAmountBuilder.build());

        final TextComponent.Builder maxAmountBuilder = Component.text()
                .append(Component.text("Max. Amount: ", NamedTextColor.GOLD))
                .append(Component.text(this.maxAmount, NamedTextColor.GRAY));

        if (hasContentEditPermission) {
            maxAmountBuilder.appendSpace()
                    .append(MAX_AMOUNT_EDIT.apply(crateName, this.id));
        }

        builder.add(maxAmountBuilder.build());

        final TextComponent.Builder giveItemBuilder = Component.text()
                .append(Component.text("Give Item?: ", NamedTextColor.GOLD))
                .append(Component.text(this.giveItem, NamedTextColor.GRAY));

        if (hasContentEditPermission) {
            giveItemBuilder.appendSpace()
                    .append(GIVE_ITEM_EDIT.apply(crateName, this.id));
        }

        builder.add(giveItemBuilder.build());

        final TextComponent.Builder commandsBuilder = Component.text()
                .append(Component.text("Commands: ", NamedTextColor.GOLD));
        final boolean hasContentEditCommandsPermission =
                sender.hasPermission(Permission.CRATE_CONTENT_EDIT_COMMANDS);

        if (this.commands.isEmpty()) {
            commandsBuilder.append(Component.text("None", NamedTextColor.GRAY));

            if (hasContentEditCommandsPermission) {
                commandsBuilder.appendSpace()
                        .append(COMMAND_ADD.apply(crateName, this.id));
            }

            builder.add(commandsBuilder.build());
        } else {
            if (hasContentEditCommandsPermission) {
                commandsBuilder.append(COMMAND_ADD.apply(crateName, this.id))
                        .append(COMMAND_CLEAR.apply(crateName, this.id));
            }

            builder.add(commandsBuilder.build());

            for (final CrateContentCommandEntity crateContentCommand : this.commands) {
                final String commandLine = '/' + crateContentCommand.getCommand();
                final TextComponent.Builder commandBuilder = Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(commandLine, NamedTextColor.GRAY).hoverEvent(
                                        HoverEvent.showText(
                                                Component.text("Click to copy.",
                                                        NamedTextColor.DARK_GRAY)))
                                .clickEvent(ClickEvent.clickEvent(
                                        plugin.getPlatformFactory().copyEventAction(),
                                        commandLine)));

                if (hasContentEditCommandsPermission) {
                    commandsBuilder.appendSpace()
                            .append(COMMAND_REMOVE.apply(crateName, this.id,
                                    crateContentCommand.getId()));
                }

                builder.add(commandBuilder.build());
            }
        }

        builder.add(Component.empty());
    }

    public void addCommand(@NonNull final TaskContext context, @NonNull final String command) {
        final CrateContentCommandEntity contentCommand =
                new CrateContentCommandEntity(this, command);
        context.session().persist(contentCommand);
        this.commands.add(contentCommand);
    }

    public void clearCommands(@NonNull final TaskContext context) {
        final Session session = context.session();
        this.commands.forEach(session::remove);
        this.commands.clear();
    }

    @NonNull
    public Stream<String> getCommandIds() {
        return this.commands.stream().map(CrateContentCommandEntity::getId)
                .map(l -> Long.toString(l));
    }

    public boolean removeCommand(@NonNull final TaskContext context, final long id) {
        final Session session = context.session();

        for (final CrateContentCommandEntity command : this.commands) {
            if (command.getId() == id) {
                session.remove(command);
                this.commands.remove(command);
                return true;
            }
        }

        return false;
    }

    @NonNull
    public Component getContentAsComponent(@NonNull final DirtCorePlugin plugin,
            final int totalTickets) {
        final List<Component> contentComponents = this.getContentComponents(totalTickets);

        if (this.item == null) {
            return Component.text("No item set.", NamedTextColor.RED)
                    .hoverEvent(this.contentComponentsToHoverEvent(contentComponents));
        }

        final Optional<ItemStack> itemStackOptional = this.item.asItemStack(plugin);

        if (!itemStackOptional.isPresent() || itemStackOptional.get().isEmpty()) {
            return Component.text(this.item.getIdentifier(), NamedTextColor.RED,
                            TextDecoration.STRIKETHROUGH)
                    .hoverEvent(this.contentComponentsToHoverEvent(contentComponents));
        }

        final ItemStack itemStack = itemStackOptional.get();
        final List<Component> loreComponents = new ArrayList<>();

        loreComponents.add(Component.empty());
        loreComponents.addAll(contentComponents);
        loreComponents.add(Component.empty());
        itemStack.appendLore(loreComponents);

        return itemStack.asDisplayComponent(false);
    }

    public void setItem(@NonNull final TaskContext context, @NonNull final ItemStack itemStack) {
        if (this.item == null) {
            final CrateContentItemEntity crateContentItem =
                    new CrateContentItemEntity(this, itemStack);
            context.session().persist(crateContentItem);
            this.item = crateContentItem;
        } else {
            this.item.update(itemStack);
            context.session().merge(this.item);
        }
    }

    @Override
    public int compareTo(@NotNull final CrateContentEntity other) {
        return Long.compare(this.id, other.id);
    }

    @NonNull
    public Optional<ItemStack> getPreviewItemStack(@NonNull final DirtCorePlugin plugin,
            @NonNull final DecimalFormat decimalFormat, final int totalTickets) {
        if (this.item == null) {
            return Optional.empty();
        }

        return this.item.asItemStack(plugin).map(itemStack -> {
            itemStack.appendLore(this.getPreviewComponents(decimalFormat, totalTickets));
            return itemStack;
        });
    }

    @NonNull
    private List<Component> getContentComponents(final int totalTickets) {
        final List<Component> list = new ArrayList<>();

        list.add(Component.text()
                .append(Component.text("ID: ", NamedTextColor.GOLD))
                .append(Component.text(this.id, NamedTextColor.GRAY)).build());
        list.add(Component.text()
                .append(Component.text("Tickets: ", NamedTextColor.GOLD))
                .append(Component.text().color(NamedTextColor.GRAY)
                        .append(Component.text(this.tickets))
                        .append(Component.text(" out of ", NamedTextColor.GOLD))
                        .append(Component.text(totalTickets))).build());
        list.add(Component.text()
                .append(Component.text("Min. Amount: ", NamedTextColor.GOLD))
                .append(Component.text(this.minAmount, NamedTextColor.GRAY)).build());
        list.add(Component.text()
                .append(Component.text("Max. Amount: ", NamedTextColor.GOLD))
                .append(Component.text(this.maxAmount, NamedTextColor.GRAY)).build());
        list.add(Component.text()
                .append(Component.text("Give Item?: ", NamedTextColor.GOLD))
                .append(Component.text(this.giveItem, NamedTextColor.GRAY)).build());

        final TextComponent.Builder commandsBuilder = Component.text()
                .append(Component.text("Commands: ", NamedTextColor.GOLD));

        if (this.commands.isEmpty()) {
            commandsBuilder.append(Component.text("None", NamedTextColor.GRAY));
            list.add(commandsBuilder.build());
        } else {
            list.add(commandsBuilder.build());

            for (final CrateContentCommandEntity crateContentCommand : this.commands) {
                final String commandLine = '/' + crateContentCommand.getCommand();

                list.add(Component.text()
                        .append(Component.text("- ", NamedTextColor.DARK_GRAY))
                        .append(Component.text(commandLine, NamedTextColor.GRAY)).build());
            }
        }

        list.add(Component.empty());
        list.add(Component.text("Click to show details.", NamedTextColor.DARK_GRAY));
        return list;
    }

    @NonNull
    private List<Component> getPreviewComponents(@NonNull final DecimalFormat decimalFormat,
            final int totalTickets) {
        final List<Component> list = new ArrayList<>();

        list.add(Component.empty());
        list.add(Component.text()
                .append(Component.text("Chance: ", NamedTextColor.GOLD))
                .append(Component.text().color(NamedTextColor.YELLOW)
                        .append(Component.text(decimalFormat.format(
                                (((double) this.tickets) / totalTickets) * 100)))
                        .append(Component.text('%'))).build());

        final TextComponent.Builder amountBuilder = Component.text().color(NamedTextColor.YELLOW)
                .append(Component.text(this.minAmount));

        if (this.maxAmount > this.minAmount) {
            amountBuilder.append(Component.text(" - ", NamedTextColor.DARK_GRAY))
                    .append(Component.text(this.maxAmount));
        }

        list.add(Component.text()
                .append(Component.text("Amount: ", NamedTextColor.GOLD))
                .append(amountBuilder).build());
        return list;
    }

    @NonNull
    private HoverEvent<Component> contentComponentsToHoverEvent(
            @NonNull final List<Component> contentComponents) {
        final TextComponent.Builder builder = Component.text();

        for (int i = 0; i < contentComponents.size(); i++) {
            if (i > 0) {
                builder.appendNewline();
            }

            builder.append(contentComponents.get(i));
        }

        return HoverEvent.showText(builder.build());
    }
}
