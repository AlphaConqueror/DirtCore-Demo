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

package net.dirtcraft.dirtcore.common.discord.event;

import java.awt.Color;
import java.util.List;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.model.manager.messaging.MessagingManager;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

public class DiscordEventListener extends ListenerAdapter {

    private final DirtCorePlugin plugin;

    public DiscordEventListener(final DirtCorePlugin plugin) {this.plugin = plugin;}

    @Override
    public void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        if (event.getChannel().getIdLong() == this.plugin.getConfiguration()
                .get(ConfigKeys.DISCORD_GAME_CHANNEL_ID)) {
            final User user = event.getAuthor();
            final Message message = event.getMessage();

            // skip on bot messages
            if (user.isBot() || message.isWebhookMessage()) {
                return;
            }

            final Member member = event.getMember();

            if (member == null) {
                return;
            }

            final ComponentBuilder<TextComponent, TextComponent.Builder> description =
                    Component.text("Discord User:", NamedTextColor.DARK_AQUA).toBuilder()
                            .appendSpace()
                            .append(Component.text(user.getName(), NamedTextColor.AQUA));
            final List<Role> roles = member.getRoles();
            Color color = null;

            if (!roles.isEmpty()) {
                description.appendNewline()
                        .append(Component.text("Roles:", NamedTextColor.DARK_AQUA));

                for (final Role role : roles) {
                    final Color roleColor = role.getColor();

                    if (color == null) {
                        color = roleColor;
                    }

                    description.appendNewline().appendSpace()
                            .append(Component.text('-', NamedTextColor.AQUA)).appendSpace()
                            .append(Component.text(role.getName(),
                                    roleColor == null ? NamedTextColor.DARK_GRAY
                                            : TextColor.color(roleColor.getRGB())));
                }
            }

            final Component component = Component.empty()
                    .append(Component.text("[Discord] ", NamedTextColor.BLUE, TextDecoration.BOLD)
                            .hoverEvent(HoverEvent.showText(
                                    Component.text("Message from Discord", NamedTextColor.BLUE))))
                    .append(Component.text(member.getEffectiveName(),
                                    color == null ? NamedTextColor.DARK_GRAY
                                            : TextColor.color(color.getRGB()))
                            .hoverEvent(HoverEvent.showText(description.build())));
            final TextComponent.Builder contentBuilder =
                    Component.text().color(NamedTextColor.WHITE)
                            .append(Component.text(message.getContentDisplay()));

            for (final Message.Attachment attachment : message.getAttachments()) {
                final String url = attachment.getUrl();

                contentBuilder.appendSpace()
                        .append(Component.text().color(NamedTextColor.BLUE)
                                .append(Component.text('['))
                                .append(Component.text(attachment.getFileName()))
                                .append(Component.text(']')).hoverEvent(HoverEvent.showText(
                                        Component.text().color(NamedTextColor.BLUE)
                                                .append(Component.text("LINK TO:").appendNewline()
                                                        .append(Component.text(url)))))
                                .clickEvent(ClickEvent.openUrl(url)));
            }

            this.plugin.getPlatformFactory().broadcast(component.append(
                    Component.text(MessagingManager.MESSAGE_SEPARATOR, NamedTextColor.GRAY)
                            .append(contentBuilder)));
        }
    }
}
