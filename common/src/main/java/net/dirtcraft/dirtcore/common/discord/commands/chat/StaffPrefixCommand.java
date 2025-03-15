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

package net.dirtcraft.dirtcore.common.discord.commands.chat;

import java.util.List;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.discord.command.builder.Commands;
import net.dirtcraft.dirtcore.common.discord.command.builder.OptionBuilder;
import net.dirtcraft.dirtcore.common.discord.command.builder.RootCommandBuilder;
import net.dirtcraft.dirtcore.common.discord.permission.DiscordPermission;
import net.dirtcraft.dirtcore.common.discord.util.DiscordEmbeds;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

// TODO: Move to global bot.
public class StaffPrefixCommand extends AbstractCommand implements Prefixable {

    private static final String DISPLAY_FULL = "full_display";
    private static final String DISPLAY_SHORT = "short_display";
    private static final String FULL_NAME = "full_name";
    private static final String NAME = "name";
    private static final String NEW_NAME = "new_name";

    private static final Supplier<OptionBuilder> OPTION_DISPLAY_FULL =
            () -> Commands.option(DISPLAY_FULL,
                    "The full display as a MiniMessage. Must be guarded by [].", OptionType.STRING);
    private static final Supplier<OptionBuilder> OPTION_DISPLAY_SHORT =
            () -> Commands.option(DISPLAY_SHORT,
                    "The short display as a MiniMessage. Must be guarded by [].",
                    OptionType.STRING);
    private static final Supplier<OptionBuilder> OPTION_FULL_NAME =
            () -> Commands.option(FULL_NAME, "The full name as a MiniMessage.", OptionType.STRING);
    private static final Supplier<OptionBuilder> OPTION_NAME =
            () -> Commands.option(NAME, "The lowercase name of the prefix without spaces.",
                    OptionType.STRING).required();
    private static final Supplier<OptionBuilder> OPTION_NEW_NAME =
            () -> Commands.option(NEW_NAME, "The new lowercase name of the prefix without spaces.",
                    OptionType.STRING);

    public StaffPrefixCommand(@NonNull final DiscordBotClient client) {
        super(client, "staff-prefix", "The staff prefix main command.");
    }

    @Override
    protected @NonNull RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.STAFF_PREFIX)
                .then(Commands.subCommand("create", "Creates a staff prefix.")
                        .then(OPTION_NAME.get())
                        .then(OPTION_FULL_NAME.get().required())
                        .then(OPTION_DISPLAY_FULL.get().required())
                        .then(OPTION_DISPLAY_SHORT.get().required())
                        .executes(this::create))
                .then(Commands.subCommand("delete", "Deletes a prefix.")
                        .then(OPTION_NAME.get())
                        .executes(this::delete))
                .then(Commands.subCommand("display", "Displays a staff prefix.")
                        .then(OPTION_NAME.get())
                        .executes(this::display))
                .then(Commands.subCommand("edit", "Edits a staff prefix.")
                        .then(OPTION_NAME.get())
                        .then(OPTION_NEW_NAME.get())
                        .then(OPTION_FULL_NAME.get())
                        .then(OPTION_DISPLAY_FULL.get())
                        .then(OPTION_DISPLAY_SHORT.get())
                        .executes(this::edit))
                .then(Commands.subCommand("list", "Lists all staff prefix names.")
                        .executes(this::list));
    }

    @NonNull
    private WebhookMessageCreateAction<Message> create(
            @NonNull final InteractionContext interactionContext) {
        final String fullDisplay =
                interactionContext.getOptionOrException(DISPLAY_FULL).getAsString();

        if (this.checkDisplayGuarded(fullDisplay)) {
            return interactionContext.getEvent().getHook().sendMessageEmbeds(
                    DiscordEmbeds.FAILURE.build(
                            String.format("Full display %s is not guarded by %s.",
                                    MarkdownUtil.monospace(fullDisplay),
                                    MarkdownUtil.monospace("[]")))).setEphemeral(true);
        }

        final String shortDisplay =
                interactionContext.getOptionOrException(DISPLAY_SHORT).getAsString();

        if (this.checkDisplayGuarded(shortDisplay)) {
            return interactionContext.getEvent().getHook().sendMessageEmbeds(
                    DiscordEmbeds.FAILURE.build(
                            String.format("Short display %s is not guarded by %s.",
                                    MarkdownUtil.monospace(shortDisplay),
                                    MarkdownUtil.monospace("[]")))).setEphemeral(true);
        }

        final String name = interactionContext.getOptionOrException(NAME).getAsString();
        final String fullName = interactionContext.getOptionOrException(FULL_NAME).getAsString();

        return this.getPlugin().getStorage().performTask(context -> {
            final MessageEmbed embed;

            if (this.getPlugin().getChatManager().staffPrefixExists(context, name)) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Staff prefix %s already exists.", MarkdownUtil.bold(name)));
            } else {
                this.client.getPlugin().getChatManager()
                        .registerStaffPrefix(context, name, fullName, fullDisplay, shortDisplay);
                embed = DiscordEmbeds.SUCCESS.build(
                        String.format("Staff prefix %s has been created.",
                                MarkdownUtil.bold(name)));
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> delete(
            @NonNull final InteractionContext interactionContext) {
        final String name = interactionContext.getOptionOrException(NAME).getAsString();

        return this.getPlugin().getStorage().performTask(context -> {
            final StaffPrefixEntity staffPrefix =
                    context.session().get(StaffPrefixEntity.class, name);
            final MessageEmbed embed;

            if (staffPrefix == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Staff prefix %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                this.client.getPlugin().getChatManager().deleteStaffPrefix(context, staffPrefix);
                embed = DiscordEmbeds.SUCCESS.build(
                        String.format("Staff prefix %s has been deleted.",
                                MarkdownUtil.bold(name)));
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> display(
            @NonNull final InteractionContext interactionContext) {
        final String name = interactionContext.getOptionOrException(NAME).getAsString();

        return this.getPlugin().getStorage().performTask(context -> {
            final StaffPrefixEntity staffPrefix =
                    context.session().get(StaffPrefixEntity.class, name);
            final MessageEmbed embed;

            if (staffPrefix == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Staff prefix %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                embed = DiscordEmbeds.STAFF_PREFIX_DISPLAY.build(this.getPlugin(), staffPrefix);
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> edit(
            @NonNull final InteractionContext interactionContext) {
        final String name = interactionContext.getOptionOrException(NAME).getAsString();

        return this.getPlugin().getStorage().performTask(context -> {
            final StaffPrefixEntity staffPrefix =
                    context.session().get(StaffPrefixEntity.class, name);
            final MessageEmbed embed;

            if (staffPrefix == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Staff prefix %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                final String newFullDisplay =
                        interactionContext.getOption(DISPLAY_FULL).map(OptionMapping::getAsString)
                                .orElse(null);

                if (newFullDisplay != null && this.checkDisplayGuarded(newFullDisplay)) {
                    return interactionContext.getEvent().getHook().sendMessageEmbeds(
                            DiscordEmbeds.FAILURE.build(
                                    String.format("Full display %s is not guarded by %s.",
                                            MarkdownUtil.monospace(newFullDisplay),
                                            MarkdownUtil.monospace("[]")))).setEphemeral(true);
                }

                final String newShortDisplay =
                        interactionContext.getOption(DISPLAY_SHORT).map(OptionMapping::getAsString)
                                .orElse(null);

                if (newShortDisplay != null && this.checkDisplayGuarded(newShortDisplay)) {
                    return interactionContext.getEvent().getHook().sendMessageEmbeds(
                            DiscordEmbeds.FAILURE.build(
                                    String.format("Short display %s is not guarded by %s.",
                                            MarkdownUtil.monospace(newShortDisplay),
                                            MarkdownUtil.monospace("[]")))).setEphemeral(true);
                }

                final String newName =
                        interactionContext.getOption(NEW_NAME).map(OptionMapping::getAsString)
                                .orElse(null);
                final String newFullName =
                        interactionContext.getOption(FULL_NAME).map(OptionMapping::getAsString)
                                .orElse(null);

                final StringBuilder builder =
                        new StringBuilder("Prefix ").append(MarkdownUtil.bold(name))
                                .append(" has been edited:");

                boolean change =
                        this.appendCondition(builder, newName, staffPrefix.getName(), "Name");
                change = this.appendCondition(builder, newFullName,
                        staffPrefix.getFullNameAsString(), "Full Name") || change;
                change = this.appendCondition(builder, newFullDisplay,
                        staffPrefix.getFullDisplayAsString(), "Full Display") || change;
                change = this.appendCondition(builder, newShortDisplay,
                        staffPrefix.getShortDisplayAsString(), "Short Display") || change;

                // FIXME
                if (change) {
                    this.getPlugin().getChatManager()
                            .editStaffPrefix(context, staffPrefix, newName, newFullName,
                                    newFullDisplay, newShortDisplay);
                    embed = DiscordEmbeds.SUCCESS.build(builder.toString());
                } else {
                    embed = DiscordEmbeds.FAILURE.build(String.format(
                            "No changes have been made. Staff prefix %s has not been modified.",
                            MarkdownUtil.bold(name)));
                }
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> list(
            @NonNull final InteractionContext interactionContext) {
        return this.getPlugin().getStorage().performTask(context -> {
            final List<String> prefixNames =
                    this.getPlugin().getChatManager().getStaffPrefixNames();
            return interactionContext.getEvent().getHook().sendMessageEmbeds(
                            DiscordEmbeds.STAFF_PREFIX_LIST.build(this.getPlugin(), prefixNames))
                    .setEphemeral(true);
        });
    }
}
