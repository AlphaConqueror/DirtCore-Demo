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

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
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
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BlockPosArgument implements ArgumentType<DirtCorePlugin, Coordinates> {

    public static final SimpleCommandExceptionType ERROR_NOT_LOADED =
            new SimpleCommandExceptionType(new LiteralMessage("That position is not loaded"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD =
            new SimpleCommandExceptionType(
                    new LiteralMessage("That position is out of this world!"));
    public static final SimpleCommandExceptionType ERROR_OUT_OF_BOUNDS =
            new SimpleCommandExceptionType(
                    new LiteralMessage("That position is outside the allowed boundaries."));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5");

    public static BlockPosArgument blockPos() {
        return new BlockPosArgument();
    }

    public static BlockPos getLoadedBlockPos(
            final CommandContext<DirtCorePlugin, Sender> commandContext, final World world,
            final String pName) throws CommandSyntaxException {
        final BlockPos blockPos = getBlockPos(commandContext, pName);

        if (!world.hasChunkAt(blockPos)) {
            throw ERROR_NOT_LOADED.create();
        }

        if (!world.isInWorldBounds(blockPos)) {
            throw ERROR_OUT_OF_WORLD.create();
        }

        return blockPos;
    }

    public static Coordinates getCoordinates(
            final CommandContext<DirtCorePlugin, Sender> commandContext, final String name) {
        return commandContext.getArgument(name, Coordinates.class);
    }

    public static BlockPos getBlockPos(final CommandContext<DirtCorePlugin, Sender> commandContext,
            final String name) {
        return getCoordinates(commandContext, name).getBlockPos(commandContext.getSource());
    }

    public static BlockPos getSpawnablePos(
            final CommandContext<DirtCorePlugin, Sender> commandContext, final World world,
            final String name) throws CommandSyntaxException {
        final BlockPos blockpos = getBlockPos(commandContext, name);

        if (!world.isInSpawnableBounds(blockpos)) {
            throw ERROR_OUT_OF_BOUNDS.create();
        }

        return blockpos;
    }

    @Override
    public @NonNull Coordinates parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return reader.canRead() && reader.peek() == LocalCoordinates.PREFIX_LOCAL_COORDINATE
                ? LocalCoordinates.parse(reader) : WorldCoordinates.parseInt(reader);
    }

    @Override
    public @NonNull String getName() {
        return "blockPos";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCorePlugin plugin, final CommandContext<DirtCorePlugin, S> context,
            final SuggestionsBuilder builder) {
        final String s = builder.getRemaining();
        final Collection<SharedSuggestionProvider.TextCoordinates> collection;

        if (!s.isEmpty() && s.charAt(0) == LocalCoordinates.PREFIX_LOCAL_COORDINATE) {
            collection =
                    Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
        } else {
            collection = context.getSource().getRelevantCoordinates();
        }

        return SharedSuggestionProvider.suggestCoordinates(s, collection, builder,
                Commands.createValidator(plugin, this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
