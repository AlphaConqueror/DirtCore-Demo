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
import net.dirtcraft.dirtcore.common.storage.entities.chat.ChatMarkerEntity;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
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
public class ChatMarkerCommand extends AbstractCommand implements Prefixable {

    private static final String DESCRIPTION = "description";
    private static final String DISPLAY = "display";
    private static final String NAME = "name";
    private static final String NEW_NAME = "new_name";

    private static final Supplier<OptionBuilder> OPTION_DESCRIPTION =
            () -> Commands.option(DESCRIPTION, "The description of the chat marker.",
                    OptionType.STRING);
    private static final Supplier<OptionBuilder> OPTION_DISPLAY =
            () -> Commands.option(DISPLAY, "The display as a MiniMessage.", OptionType.STRING);
    private static final Supplier<OptionBuilder> OPTION_NAME =
            () -> Commands.option(NAME, "The name of the chat marker without spaces.",
                    OptionType.STRING).required();
    private static final Supplier<OptionBuilder> OPTION_NEW_NAME =
            () -> Commands.option(NEW_NAME, "The new name of the chat marker without spaces.",
                    OptionType.STRING);

    public ChatMarkerCommand(@NonNull final DiscordBotClient client) {
        super(client, "chat-marker", "The chat marker main command.");
    }

    @Override
    protected @NonNull RootCommandBuilder build(@NotNull final RootCommandBuilder data) {
        return data.showFor(
                        DefaultMemberPermissions.enabledFor(Permission.USE_APPLICATION_COMMANDS))
                .requires(DiscordPermission.CHAT_MARKER)
                .then(Commands.subCommand("create", "Creates a chat marker.")
                        .then(OPTION_NAME.get())
                        .then(OPTION_DESCRIPTION.get().required())
                        .then(OPTION_DISPLAY.get().required())
                        .executes(this::create))
                .then(Commands.subCommand("delete", "Deletes a chat marker.")
                        .then(OPTION_NAME.get())
                        .executes(this::delete))
                .then(Commands.subCommand("display", "Displays a chat marker.")
                        .then(OPTION_NAME.get())
                        .executes(this::display))
                .then(Commands.subCommand("edit", "Edits a chat marker.")
                        .then(OPTION_NAME.get())
                        .then(OPTION_NEW_NAME.get())
                        .then(OPTION_DESCRIPTION.get())
                        .then(OPTION_DISPLAY.get())
                        .executes(this::edit))
                .then(Commands.subCommand("list", "Lists all chat marker names.")
                        .executes(this::list));
    }

    @NonNull
    private WebhookMessageCreateAction<Message> create(
            @NonNull final InteractionContext interactionContext) {
        final String name =
                this.parseName(interactionContext.getOptionOrException(NAME).getAsString());
        final String description =
                interactionContext.getOptionOrException(DESCRIPTION).getAsString();
        final String display = interactionContext.getOptionOrException(DISPLAY).getAsString();

        return this.getPlugin().getStorage().performTask(context -> {
            final MessageEmbed embed;

            if (this.getPlugin().getChatManager().chatMarkerExists(context, name)) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Chat marker %s already exists.", MarkdownUtil.bold(name)));
            } else {
                this.client.getPlugin().getChatManager()
                        .registerChatMarker(context, name, description, display);
                embed = DiscordEmbeds.SUCCESS.build(
                        String.format("Chat marker %s has been created.", MarkdownUtil.bold(name)));
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> delete(
            @NonNull final InteractionContext interactionContext) {
        final String name =
                this.parseName(interactionContext.getOptionOrException(NAME).getAsString());

        return this.getPlugin().getStorage().performTask(context -> {
            final ChatMarkerEntity chatMarker = context.session().get(ChatMarkerEntity.class, name);
            final MessageEmbed embed;

            if (chatMarker == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Chat marker %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                this.client.getPlugin().getChatManager().deleteChatMarker(context, chatMarker);
                embed = DiscordEmbeds.SUCCESS.build(
                        String.format("Chat marker %s has been deleted.", MarkdownUtil.bold(name)));
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> display(
            @NonNull final InteractionContext interactionContext) {
        final String name =
                this.parseName(interactionContext.getOptionOrException(NAME).getAsString());

        return this.getPlugin().getStorage().performTask(context -> {
            final ChatMarkerEntity chatMarker = context.session().get(ChatMarkerEntity.class, name);
            final MessageEmbed embed;

            if (chatMarker == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Chat marker %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                embed = DiscordEmbeds.CHAT_MARKER_DISPLAY.build(this.getPlugin(), chatMarker);
            }

            return interactionContext.getEvent().getHook().sendMessageEmbeds(embed)
                    .setEphemeral(true);
        });
    }

    @NonNull
    private WebhookMessageCreateAction<Message> edit(
            @NonNull final InteractionContext interactionContext) {
        final String name =
                this.parseName(interactionContext.getOptionOrException(NAME).getAsString());

        return this.getPlugin().getStorage().performTask(context -> {
            final ChatMarkerEntity chatMarker = context.session().get(ChatMarkerEntity.class, name);
            final MessageEmbed embed;

            if (chatMarker == null) {
                embed = DiscordEmbeds.FAILURE.build(
                        String.format("Chat marker %s does not exists.", MarkdownUtil.bold(name)));
            } else {
                final String newName = interactionContext.getOption(NEW_NAME)
                        .map(optionMapping -> this.parseName(optionMapping.getAsString()))
                        .orElse(null);
                final String newDescription =
                        interactionContext.getOption(DESCRIPTION).map(OptionMapping::getAsString)
                                .orElse(null);
                final String newDisplay =
                        interactionContext.getOption(DISPLAY).map(OptionMapping::getAsString)
                                .orElse(null);

                final StringBuilder builder =
                        new StringBuilder("Chat marker ").append(MarkdownUtil.bold(name))
                                .append(" has been edited:");

                boolean change =
                        this.appendCondition(builder, newName, chatMarker.getName(), "Name");
                change = this.appendCondition(builder, newDescription, chatMarker.getDescription(),
                        "Description") || change;
                change = this.appendCondition(builder, newDisplay, chatMarker.getDisplayAsString(),
                        "Display") || change;

                if (change) {
                    this.getPlugin().getChatManager()
                            .editChatMarker(context, chatMarker, newName, newDescription,
                                    newDisplay);
                    embed = DiscordEmbeds.SUCCESS.build(builder.toString());
                } else {
                    embed = DiscordEmbeds.FAILURE.build(String.format(
                            "No changes have been made. Chat manager %s has not been modified.",
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
            final List<String> markerNames =
                    this.getPlugin().getChatManager().getChatMarkerNames(context).stream().sorted()
                            .collect(ImmutableCollectors.toList());
            return interactionContext.getEvent().getHook().sendMessageEmbeds(
                            DiscordEmbeds.CHAT_MARKER_LIST.build(this.getPlugin(), markerNames))
                    .setEphemeral(true);
        });
    }
}
