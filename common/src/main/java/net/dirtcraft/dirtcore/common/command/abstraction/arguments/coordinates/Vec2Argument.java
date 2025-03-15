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
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Vec2Argument implements ArgumentType<DirtCorePlugin, Coordinates> {

    public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE =
            new SimpleCommandExceptionType(
                    new LiteralMessage("Incomplete (expected 2 coordinates)"));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
    private final boolean centerCorrect;

    public Vec2Argument(final boolean centerCorrect) {
        this.centerCorrect = centerCorrect;
    }

    public static Vec2Argument vec2() {
        return new Vec2Argument(true);
    }

    public static Vec2Argument vec2(final boolean centerCorrect) {
        return new Vec2Argument(centerCorrect);
    }

    public static Coordinates getCoordinates(final CommandContext<DirtCorePlugin, Sender> pContext,
            final String pName) {
        return pContext.getArgument(pName, Coordinates.class);
    }

    public static Vec2 getVec2(final CommandContext<DirtCorePlugin, Sender> pContext,
            final String pName) {
        final Vec3 vec3 = getCoordinates(pContext, pName).getPosition(pContext.getSource());
        return Vec2.from((float) vec3.x, (float) vec3.z);
    }

    @Override
    public @NonNull Coordinates parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        if (!reader.canRead()) {
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
        }

        final int i = reader.getCursor();
        final WorldCoordinate d1 = WorldCoordinate.parseDouble(reader, this.centerCorrect);

        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();

            final WorldCoordinate d2 = WorldCoordinate.parseDouble(reader, this.centerCorrect);
            return new WorldCoordinates(d1, new WorldCoordinate(true, 0.0), d2);
        }

        reader.setCursor(i);
        throw ERROR_NOT_COMPLETE.createWithContext(reader);
    }

    @Override
    public @NonNull String getName() {
        return "vec2";
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
            collection = context.getSource().getAbsoluteCoordinates();
        }

        return SharedSuggestionProvider.suggest2DCoordinates(s, collection, builder,
                Commands.createValidator(plugin, this::parse));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
