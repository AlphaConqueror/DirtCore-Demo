/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ChunkPosArgument implements ArgumentType<DirtCorePlugin, Coordinates> {

    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE =
            new SimpleCommandExceptionType(
                    new LiteralMessage("Incomplete (expected 2 coordinates)"));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("0 0", "~ ~", "1 -5", "~1 ~-2");

    public ChunkPosArgument() {}

    public static ChunkPosArgument chunkPos() {
        return new ChunkPosArgument();
    }

    public static Coordinates getCoordinates(final CommandContext<DirtCorePlugin, Sender> pContext,
            final String pName) {
        return pContext.getArgument(pName, Coordinates.class);
    }

    public static Vec2i getChunkPos(final CommandContext<DirtCorePlugin, Sender> commandContext,
            final String name) {
        return getCoordinates(commandContext, name).getChunkPos(commandContext.getSource());
    }

    @Override
    public @NonNull Coordinates parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
        }

        final int i = reader.getCursor();
        final WorldCoordinate d1 = WorldCoordinate.parseIntForChunk(reader);

        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();

            final WorldCoordinate d2 = WorldCoordinate.parseIntForChunk(reader);
            return new WorldCoordinates(d1, new WorldCoordinate(true, 0.0), d2);
        }

        reader.setCursor(i);
        throw ERROR_NOT_COMPLETE.createWithContext(reader);
    }

    @Override
    public @NonNull String getName() {
        return "chunkPos";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCorePlugin plugin, final CommandContext<DirtCorePlugin, S> context,
            final SuggestionsBuilder builder) {
        final String s = builder.getRemaining();
        final Collection<SharedSuggestionProvider.TextCoordinates> collection;

        if (!s.isEmpty() && s.charAt(0) == '^') {
            collection =
                    Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
        } else {
            collection =
                    context.getSource().getAbsoluteCoordinates().stream().map(textCoordinates -> {
                        // if the text coordinates consist of values, adjust them to chunks
                        try {
                            final int x = Integer.parseInt(textCoordinates.x);
                            final int z = Integer.parseInt(textCoordinates.z);
                            final int chunkX = World.blockToChunkCoordinate(x);
                            final int chunkZ = World.blockToChunkCoordinate(z);

                            return SharedSuggestionProvider.TextCoordinates.from(chunkX, 0, chunkZ);
                        } catch (final NumberFormatException ignored) {}

                        return textCoordinates;
                    }).collect(Collectors.toList());
        }

        return SharedSuggestionProvider.suggest2DCoordinates(s, collection, builder,
                Commands.createValidator(plugin, this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
