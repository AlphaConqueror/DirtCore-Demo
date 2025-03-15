/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.platform;

import com.google.common.collect.ImmutableSet;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.platform.PlatformFactory;
import net.dirtcraft.dirtcore.common.platform.sender.Sender;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class BukkitPlatformFactory implements PlatformFactory<BaseComponent[]> {

    private static final Pattern DEFAULT_URL_PATTERN =
            Pattern.compile("(?:(https?)://)?([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    private static final Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
    private static final Set<String> ATTACHMENT_EXTENSIONS =
            ImmutableSet.of("jpg", "jpeg", "png", "gif", "webp", "tiff", "svg", "apng", "webm",
                    "flv", "vob", "avi", "mov", "wmv", "amv", "mp4", "mpg", "mpeg", "gifv");
    private static final BungeeComponentSerializer BUNGEE_COMPONENT_SERIALIZER =
            BungeeComponentSerializer.legacy();
    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER =
            LegacyComponentSerializer.legacySection();
    private static final TextReplacementConfig TEXT_REPLACEMENT_CONFIG =
            TextReplacementConfig.builder().match(DEFAULT_URL_PATTERN).replacement(url -> {
                String clickUrl = url.content();

                if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                    clickUrl = "http://" + clickUrl;
                }

                String shortName;

                try {
                    final URI uri = new URI(clickUrl);
                    final String host = uri.getHost();

                    if (host == null) {
                        return url;
                    }

                    String domainName = host.substring(host.lastIndexOf('.') + 1);

                    if (ATTACHMENT_EXTENSIONS.contains(domainName)) {
                        return url;
                    }

                    shortName = host;
                } catch (final URISyntaxException ignored) {
                    shortName = clickUrl;
                }

                return url.content('[' + shortName + ']')
                        .style(Style.style(NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                        .hoverEvent(HoverEvent.showText(Component.text().color(NamedTextColor.BLUE)
                                .append(Component.text("LINK TO:").appendNewline()
                                        .append(Component.text(clickUrl)))))
                        .clickEvent(ClickEvent.openUrl(clickUrl));
            }).build();

    @NonNull
    private final DirtCoreBukkitPlugin plugin;
    private final BukkitPlayerFactory playerFactory;
    private final ClickEvent.Action copyEventAction;

    public BukkitPlatformFactory(@NotNull final DirtCoreBukkitPlugin plugin) {
        this.plugin = plugin;
        this.playerFactory = new BukkitPlayerFactory(plugin);
        this.copyEventAction = this.checkCopyActionAvailable() ? ClickEvent.Action.COPY_TO_CLIPBOARD
                : ClickEvent.Action.SUGGEST_COMMAND;
    }

    @Override
    public void broadcast(@NonNull final Component message) {
        this.plugin.getBootstrap().getServer().spigot().broadcast(this.transformComponent(message));
    }

    @Override
    public void performCommand(@NonNull final String command) {
        this.plugin.getBootstrap().getServer()
                .dispatchCommand(this.plugin.getBootstrap().getConsole(), command);
    }

    @Override
    public boolean isPlayerOnline(@NonNull final UUID uuid) {
        return this.plugin.getBootstrap().getServer().getPlayer(uuid) != null;
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final String username) {
        final org.bukkit.entity.Player player =
                this.plugin.getBootstrap().getServer().getPlayer(username);

        return player == null ? Optional.empty() : Optional.of(this.playerFactory.wrap(player));
    }

    @Override
    public @NonNull Optional<Player> getPlayer(@NonNull final UUID uniqueId) {
        final org.bukkit.entity.Player player =
                this.plugin.getBootstrap().getServer().getPlayer(uniqueId);

        return player == null ? Optional.empty() : Optional.of(this.playerFactory.wrap(player));
    }

    @Override
    public @NotNull Collection<String> getPlayerNames() {
        return this.plugin.getBootstrap().getServer().getOnlinePlayers().stream()
                .map(HumanEntity::getName).collect(ImmutableCollectors.toSet());
    }

    @Override
    public @NonNull Collection<UUID> getPlayerUUIDs() {
        return this.plugin.getBootstrap().getServer().getOnlinePlayers().stream()
                .map(Entity::getUniqueId).collect(ImmutableCollectors.toSet());
    }

    @Override
    public @NonNull Stream<Player> getOnlinePlayers() {
        return this.plugin.getBootstrap().getServer().getOnlinePlayers().stream()
                .map(this.playerFactory::wrap);
    }

    @Override
    public @NonNull Stream<Sender> getOnlineSenders() {
        return Stream.concat(Stream.of(this.getConsoleSender()),
                this.plugin.getBootstrap().getServer().getOnlinePlayers().stream()
                        .map(player -> this.plugin.getSenderFactory().wrap(player)));
    }

    @Override
    public @NonNull Sender getConsoleSender() {
        return this.plugin.getSenderFactory().wrap(this.plugin.getBootstrap().getConsole());
    }

    @Override
    public @NonNull Optional<Block> getBlock(@NonNull final String identifier) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NonNull Collection<String> getBlockNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BaseComponent @NonNull [] transformComponent(@NonNull final Component component) {
        return BUNGEE_COMPONENT_SERIALIZER.serialize(this.extractURLs(component));
    }

    @NonNull
    public String transformToString(@NonNull final Component component) {
        return LEGACY_COMPONENT_SERIALIZER.serialize(this.extractURLs(component));
    }

    @Override
    public ClickEvent.Action copyEventAction() {
        return this.copyEventAction;
    }

    @NonNull
    private Component extractURLs(@NonNull final Component component) {
        return component.replaceText(TEXT_REPLACEMENT_CONFIG);
    }

    private boolean checkCopyActionAvailable() {
        try {
            net.md_5.bungee.api.chat.ClickEvent.Action.valueOf("COPY_TO_CLIPBOARD");
        } catch (final IllegalArgumentException ignored) {
            return false;
        }

        return true;
    }
}
