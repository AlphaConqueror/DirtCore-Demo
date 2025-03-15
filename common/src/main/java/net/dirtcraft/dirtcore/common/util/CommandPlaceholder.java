/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import java.util.Optional;
import java.util.function.Predicate;
import net.dirtcraft.dirtcore.common.model.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.CheckReturnValue;

public interface CommandPlaceholder {

    @CheckReturnValue
    @NonNull
    static Args0 builder0(@NonNull final String s) {
        return new Args0(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0> Args1<A0> builder1(@NonNull final String s) {
        return new Args1<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1> Args2<A0, A1> builder2(@NonNull final String s) {
        return new Args2<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1, A2> Args3<A0, A1, A2> builder3(@NonNull final String s) {
        return new Args3<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1, A2, A3> Args4<A0, A1, A2, A3> builder4(@NonNull final String s) {
        return new Args4<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1, A2, A3, A4> Args5<A0, A1, A2, A3, A4> builder5(@NonNull final String s) {
        return new Args5<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1, A2, A3, A4, A5> Args6<A0, A1, A2, A3, A4, A5> builder6(
            @NonNull final String s) {
        return new Args6<>(s);
    }

    @CheckReturnValue
    @NonNull
    static <A0, A1, A2, A3, A4, A5, A6> Args7<A0, A1, A2, A3, A4, A5, A6> builder7(
            @NonNull final String s) {
        return new Args7<>(s);
    }

    @CheckReturnValue
    @NonNull CommandPlaceholder requires(@NonNull Predicate<Sender> requirement);

    @CheckReturnValue
    @NonNull CommandPlaceholder requiresPermission(@NonNull Permission permission);

    @CheckReturnValue
    @NonNull CommandPlaceholder append(@NonNull String s);

    abstract class Impl<T extends Impl<T>> implements CommandPlaceholder {

        protected final StringBuilder stringBuilder;
        protected Predicate<Sender> requirement;

        protected Impl(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            this.stringBuilder = stringBuilder;
            this.requirement = requirement;
        }

        protected Impl(@NonNull final String s) {
            this(new StringBuilder(s), sender -> true);
        }

        @NonNull
        protected abstract T getThis();

        @Override
        public @NonNull T requires(@NonNull final Predicate<Sender> requirement) {
            this.requirement = this.requirement.and(requirement);
            return this.getThis();
        }

        @Override
        public @NonNull T requiresPermission(@NonNull final Permission permission) {
            return this.requires(permission::isAuthorized);
        }

        @Override
        public @NonNull T append(@NonNull final String s) {
            this.stringBuilder.append(s);
            return this.getThis();
        }

        @NonNull
        protected Optional<String> testAndBuild(@NonNull final Sender sender,
                final Object... args) {
            return this.requirement.test(sender) ? Optional.of(this.build(args)) : Optional.empty();
        }

        @NonNull
        protected String build(final Object... args) {
            final String s = this.stringBuilder.toString();
            return args == null || args.length == 0 ? s : String.format(s, args);
        }
    }

    class Args0 extends Impl<Args0> {

        protected Args0(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender) {
            return this.testAndBuild(sender);
        }

        @NonNull
        public String buildSkipRequirement() {
            return this.build();
        }

        @CheckReturnValue
        @NonNull
        public <A0> Args1<A0> withArg(@NonNull final String s) {
            return new Args1<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args0 getThis() {
            return this;
        }
    }

    class Args1<A0> extends Impl<Args1<A0>> {

        protected Args1(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args1(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0) {
            return this.testAndBuild(sender, arg0);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0) {
            return this.build(arg0);
        }

        @CheckReturnValue
        @NonNull
        public <A1> Args2<A0, A1> withArg(@NonNull final String s) {
            return new Args2<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args1<A0> getThis() {
            return this;
        }
    }

    class Args2<A0, A1> extends Impl<Args2<A0, A1>> {

        protected Args2(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args2(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1) {
            return this.testAndBuild(sender, arg0, arg1);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1) {
            return this.build(arg0, arg1);
        }

        @CheckReturnValue
        @NonNull
        public <A2> Args3<A0, A1, A2> withArg(@NonNull final String s) {
            return new Args3<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args2<A0, A1> getThis() {
            return this;
        }
    }

    class Args3<A0, A1, A2> extends Impl<Args3<A0, A1, A2>> {

        protected Args3(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args3(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1,
                final A2 arg2) {
            return this.testAndBuild(sender, arg0, arg1, arg2);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1, final A2 arg2) {
            return this.build(arg0, arg1, arg2);
        }

        @CheckReturnValue
        @NonNull
        public <A3> Args4<A0, A1, A2, A3> withArg(@NonNull final String s) {
            return new Args4<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args3<A0, A1, A2> getThis() {
            return this;
        }
    }

    class Args4<A0, A1, A2, A3> extends Impl<Args4<A0, A1, A2, A3>> {

        protected Args4(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args4(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1,
                final A2 arg2, final A3 arg3) {
            return this.testAndBuild(sender, arg0, arg1, arg2, arg3);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1, final A2 arg2,
                final A3 arg3) {
            return this.build(arg0, arg1, arg2, arg3);
        }

        @CheckReturnValue
        @NonNull
        public <A4> Args5<A0, A1, A2, A3, A4> withArg(@NonNull final String s) {
            return new Args5<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args4<A0, A1, A2, A3> getThis() {
            return this;
        }
    }

    class Args5<A0, A1, A2, A3, A4> extends Impl<Args5<A0, A1, A2, A3, A4>> {

        protected Args5(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args5(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1,
                final A2 arg2, final A3 arg3, final A4 arg4) {
            return this.testAndBuild(sender, arg0, arg1, arg2, arg3, arg4);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1, final A2 arg2,
                final A3 arg3, final A4 arg4) {
            return this.build(arg0, arg1, arg2, arg3, arg4);
        }

        @CheckReturnValue
        @NonNull
        public <A5> Args6<A0, A1, A2, A3, A4, A5> withArg(@NonNull final String s) {
            return new Args6<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args5<A0, A1, A2, A3, A4> getThis() {
            return this;
        }
    }

    class Args6<A0, A1, A2, A3, A4, A5> extends Impl<Args6<A0, A1, A2, A3, A4, A5>> {

        protected Args6(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args6(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1,
                final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5) {
            return this.testAndBuild(sender, arg0, arg1, arg2, arg3, arg4, arg5);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1, final A2 arg2,
                final A3 arg3, final A4 arg4, final A5 arg5) {
            return this.build(arg0, arg1, arg2, arg3, arg4, arg5);
        }

        @CheckReturnValue
        @NonNull
        public <A6> Args7<A0, A1, A2, A3, A4, A5, A6> withArg(@NonNull final String s) {
            return new Args7<>(new StringBuilder(this.stringBuilder).append(s), this.requirement);
        }

        @Override
        protected @NonNull Args6<A0, A1, A2, A3, A4, A5> getThis() {
            return this;
        }
    }

    class Args7<A0, A1, A2, A3, A4, A5, A6> extends Impl<Args7<A0, A1, A2, A3, A4, A5, A6>> {

        protected Args7(@NonNull final StringBuilder stringBuilder,
                @NonNull final Predicate<Sender> requirement) {
            super(stringBuilder, requirement);
        }

        protected Args7(@NonNull final String s) {
            super(s);
        }

        @NonNull
        public Optional<String> build(@NonNull final Sender sender, final A0 arg0, final A1 arg1,
                final A2 arg2, final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
            return this.testAndBuild(sender, arg0, arg1, arg2, arg3, arg4, arg5, arg6);
        }

        @NonNull
        public String buildSkipRequirement(final A0 arg0, final A1 arg1, final A2 arg2,
                final A3 arg3, final A4 arg4, final A5 arg5, final A6 arg6) {
            return this.build(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
        }

        @Override
        protected @NonNull Args7<A0, A1, A2, A3, A4, A5, A6> getThis() {
            return this;
        }
    }
}
