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
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class Vec3Argument implements ArgumentType<DirtCorePlugin, Coordinates> {

    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE =
            new SimpleCommandExceptionType(
                    new LiteralMessage("Incomplete (expected 3 coordinates)"));
    public static final SimpleCommandExceptionType ERROR_MIXED_TYPE =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Cannot mix world & local coordinates (everything must either use ^ or not)"));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5");
    private final boolean centerCorrect;

    public Vec3Argument(final boolean pCenterCorrect) {
        this.centerCorrect = pCenterCorrect;
    }

    public static Vec3Argument vec3() {
        return new Vec3Argument(true);
    }

    public static Vec3Argument vec3(final boolean pCenterCorrect) {
        return new Vec3Argument(pCenterCorrect);
    }

    public static Coordinates getCoordinates(final CommandContext<DirtCorePlugin, Sender> pContext,
            final String pName) {
        return pContext.getArgument(pName, Coordinates.class);
    }

    public static Vec3 getVec3(final CommandContext<DirtCorePlugin, Sender> commandContext,
            final String name) {
        return getCoordinates(commandContext, name).getPosition(commandContext.getSource());
    }

    @NotNull
    @Override
    public Coordinates parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return reader.canRead() && reader.peek() == LocalCoordinates.PREFIX_LOCAL_COORDINATE
                ? LocalCoordinates.parse(reader)
                : WorldCoordinates.parseDouble(reader, this.centerCorrect);
    }

    @Override
    public @NonNull String getName() {
        return "vec3";
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
            collection = context.getSource().getAbsoluteCoordinates();
        }

        return SharedSuggestionProvider.suggestCoordinates(s, collection, builder,
                Commands.createValidator(plugin, this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
