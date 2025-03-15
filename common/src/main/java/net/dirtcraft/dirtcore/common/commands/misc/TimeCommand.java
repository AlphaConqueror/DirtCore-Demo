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

package net.dirtcraft.dirtcore.common.commands.misc;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.DefaultArguments;
import net.dirtcraft.dirtcore.common.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.ConsoleUsage;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.util.Components;
import net.dirtcraft.dirtcore.common.util.ImmutableCollectors;
import net.dirtcraft.dirtcore.common.util.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class TimeCommand extends AbstractCommand<DirtCorePlugin, Sender> {

    public TimeCommand(final DirtCorePlugin plugin) {
        super(plugin);
    }

    @Override
    public ArgumentBuilder<DirtCorePlugin, Sender, ?> build(
        @NonNull final ArgumentFactory<DirtCorePlugin> factory) {
        final DefaultArguments.Argument<Long> amountArgument =
            DefaultArguments.LONG.apply("amount");
        final DefaultArguments.Argument<String> timeOfDayArgument =
            DefaultArguments.SELECTION.apply("timeOfDay", () -> TimeOfDay.IDENTIFIERS);
        final DefaultArguments.Argument<World> worldArgument =
            DefaultArguments.WORLD.fromFactory(factory);

        return Commands.literal("time").requiresPermission(Permission.TIME)
            .consoleUsage(ConsoleUsage.DENIED)
            .then(Commands.literal("add")
                .then(amountArgument.getArgument()
                    .executes(context -> this.addTime(context.getSource(),
                        amountArgument.fromContext(context)))
                    .then(worldArgument.getArgument().consoleUsage(ConsoleUsage.ALLOWED)
                        .executes(context -> this.addTime(context.getSource(),
                            amountArgument.fromContext(context),
                            worldArgument.fromContext(context))))))
            .then(Commands.literal("remove")
                .then(amountArgument.getArgument()
                    .executes(context -> this.removeTime(context.getSource(),
                        amountArgument.fromContext(context)))
                    .then(worldArgument.getArgument().consoleUsage(ConsoleUsage.ALLOWED)
                        .executes(context -> this.removeTime(context.getSource(),
                            amountArgument.fromContext(context),
                            worldArgument.fromContext(context))))))
            .then(Commands.literal("set")
                .then(amountArgument.getArgument()
                    .executes(context -> this.setTime(context.getSource(),
                        amountArgument.fromContext(context)))
                    .then(worldArgument.getArgument().consoleUsage(ConsoleUsage.ALLOWED)
                        .executes(context -> this.setTime(context.getSource(),
                            amountArgument.fromContext(context),
                            worldArgument.fromContext(context)))))
                .then(timeOfDayArgument.getArgument()
                    .executes(context -> this.setTime(context.getSource(),
                        timeOfDayArgument.fromContext(context)))
                    .then(worldArgument.getArgument().consoleUsage(ConsoleUsage.ALLOWED)
                        .executes(context -> this.setTime(context.getSource(),
                            timeOfDayArgument.fromContext(context),
                            worldArgument.fromContext(context))))));
    }

    private int addTime(@NotNull final Sender sender, final long time) {
        final World world = sender.getPlayerOrException().getWorld();
        return this.addTime(sender, time, world);
    }

    private int addTime(@NotNull final Sender sender, final long time, @NotNull final World world) {
        return this.adjustTime(sender, world, w -> w.setDayTime(w.getDayTime() + time));
    }

    private int removeTime(@NotNull final Sender sender, final long time) {
        final World world = sender.getPlayerOrException().getWorld();
        return this.removeTime(sender, time, world);
    }

    private int removeTime(@NotNull final Sender sender, final long time,
        @NotNull final World world) {
        return this.adjustTime(sender, world, w -> w.setDayTime(w.getDayTime() - time));
    }

    private int setTime(@NotNull final Sender sender, @NonNull final String timeOfDay) {
        final World world = sender.getPlayerOrException().getWorld();
        return this.setTime(sender, TimeOfDay.fromString(timeOfDay).getTime(), world);
    }

    private int setTime(@NotNull final Sender sender, @NonNull final String timeOfDay,
        @NonNull final World world) {
        return this.setTime(sender, TimeOfDay.fromString(timeOfDay).getTime(), world);
    }

    private int setTime(@NotNull final Sender sender, final long time) {
        final World world = sender.getPlayerOrException().getWorld();
        return this.setTime(sender, time, world);
    }

    private int setTime(@NotNull final Sender sender, final long time, @NonNull final World world) {
        return this.adjustTime(sender, world, w -> w.setDayTime(time));
    }

    private int adjustTime(@NotNull final Sender sender, @NotNull final World world,
        @NonNull final Function<World, Boolean> function) {
        final boolean success = function.apply(world);

        if (!success) {
            sender.sendMessage(Components.TIME_SET_FAILED.build(world.getIdentifier()));
            return Command.SINGLE_FAILURE;
        }

        sender.sendMessage(Components.TIME_SET.build(world.getDayTime(), world.getIdentifier()));
        return Command.SINGLE_SUCCESS;
    }

    private enum TimeOfDay {

        DAY(1000),
        NOON(6000),
        NIGHT(13000),
        MIDNIGHT(18000);

        @NonNull
        public static final Set<String> IDENTIFIERS =
            Arrays.stream(values()).map(TimeOfDay::getIdentifier)
                .collect(ImmutableCollectors.toSet());

        private final long time;

        TimeOfDay(final long time) {
            this.time = time;
        }

        @NonNull
        public static TimeOfDay fromString(@NonNull final String s) {
            final String lowerCase = s.toLowerCase(Locale.ROOT);

            for (final TimeOfDay value : TimeOfDay.values()) {
                if (lowerCase.equals(value.getIdentifier())) {
                    return value;
                }
            }

            throw new IllegalArgumentException(s);
        }

        @NonNull
        public String getIdentifier() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        public long getTime() {
            return this.time;
        }
    }
}
