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

package net.dirtcraft.dirtcore.common.command;

import java.util.Collection;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates.BlockPosArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates.ChunkPosArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates.Vec3Argument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.block.BlockResult;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.misc.SelectionArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive.BoolArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive.DoubleArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive.IntegerArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive.LongArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive.StringArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.user.UserArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.user.UserParser;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.RequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface DefaultArguments {

    int MIN_PAGE = 1;

    /*
     * GLOBAL
     */ Function<String, Argument<Boolean>> BOOLEAN =
            name -> DefaultArgument.of(name, BoolArgumentType.bool(), BoolArgumentType::getBool);
    Function<String, Argument<Double>> DOUBLE =
            name -> DefaultArgument.of(name, DoubleArgumentType.doubleArg(),
                    DoubleArgumentType::getDouble);
    Function<String, Argument<Integer>> INTEGER =
            name -> DefaultArgument.of(name, IntegerArgumentType.integer(),
                    IntegerArgumentType::getInteger);
    Function<String, Argument<Long>> LONG =
            name -> DefaultArgument.of(name, LongArgumentType.longArg(), LongArgumentType::getLong);
    BiFunction<String, Supplier<Collection<String>>, DefaultArgument<String>> SELECTION =
            (name, selection) -> DefaultArgument.of(name,
                    SelectionArgumentType.selection(selection),
                    SelectionArgumentType::getSelection);
    Function<String, Argument<String>> STRING =
            name -> DefaultArgument.of(name, StringArgumentType.greedyString(),
                    (context, s) -> StringArgumentType.getString(context, s).trim());
    Function<String, Argument<String>> WORD =
            name -> DefaultArgument.of(name, StringArgumentType.word(),
                    (context, s) -> StringArgumentType.getString(context, s).trim());
    Function<String, Argument<String>> WORD_LOWERCASE = name -> WORD.apply(name).withFunction(
            (context, s) -> StringArgumentType.getString(context, s).trim()
                    .toLowerCase(Locale.ROOT));
    /*
     * DEFAULT
     */

    Argument<BlockPos> BLOCK_POS = DefaultArgument.of("blockPos", BlockPosArgument.blockPos(),
            BlockPosArgument::getBlockPos);
    Argument<Vec2i> CHUNK_POS = DefaultArgument.of("chunkPos", ChunkPosArgument.chunkPos(),
            ChunkPosArgument::getChunkPos);
    Argument<Integer> PAGE =
            INTEGER.apply("page").withArgumentType(IntegerArgumentType.integer(MIN_PAGE));
    Argument<Vec3> POS = DefaultArgument.of("pos", Vec3Argument.vec3(), Vec3Argument::getVec3);
    Argument<UserParser.UserInformation> USER_TARGET =
            DefaultArgument.of("target", UserArgument.user(), UserArgument::getUser);
    Argument<UserParser.UserInformation> USER_TARGET_ONLINE =
            USER_TARGET.withArgumentType(UserArgument.onlineUser());

    /*
     * PLATFORM
     */

    PlatformArgument<BlockResult> BLOCK =
            factory -> DefaultArgument.of("block", factory.block(), factory::getBlock);
    PlatformArgument<ItemResult> ITEM =
            factory -> DefaultArgument.of("item", factory.item(), factory::getItem);
    PlatformArgument<Player> PLAYER_TARGET =
            factory -> DefaultArgument.of("target", factory.player(), factory::getPlayer);
    PlatformArgument<Collection<Player>> PLAYER_TARGETS =
            factory -> DefaultArgument.of("targets", factory.players(), factory::getPlayers);
    PlatformArgument<World> WORLD =
            factory -> DefaultArgument.of("world", factory.world(), factory::getWorld);

    /*
     * SPECIFIC
     */

    DefaultArguments.Argument<String> MESSAGE = DefaultArguments.STRING.apply("message");
    DefaultArguments.Argument<String> REASON = DefaultArguments.STRING.apply("reason");

    class DefaultArgument<T> implements Argument<T> {

        @NonNull
        private final String name;
        @NonNull
        private final ArgumentType<DirtCorePlugin, ?> argumentType;
        @NonNull
        private final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function;
        @Nullable
        private final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider;

        /**
         * Note: Since ArgumentType is a functional interface, you need to be careful.
         * Wrong: (p, reader) -> IntegerArgumentType.integer()
         * Right: IntegerArgumentType.integer()
         */
        private DefaultArgument(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function,
                @Nullable final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            this.name = name;
            this.argumentType = argumentType;
            this.function = function;
            this.suggestionProvider = suggestionProvider;
        }

        public static <T> DefaultArgument<T> of(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function,
                @NonNull final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            return new DefaultArgument<>(name, argumentType, function, suggestionProvider);
        }

        public static <T> DefaultArgument<T> of(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function) {
            return new DefaultArgument<>(name, argumentType, function, null);
        }

        @Override
        public @NonNull RequiredArgumentBuilder<DirtCorePlugin, Sender, ?> getArgument() {
            final RequiredArgumentBuilder<DirtCorePlugin, Sender, ?> argumentBuilder =
                    Commands.argument(this.name, this.argumentType);

            if (this.suggestionProvider != null) {
                argumentBuilder.suggests(this.suggestionProvider);
            }

            return argumentBuilder;
        }

        @Override
        public @NonNull T fromContext(
                @NonNull final CommandContext<DirtCorePlugin, Sender> commandContext) {
            return this.function.apply(commandContext, this.name);
        }

        @Override
        public @NonNull String getName() {
            return this.name;
        }

        @Override
        public @NonNull Argument<T> withName(@NonNull final String name) {
            return new DefaultArgument<>(name, this.argumentType, this.function,
                    this.suggestionProvider);
        }

        @Override
        public @NonNull ArgumentType<DirtCorePlugin, ?> getArgumentType() {
            return this.argumentType;
        }

        @Override
        public @NonNull Argument<T> withArgumentType(
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType) {
            return new DefaultArgument<>(this.name, argumentType, this.function,
                    this.suggestionProvider);
        }

        @Override
        public @NonNull BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> getFunction() {
            return this.function;
        }

        @Override
        public @NonNull <U> Argument<U> withFunction(
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, U> function) {
            return new DefaultArgument<>(this.name, this.argumentType, function,
                    this.suggestionProvider);
        }

        @Override
        public @Nullable SuggestionProvider<DirtCorePlugin, Sender> getSuggestions() {
            return this.suggestionProvider;
        }

        @Override
        public @NonNull Argument<T> withSuggestions(
                @NonNull final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            return new DefaultArgument<>(this.name, this.argumentType, this.function,
                    suggestionProvider);
        }
    }

    @FunctionalInterface
    interface PlatformArgument<T> {

        @NonNull Argument<T> fromFactory(@NonNull ArgumentFactory<DirtCorePlugin> factory);
    }

    interface Argument<T> {

        @NonNull RequiredArgumentBuilder<DirtCorePlugin, Sender, ?> getArgument();

        @NonNull T fromContext(@NonNull CommandContext<DirtCorePlugin, Sender> commandContext);

        @NonNull String getName();

        @NonNull Argument<T> withName(@NonNull String name);

        @NonNull ArgumentType<DirtCorePlugin, ?> getArgumentType();

        /**
         * Note: Since ArgumentType is a functional interface, you need to be careful.
         * Wrong: (p, reader) -> IntegerArgumentType.integer()
         * Right: IntegerArgumentType.integer()
         */
        @NonNull Argument<T> withArgumentType(
                @NonNull ArgumentType<DirtCorePlugin, ?> argumentType);

        @NonNull BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> getFunction();

        @NonNull <U> Argument<U> withFunction(
                @NonNull BiFunction<CommandContext<DirtCorePlugin, Sender>, String, U> function);

        @Nullable SuggestionProvider<DirtCorePlugin, Sender> getSuggestions();

        @NonNull Argument<T> withSuggestions(
                @NonNull SuggestionProvider<DirtCorePlugin, Sender> provider);
    }
}
