/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.util;

import java.awt.Color;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.config.ConfigKeys;
import net.dirtcraft.dirtcore.common.event.internal.player.PlayerAchievementEvent;
import net.dirtcraft.dirtcore.common.model.Identifiable;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import net.dirtcraft.dirtcore.common.storage.entities.chat.ChatMarkerEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.PrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.chat.StaffPrefixEntity;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.MuteEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.ExpirablePunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.PunishmentEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RestrictiveAction;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.abstraction.RevertingPunishmentEntity;
import net.dirtcraft.dirtcore.common.util.FormatUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MarkdownUtil;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface DiscordEmbeds {

    Args0 AN_ERROR_OCCURRED =
            () -> new EmbedBuilder().setDescription("An error occurred during command execution.")
                    .setColor(Color.RED).build();

    Args2<DirtCorePlugin, ChatMarkerEntity> CHAT_MARKER_DISPLAY =
            (plugin, chatMarker) -> getDefaultBuilder(plugin).setTitle(
                            "Chat Marker: " + MarkdownUtil.bold(chatMarker.getName()))
                    .addField("Description", MarkdownUtil.quote(chatMarker.getDescription()), false)
                    .addField("Display", MarkdownUtil.quote(chatMarker.getDisplayAsString()), false)
                    .build();

    Args2<DirtCorePlugin, List<String>> CHAT_MARKER_LIST = (plugin, markerNames) -> {
        final EmbedBuilder builder = getDefaultBuilder(plugin).setTitle("Available Chat Markers");

        if (markerNames.isEmpty()) {
            builder.setDescription("No chat markers available!");
        } else {
            builder.setDescription(MarkdownSanitizer.escape(String.join("\n", markerNames)));
            builder.setFooter(
                    markerNames.size() + " chat marker" + (markerNames.size() == 1 ? "" : 's')
                            + " available");
        }

        return builder.build();
    };

    Args1<Permission> BOT_NO_PERMISSION = permission -> new EmbedBuilder().setDescription(
            "The bot is missing the permission `" + permission + "` to execute this "
                    + "command! Please contact an admin.").setColor(Color.RED).build();

    Args1<String> FAILURE =
            message -> new EmbedBuilder().setDescription(message).setColor(Color.RED).build();

    Args1<net.dirtcraft.dirtcore.common.discord.permission.Permission> NO_PERMISSION =
            permission -> new EmbedBuilder().setDescription(
                    "You are missing the permission `" + permission.getPermission()
                            + "` to execute this " + "command!").setColor(Color.RED).build();

    Args1<String> SUCCESS =
            message -> new EmbedBuilder().setDescription(message).setColor(Color.GREEN).build();

    Args0 THIS_SHOULDNT_HAVE_HAPPENED = () -> new EmbedBuilder().setDescription(
                    "This shouldn't have happened. Please contact the developer.").setColor(Color.RED)
            .build();

    Args1<String> WARNING =
            message -> new EmbedBuilder().setDescription(message).setColor(Color.YELLOW).build();

    // https://embed.dan.onl/?data=eyJhdXRob3IiOnsibmFtZSI6IkxPRyA%2BIEFscGhhQ29ucXVlcm9yQEFUTTkifSwiZmllbGRzIjpbeyJuYW1lIjoiVHlwZSIsInZhbHVlIjoiUHVuaXNobWVudCIsImlubGluZSI6dHJ1ZX0seyJuYW1lIjoiVGFyZ2V0IiwidmFsdWUiOiJXaGlzcGVyZWRWZWlsIiwiaW5saW5lIjp0cnVlfSx7Im5hbWUiOiJEZXNjcmlwdGlvbiIsInZhbHVlIjoidHlwZT1LSUNLIHJlYXNvbj0nWW91IGhhdmUgYmVlbiBraWNrZWQuJyJ9XSwidGh1bWJuYWlsIjoiaHR0cHM6Ly9yZW5kZXIuc2tpbm1jLm5ldC8zZC5waHA%2FdXNlcj1BbHBoYUNvbnF1ZXJvciZ2cj0tMTAmaHIwJmhyaD0yNSZhYT0maGVhZE9ubHk9dHJ1ZSZyYXRpbz01MCIsImNvbG9yIjoiIzAwYjBmNCIsImZvb3RlciI6eyJ0ZXh0IjoiQWxsIFRoZSBNb2RzIDkifSwidGltZXN0YW1wIjoxNzE0MzQ2ODM3Mjk4fQ%3D%3D
    Args4<@NonNull DirtCorePlugin, @NonNull LogEntity, @NonNull User, @Nullable Identifiable> LOG =
            (plugin, log, source, target) -> getDefaultLogBuilder(plugin, Color.decode("#a6ff98"),
                    log, source, target, true).build();

    Args6<@NonNull DirtCorePlugin, @NonNull LogEntity, @NonNull User, @Nullable User,
            @NonNull PunishmentEntity, @Nullable User>
            LOG_BANNED_USER_JOIN = (plugin, log, source, target, punishment, author) -> {
        if (!(punishment instanceof BanEntity)) {
            plugin.getLogger().warn("Provided punishment was not a ban, got {} instead.",
                    punishment.getClass().getName());
            return THIS_SHOULDNT_HAVE_HAPPENED.build();
        }

        return getDefaultLogBuilder(plugin, Color.RED, log, source, target, true).addField(
                createIncidentDetailsField(punishment, author)).build();
    };

    Args7<@NonNull DirtCorePlugin, @NonNull LogEntity, @NonNull User, @Nullable User,
            @NonNull PunishmentEntity, @Nullable User, @NonNull User>
            LOG_BANNED_IP_USER_JOIN =
            (plugin, log, source, target, punishment, author, punishmentTarget) -> {
                if (!(punishment instanceof BanEntity)) {
                    plugin.getLogger().warn("Provided punishment was not a ban, got {} instead.",
                            punishment.getClass().getName());
                    return THIS_SHOULDNT_HAVE_HAPPENED.build();
                }

                return getDefaultLogBuilder(plugin, Color.RED, log, source, target, true).addField(
                        createRelatedIncidentField(punishment, author, punishmentTarget)).build();
            };

    Args6<@NonNull DirtCorePlugin, @NonNull LogEntity, @NonNull User, @Nullable User,
            @NonNull PunishmentEntity, @Nullable User>
            LOG_MUTED_USER_CHAT = (plugin, log, source, target, punishment, author) -> {
        if (!(punishment instanceof MuteEntity)) {
            plugin.getLogger().warn("Provided punishment was not a mute, got {} instead.",
                    punishment.getClass().getName());
            return THIS_SHOULDNT_HAVE_HAPPENED.build();
        }

        final EmbedBuilder builder =
                getDefaultLogBuilder(plugin, Color.RED, log, source, target, false);
        final Optional<String> description = log.getDescription();

        if (description.isPresent()) {
            builder.addField("Additional", description.get(), false);
        } else {
            plugin.getLogger().warn("Muted user chat discord embed could not find message.");
        }

        return builder.addField(createIncidentDetailsField(punishment, author)).build();
    };

    Args6<@NonNull DirtCorePlugin, @NonNull LogEntity, @NonNull User, @Nullable User,
            @NonNull PunishmentEntity, @Nullable User>
            LOG_PUNISHMENT =
            (plugin, log, source, target, punishment, author) -> getDefaultLogBuilder(plugin,
                    Color.RED, log, source, target, true).addField(
                    createIncidentDetailsField(punishment, author)).build();

    Args6<@NonNull TaskContext, @NonNull LogEntity, @NonNull User, @Nullable User,
            @NonNull RevertingPunishmentEntity<?>, @Nullable User>
            LOG_REVERT = (context, log, source, target, revertingPunishment, author) -> {
        DirtCorePlugin plugin = context.plugin();
        return getDefaultLogBuilder(plugin, Color.RED, log, source, target, true).addField(
                        createIncidentDetailsField(revertingPunishment, author))
                .addField("Original " + "Incident",
                        formatPunishment(revertingPunishment.getOriginal(), plugin.getUserManager()
                                .getOrCreateUser(context,
                                        revertingPunishment.getOriginal().getAuthor())), false)
                .build();
    };

    Args1<PlayerAchievementEvent> PLAYER_ACHIEVEMENT = event -> {
        String description = event.getAchievementDescription();
        final String iconUrl = String.format(
                "https://render.skinmc.net/3d.php?user=%s&vr=-10&hr0&hrh=25&aa=&headOnly=true"
                        + "&ratio=50", event.getUsernameName());

        if (description == null || FormatUtils.isBlank(description)) {
            description = "Achievement Log";
        }

        return new EmbedBuilder().setColor(Color.decode("#6A0DAD")).appendDescription(
                        MarkdownUtil.bold(event.getUsernameName()) + " has made the advancement "
                                + MarkdownUtil.bold(event.getAchievementName()))
                .setFooter(description, iconUrl).build();
    };

    Args2<DirtCorePlugin, Collection<String>> PLAYER_LIST = (plugin, playerNames) -> {
        final EmbedBuilder builder = getDefaultBuilder(plugin).setTitle("Online Players");

        if (playerNames.isEmpty()) {
            builder.setDescription("There are currently no players online!");
        } else {
            builder.setDescription(MarkdownSanitizer.escape(String.join("\n", playerNames)));
            builder.setFooter(playerNames.size() + " player" + (playerNames.size() == 1 ? "" : "s")
                    + " online");
        }

        return builder.build();
    };

    Args2<DirtCorePlugin, PrefixEntity> PREFIX_DISPLAY =
            (plugin, prefix) -> getDefaultBuilder(plugin).setTitle(
                            "Prefix: " + MarkdownUtil.bold(prefix.getName()))
                    .addField("Description", MarkdownUtil.quote(prefix.getDescription()), false)
                    .addField("Display", MarkdownUtil.quote(prefix.getDisplayAsString()), false)
                    .build();

    Args2<DirtCorePlugin, List<String>> PREFIX_LIST = (plugin, prefixNames) -> {
        final EmbedBuilder builder = getDefaultBuilder(plugin).setTitle("Available Prefixes");

        if (prefixNames.isEmpty()) {
            builder.setDescription("No prefixes available!");
        } else {
            builder.setDescription(MarkdownSanitizer.escape(String.join("\n", prefixNames)));
            builder.setFooter(prefixNames.size() + " prefix" + (prefixNames.size() == 1 ? "" : "es")
                    + " available");
        }

        return builder.build();
    };

    Args1<DirtCorePlugin> SERVER_STARTING =
            plugin -> new EmbedBuilder().setColor(Color.decode("#e7b416"))
                    .appendDescription(MarkdownUtil.bold(plugin.getServerName()))
                    .appendDescription(" is starting ...").build();

    Args2<DirtCorePlugin, String> SERVER_STARTED =
            (plugin, timeFormat) -> new EmbedBuilder().setColor(Color.decode("#2dc937"))
                    .appendDescription(MarkdownUtil.bold(plugin.getServerName()))
                    .appendDescription(" is now online!")
                    .setFooter(String.format("Restart took %s!", timeFormat),
                            plugin.getServerIcon()).build();

    Args1<DirtCorePlugin> SERVER_STOPPING =
            plugin -> new EmbedBuilder().setColor(Color.decode("#cc3232"))
                    .appendDescription(MarkdownUtil.bold(plugin.getServerName()))
                    .appendDescription(" is now restarting ...").build();

    Args2<DirtCorePlugin, StaffPrefixEntity> STAFF_PREFIX_DISPLAY =
            (plugin, staffPrefix) -> getDefaultBuilder(plugin).setTitle(
                            "Prefix: " + MarkdownUtil.bold(staffPrefix.getName()))
                    .addField("Full Name", MarkdownUtil.quote(staffPrefix.getFullNameAsString()),
                            false).addField("Full Display",
                            MarkdownUtil.quote(staffPrefix.getFullDisplayAsString()), false)
                    .addField("Short Display",
                            MarkdownUtil.quote(staffPrefix.getShortDisplayAsString()), false)
                    .build();

    Args2<DirtCorePlugin, List<String>> STAFF_PREFIX_LIST = (plugin, staffPrefixNames) -> {
        final EmbedBuilder builder = getDefaultBuilder(plugin).setTitle("Available Staff Prefixes");

        if (staffPrefixNames.isEmpty()) {
            builder.setDescription("No staff prefixes available!");
        } else {
            builder.setDescription(MarkdownSanitizer.escape(String.join("\n", staffPrefixNames)));
            builder.setFooter(
                    staffPrefixNames.size() + " staff prefix" + (staffPrefixNames.size() == 1 ? ""
                            : "es") + " available");
        }

        return builder.build();
    };

    Args2<DirtCorePlugin, String> WELCOME_MESSAGE_DEFAULT =
            (plugin, name) -> getDefaultBuilder(plugin).setDescription(
                    "Welcome " + MarkdownUtil.bold(MarkdownSanitizer.escape(name)) + " to "
                            + plugin.getServerName() + '!').build();

    @NonNull
    static EmbedBuilder getDefaultBuilder(final DirtCorePlugin plugin) {
        return new EmbedBuilder().setColor(
                Color.decode(plugin.getConfiguration().get(ConfigKeys.DISCORD_EMBED_COLOR)));
    }

    @NonNull
    static EmbedBuilder getDefaultLogBuilder(@NonNull final DirtCorePlugin plugin,
            @NonNull final Color color, @NonNull final LogEntity log, @NonNull final User source,
            @Nullable final Identifiable target, final boolean printDescription) {
        final EmbedBuilder builder = new EmbedBuilder().setColor(color)
                .setAuthor("LOG > " + source.getName(), null,
                        source.isConsole() ? plugin.getConfiguration().get(ConfigKeys.TERMINAL_ICON)
                                : FormatUtils.formatMinecraftHead(source.getUniqueId().toString()))
                .addField("Type", log.getType().getText(), true);

        if (target != null) {
            builder.addField("Target", target.getName(), true)
                    .setThumbnail(FormatUtils.formatMinecraftHead(target.getName()));
        }

        log.getTitle().ifPresent(title -> builder.addField("Activity Log", title, false));

        if (printDescription) {
            log.getDescription()
                    .ifPresent(description -> builder.addField("Description", description, false));
        }

        return builder.setTimestamp(Instant.now())
                .setFooter(plugin.getServerName(), plugin.getServerIcon());
    }

    static MessageEmbed.@NonNull Field createIncidentDetailsField(
            @NonNull final RestrictiveAction restrictiveAction, @Nullable final User author) {
        return new MessageEmbed.Field("Incident Details",
                formatPunishment(restrictiveAction, author), false);
    }

    static MessageEmbed.@NonNull Field createRelatedIncidentField(
            @NonNull final PunishmentEntity punishment, @Nullable final User author,
            @Nullable final User target) {
        return new MessageEmbed.Field("Related Incident",
                formatPunishment(punishment, author, target), false);
    }

    @NonNull
    static String formatPunishment(@NonNull final PunishmentEntity punishment,
            @Nullable final User author, @Nullable final User target) {
        String prefix = "";

        if (target != null) {
            prefix = MarkdownUtil.bold("Target") + ": " + target.getName() + '\n';
        }

        return prefix + formatPunishment(punishment, author);
    }

    @NonNull
    static String formatPunishment(@NonNull final RestrictiveAction restrictiveAction,
            @Nullable final User author) {
        final StringBuilder descriptionBuilder =
                new StringBuilder().append(MarkdownUtil.bold("Code"))
                        .append(": ")
                        .append(restrictiveAction.getIncidentId())
                        .append('\n')
                        .append(MarkdownUtil.bold("Author"))
                        .append(": ")
                        .append(author == null ? restrictiveAction.getAuthor().toString()
                                : author.getName());

        if (restrictiveAction instanceof ExpirablePunishmentEntity) {
            final ExpirablePunishmentEntity<?, ?> expirablePunishment =
                    (ExpirablePunishmentEntity<?, ?>) restrictiveAction;

            descriptionBuilder.append('\n')
                    .append(MarkdownUtil.bold("Duration"))
                    .append(": ")
                    .append(expirablePunishment.getExpiry()
                            .map(expiry -> TimeFormat.RELATIVE.format(expiry.toInstant()))
                            .orElse("Permanent"));
        }

        if (restrictiveAction instanceof BanEntity) {
            final BanEntity banEntity = (BanEntity) restrictiveAction;

            descriptionBuilder.append('\n')
                    .append(MarkdownUtil.bold("IP-Banned"))
                    .append(": ")
                    .append(banEntity.isIpBanned());
        }

        descriptionBuilder.append('\n')
                .append(MarkdownUtil.bold("Issued"))
                .append(": ")
                .append(TimeFormat.RELATIVE.format(restrictiveAction.getTimestamp().toInstant()))
                .append('\n')
                .append(MarkdownUtil.bold("Reason"))
                .append(":\n>>> ")
                .append(restrictiveAction.getReason());

        return descriptionBuilder.toString();
    }

    interface Args0 {

        MessageEmbed build();
    }

    interface Args1<A0> {

        MessageEmbed build(A0 arg0);
    }

    interface Args2<A0, A1> {

        MessageEmbed build(A0 arg0, A1 arg1);
    }

    interface Args3<A0, A1, A2> {

        MessageEmbed build(A0 arg0, A1 arg1, A2 arg2);
    }

    interface Args4<A0, A1, A2, A3> {

        MessageEmbed build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);
    }

    interface Args5<A0, A1, A2, A3, A4> {

        MessageEmbed build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {

        MessageEmbed build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);
    }

    interface Args7<A0, A1, A2, A3, A4, A5, A6> {

        MessageEmbed build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5, A6 arg6);
    }
}
