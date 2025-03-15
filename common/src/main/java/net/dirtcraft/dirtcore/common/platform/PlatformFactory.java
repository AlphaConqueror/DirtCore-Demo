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

package net.dirtcraft.dirtcore.common.platform;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.model.minecraft.Block;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.item.ItemStack;
import net.dirtcraft.dirtcore.common.model.minecraft.item.SimpleItemStack;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.BanEntity;
import net.dirtcraft.dirtcore.common.storage.entities.punishment.KickEntity;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.UUIDs;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PlatformFactory<E, P, C, I, W, B> {

    Set<String> ATTACHMENT_EXTENSIONS =
            ImmutableSet.of("jpg", "jpeg", "png", "gif", "webp", "tiff", "svg", "apng", "webm",
                    "flv", "vob", "avi", "mov", "wmv", "amv", "mp4", "mpg", "mpeg", "gifv");
    GsonComponentSerializer GSON_COMPONENT_SERIALIZER = GsonComponentSerializer.gson();
    Pattern FORMATTING_PATTERN = Pattern.compile("(?i)§[0-9A-FK-OR]");
    Pattern URL_DEFAULT_PATTERN =
            Pattern.compile("(((https?)://)|(www\\.))([-\\w_.]+\\.\\w{2,})(/\\S*)?");
    Pattern URL_SCHEME_PATTERN = Pattern.compile("^[a-z][a-z0-9+\\-.]*:");
    TextReplacementConfig URL_TEXT_REPLACEMENT_CONFIG =
            TextReplacementConfig.builder().match(URL_DEFAULT_PATTERN).replacement(url -> {
                String clickUrl = url.content();

                if (!URL_SCHEME_PATTERN.matcher(clickUrl).find()) {
                    clickUrl = "https://" + clickUrl;
                }

                String shortName;

                try {
                    URI uri = new URI(clickUrl);
                    String host = uri.getHost();

                    if (host == null) {
                        return url;
                    }

                    String domainName = host.substring(host.lastIndexOf('.') + 1);

                    if (ATTACHMENT_EXTENSIONS.contains(domainName)) {
                        return url;
                    }

                    shortName = host;
                } catch (URISyntaxException ignored) {
                    shortName = clickUrl;
                }

                return url.content('[' + shortName + ']')
                        .style(Style.style(NamedTextColor.BLUE, TextDecoration.UNDERLINED))
                        .hoverEvent(HoverEvent.showText(Component.text().color(NamedTextColor.BLUE)
                                .append(Component.text("LINK TO:").appendNewline()
                                        .append(Component.text(clickUrl)))))
                        .clickEvent(ClickEvent.openUrl(clickUrl));
            }).build();
    // FIXME: Change mojang api -> microsoft api
    // https://wiki.vg/Mojang_API#Username_to_UUID
    String URL_USERNAME_TO_UUID = "https://api.mojang.com/users/profiles/minecraft/%s";
    // https://wiki.vg/Mojang_API#UUID_to_Profile_and_Skin.2FCape
    String URL_UUID_TO_USERNAME = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

    void broadcast(@NonNull Component message);

    void performCommand(@NonNull String command);

    @NonNull Entity wrapEntity(@NonNull E entity);

    @NonNull List<Entity> wrapEntities(@NonNull Collection<E> entities);

    @NonNull E transformEntity(@NonNull Entity entity);

    boolean isValidEntityType(@NonNull String type);

    @NonNull Collection<String> getEntityTypes();

    @NonNull Collection<String> getEntityTypesNoPlayer();

    boolean isPlayerOnline(@NonNull UUID uniqueId);

    @NonNull Optional<Player> getPlayer(@NonNull String username);

    @NonNull Optional<Player> getPlayer(@NonNull UUID uniqueId);

    @NonNull Player wrapPlayer(@NonNull P player);

    @NonNull List<Player> wrapPlayers(@NonNull Collection<P> players);

    @NonNull Collection<String> getPlayerNames();

    @NonNull Collection<UUID> getPlayerUUIDs();

    @NonNull Stream<Player> getOnlinePlayers();

    @NonNull Stream<Sender> getOnlineSenders();

    @NonNull Sender getConsoleSender();

    @NonNull Optional<Block> getBlock(@NonNull String identifier);

    @NonNull Collection<String> getBlockNames();

    @NonNull Block wrapBlock(@NonNull B block);

    @NonNull Stream<String> getModNames();

    @NonNull C transformComponent(@NonNull Component component);

    @NonNull Component transformComponent(@NonNull C component);

    @NonNull ItemStack wrapItemStack(@NonNull I itemStack);

    @NonNull I transformItemStack(@NonNull ItemStack itemStack);

    @NonNull ItemStack transformItemStack(@NonNull SimpleItemStack simpleItemStack);

    @NonNull Optional<ItemStack> createItemStack(@NonNull String identifier, int count,
            @Nullable String persistentData);

    @NonNull World wrapWorld(@NonNull W world);

    @NonNull W transformWorld(@NonNull World world);

    @NonNull Collection<World> getWorlds();

    @NonNull String componentToUnformattedString(@NonNull Component component);

    default @NonNull TransformContext getTransformContext() {
        return TransformContext.EMPTY;
    }

    default @NonNull String componentToJson(@NonNull final Component component) {
        return GSON_COMPONENT_SERIALIZER.serialize(component);
    }

    default @NonNull Component jsonToComponent(@NonNull final String json) {
        return GSON_COMPONENT_SERIALIZER.deserialize(json);
    }

    default @NonNull String stripFormatting(@NonNull final String text) {
        return FORMATTING_PATTERN.matcher(text).replaceAll("");
    }

    default @NonNull Optional<UUID> requestUniqueIdFromUsername(@NonNull final String username) {
        try {
            final String json =
                    IOUtils.toString(new URL(String.format(URL_USERNAME_TO_UUID, username)),
                            StandardCharsets.UTF_8);
            final JsonElement element = JsonParser.parseString(json);

            if (element.isJsonObject()) {
                final JsonObject root = element.getAsJsonObject();
                final JsonElement id = root.get("id");

                if (id != null && id.isJsonPrimitive()) {
                    final String s = id.getAsString();

                    return Optional.ofNullable(UUIDs.parse(s));
                }
            }
        } catch (final Exception ignored) {}

        return Optional.empty();
    }

    default @NonNull Optional<String> requestUsernameFromUniqueId(@NonNull final UUID uniqueId) {
        try {
            final String json =
                    IOUtils.toString(new URL(String.format(URL_UUID_TO_USERNAME, uniqueId)),
                            StandardCharsets.UTF_8);
            final JsonElement element = JsonParser.parseString(json);

            if (element.isJsonObject()) {
                final JsonObject root = element.getAsJsonObject();
                final JsonElement username = root.get("name");

                if (username != null && username.isJsonPrimitive()) {
                    final String s = username.getAsString();

                    return Optional.ofNullable(s);
                }
            }
        } catch (final Exception ignored) {}

        return Optional.empty();
    }

    /**
     * Gets the copy event for the current version.
     * Copy to clipboard action was only made available at 1.15.
     *
     * @return the copy action
     */
    default ClickEvent.Action copyEventAction() {
        return ClickEvent.Action.COPY_TO_CLIPBOARD;
    }

    default Components.Args2<@NonNull KickEntity, @NonNull String> kickIncidentScreenComponent() {
        return Components.KICK_SCREEN_INCIDENT;
    }

    default Components.Args2<@NonNull String, @NonNull String> kickNoIncidentScreenComponent() {
        return Components.KICK_SCREEN_NO_INCIDENT;
    }

    default Components.Args2<@NonNull BanEntity, @NonNull String> banScreenComponent() {
        return Components.BAN_SCREEN;
    }

    // FIXME: Check what this is used for.
    interface TransformContext {

        TransformContext EMPTY = new TransformContext() {};
    }
}
