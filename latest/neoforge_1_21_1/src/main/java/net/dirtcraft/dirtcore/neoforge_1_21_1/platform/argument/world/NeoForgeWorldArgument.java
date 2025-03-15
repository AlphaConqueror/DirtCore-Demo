/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.world;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.neoforge_1_21_1.util.NeoForgeUtils;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeWorldArgument extends AbstractWorldArgument<DirtCoreNeoForgePlugin> {

    private static final SimpleCommandExceptionType ERROR_SERVER =
            new SimpleCommandExceptionType(new LiteralMessage("Server not found"));
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

    private NeoForgeWorldArgument() {}

    public static NeoForgeWorldArgument world() {
        return new NeoForgeWorldArgument();
    }

    @Override
    public @NonNull World parse(final DirtCoreNeoForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final ResourceLocation resourceLocation = NeoForgeUtils.read(reader);
        final ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, resourceLocation);
        final ServerLevel serverLevel = plugin.getBootstrap().getServer()
                .orElseThrow(() -> ERROR_SERVER.createWithContext(reader)).getLevel(key);

        if (serverLevel == null) {
            throw ERROR_WORLD.createWithContext(reader);
        }

        return plugin.getPlatformFactory().wrapWorld(serverLevel);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreNeoForgePlugin plugin,
            final CommandContext<DirtCoreNeoForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return plugin.getBootstrap().getServer().map(server -> {
            final Registry<Level> dimensionRegistry =
                    server.registryAccess().registryOrThrow(Registries.DIMENSION);
            return NeoForgeSharedSuggestionProvider.suggestResource(
                    dimensionRegistry.registryKeySet().stream().map(ResourceKey::location),
                    builder);
        }).orElse(Suggestions.empty());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
