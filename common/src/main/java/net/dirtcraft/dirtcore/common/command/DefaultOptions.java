/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.user.UserParser;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.option.OptionRequiredArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface DefaultOptions {

    /*
     * DEFAULT
     */

    Function<String, Option> ARGUMENT_LESS = DefaultOption::of;
    RequiredOption<Vec2i> CHUNK_POS = DefaultRequiredOption.of(DefaultArguments.CHUNK_POS);
    BiFunction<String, Supplier<Collection<String>>, RequiredOption<String>> SELECTION =
            (name, selection) -> DefaultRequiredOption.of(
                    DefaultArguments.SELECTION.apply(name, selection));
    RequiredOption<Integer> PAGE = DefaultRequiredOption.of(DefaultArguments.PAGE);
    RequiredOption<Vec3> POS = DefaultRequiredOption.of(DefaultArguments.POS);
    RequiredOption<UserParser.UserInformation> USER_TARGET =
            DefaultRequiredOption.of(DefaultArguments.USER_TARGET);

    /*
     * PLATFORM
     */

    PlatformOption<World> WORLD =
            factory -> DefaultRequiredOption.of(DefaultArguments.WORLD.fromFactory(factory));

    class DefaultOption implements Option {

        @NonNull
        private final String name;

        private DefaultOption(@NonNull final String name) {
            this.name = name;
        }

        public static DefaultOption of(@NonNull final String name) {
            return new DefaultOption(name);
        }

        @Override
        public @NonNull String getName() {
            return this.name;
        }

        @Override
        public @NonNull OptionArgumentBuilder<DirtCorePlugin, Sender, ?> getOption() {
            return Commands.option(this.name);
        }

        @Override
        public boolean existsInContext(
                @NonNull final CommandContext<DirtCorePlugin, Sender> commandContext) {
            return commandContext.hasOption(this.name);
        }

        @Override
        public @NonNull DefaultOption withName(@NonNull final String name) {
            return new DefaultOption(name);
        }
    }

    class DefaultRequiredOption<T> extends DefaultOption implements RequiredOption<T> {

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
        private DefaultRequiredOption(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function,
                @Nullable final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            super(name);
            this.argumentType = argumentType;
            this.function = function;
            this.suggestionProvider = suggestionProvider;
        }

        public static <T> DefaultRequiredOption<T> of(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function,
                @NonNull final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            return new DefaultRequiredOption<>(name, argumentType, function, suggestionProvider);
        }

        public static <T> DefaultRequiredOption<T> of(@NonNull final String name,
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType,
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, T> function) {
            return new DefaultRequiredOption<>(name, argumentType, function, null);
        }

        public static <T> DefaultRequiredOption<T> of(
                final DefaultArguments.@NonNull Argument<T> argument) {
            return new DefaultRequiredOption<>(argument.getName(), argument.getArgumentType(),
                    argument.getFunction(), argument.getSuggestions());
        }

        @Override
        public @NonNull OptionRequiredArgumentBuilder<DirtCorePlugin, Sender, ?> getOption() {
            final OptionRequiredArgumentBuilder<DirtCorePlugin, Sender, ?> optionArgumentBuilder =
                    Commands.option(this.getName(), this.argumentType);

            if (this.suggestionProvider != null) {
                optionArgumentBuilder.suggests(this.suggestionProvider);
            }

            return optionArgumentBuilder;
        }

        @Override
        public @NonNull DefaultRequiredOption<T> withName(@NonNull final String name) {
            return new DefaultRequiredOption<>(name, this.argumentType, this.function,
                    this.suggestionProvider);
        }

        @Override
        public @NonNull Optional<T> fromContext(
                @NonNull final CommandContext<DirtCorePlugin, Sender> commandContext) {
            if (this.existsInContext(commandContext)) {
                return Optional.ofNullable(this.function.apply(commandContext, this.getName()));
            }

            return Optional.empty();
        }

        @Override
        public @NonNull RequiredOption<T> withArgumentType(
                @NonNull final ArgumentType<DirtCorePlugin, ?> argumentType) {
            return new DefaultRequiredOption<>(this.getName(), argumentType, this.function,
                    this.suggestionProvider);
        }

        @Override
        public @NonNull <U> RequiredOption<U> withFunction(
                @NonNull final BiFunction<CommandContext<DirtCorePlugin, Sender>, String, U> function) {
            return new DefaultRequiredOption<>(this.getName(), this.argumentType, function,
                    this.suggestionProvider);
        }

        @Override
        public @NonNull RequiredOption<T> withSuggestions(
                @NonNull final SuggestionProvider<DirtCorePlugin, Sender> suggestionProvider) {
            return new DefaultRequiredOption<>(this.getName(), this.argumentType, this.function,
                    suggestionProvider);
        }
    }

    @FunctionalInterface
    interface PlatformOption<T> {

        @NonNull RequiredOption<T> fromFactory(@NonNull ArgumentFactory<DirtCorePlugin> factory);
    }

    interface Option {

        @NonNull String getName();

        @NonNull OptionArgumentBuilder<DirtCorePlugin, Sender, ?> getOption();

        boolean existsInContext(@NonNull CommandContext<DirtCorePlugin, Sender> commandContext);

        @NonNull Option withName(@NonNull String name);
    }

    interface RequiredOption<T> extends Option {

        @NonNull Optional<T> fromContext(
                @NonNull CommandContext<DirtCorePlugin, Sender> commandContext);

        /**
         * Note: Since ArgumentType is a functional interface, you need to be careful.
         * Wrong: (p, reader) -> IntegerArgumentType.integer()
         * Right: IntegerArgumentType.integer()
         */
        @NonNull RequiredOption<T> withArgumentType(
                @NonNull ArgumentType<DirtCorePlugin, ?> argumentType);

        @NonNull <U> RequiredOption<U> withFunction(
                @NonNull BiFunction<CommandContext<DirtCorePlugin, Sender>, String, U> function);

        @NonNull RequiredOption<T> withSuggestions(
                @NonNull SuggestionProvider<DirtCorePlugin, Sender> provider);

        default T fromContextOrDefault(
                @NonNull final CommandContext<DirtCorePlugin, Sender> commandContext, final T def) {
            return this.fromContext(commandContext).orElse(def);
        }

        default @NonNull Supplier<@NonNull Optional<T>> fromContextAsSupplier(
                @NonNull final CommandContext<DirtCorePlugin, Sender> commandContext) {
            return () -> this.fromContext(commandContext);
        }
    }
}
