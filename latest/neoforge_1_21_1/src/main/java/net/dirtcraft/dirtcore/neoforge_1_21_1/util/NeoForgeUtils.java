/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import java.util.Map;
import java.util.Optional;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class NeoForgeUtils {

    private static final SimpleCommandExceptionType ERROR_INVALID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid ID"));

    public static ResourceLocation read(final StringReader reader) throws CommandSyntaxException {
        final int i = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isAllowedInResourceLocation(reader.peek())) {
            reader.skip();
        }

        final String s = reader.getString().substring(i, reader.getCursor());

        try {
            return ResourceLocation.parse(s);
        } catch (final ResourceLocationException resourcelocationexception) {
            reader.setCursor(i);
            throw ERROR_INVALID.createWithContext(reader);
        }
    }

    public static HolderLookup.Provider holderLookupProvider(
            @NonNull final DirtCoreNeoForgePlugin plugin) {
        return plugin.getBootstrap().getServer()
                .map(server -> (HolderLookup.Provider) server.registryAccess())
                .orElseGet(VanillaRegistries::createLookup);
    }

    @Nullable
    public static String patchedDataToString(@NonNull final DirtCoreNeoForgePlugin plugin,
            @NonNull final DataComponentPatch dataComponentPatch) {
        if (dataComponentPatch.isEmpty()) {
            return null;
        }

        final Optional<MinecraftServer> serverOptional = plugin.getBootstrap().getServer();

        if (serverOptional.isEmpty()) {
            return null;
        }

        final JsonObject jsonObject;
        final MinecraftServer server = serverOptional.get();

        try {
            jsonObject = DataComponentPatch.CODEC.encode(dataComponentPatch,
                    server.registryAccess().createSerializationContext(JsonOps.INSTANCE),
                    new JsonObject()).getOrThrow().getAsJsonObject();
        } catch (final Exception e) {
            plugin.getLogger().warn("Error encoding data component patch: {}", e.getMessage());
            return null;
        }

        final StringBuilder stringBuilder = new StringBuilder("[");
        boolean flag = false;

        for (final Map.Entry<String, JsonElement> entry : jsonObject.asMap().entrySet()) {
            if (flag) {
                stringBuilder.append(',');
            } else {
                flag = true;
            }

            final JsonElement value = entry.getValue();

            stringBuilder.append(entry.getKey())
                    .append('=')
                    .append(value.toString());
        }

        return stringBuilder.append(']').toString();
    }

    @Nullable
    public static DataComponentPatch stringToDataComponentPatch(
            @NonNull final DirtCoreNeoForgePlugin plugin,
            final HolderLookup.@NonNull Provider provider, @NonNull final String s) {
        final StringReader reader = new StringReader(s);
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();

        try {
            new NeoForgeDataComponentParser(provider).parse(reader,
                    new NeoForgeDataComponentParser.Visitor() {

                        @Override
                        public <T> void visitComponent(
                                @NonNull final DataComponentType<T> componentType,
                                @NonNull final T t) {
                            builder.set(componentType, t);
                        }

                        @Override
                        public <T> void visitRemovedComponent(
                                @NonNull final DataComponentType<T> componentType) {
                            builder.remove(componentType);
                        }
                    });
        } catch (final CommandSyntaxException e) {
            plugin.getLogger().warn("Error encoding data component patch: {}", e.getMessage());
            return null;
        }

        return builder.build();
    }
}
