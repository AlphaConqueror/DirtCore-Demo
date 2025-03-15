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

import com.google.common.collect.ImmutableSet;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.dirtcraft.dirtcore.common.command.abstraction.AbstractCommand;
import net.dirtcraft.dirtcore.common.command.abstraction.Command;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.Commands;
import net.dirtcraft.dirtcore.common.command.abstraction.ParseResults;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.builder.ArgumentBuilder;
import net.dirtcraft.dirtcore.common.command.abstraction.context.ParsedCommandNode;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.commands.misc.DiscordCommand;
import net.dirtcraft.dirtcore.common.commands.misc.StoreCommand;
import net.dirtcraft.dirtcore.common.commands.misc.TimeCommand;
import net.dirtcraft.dirtcore.common.commands.misc.VoteCommand;
import net.dirtcraft.dirtcore.common.commands.teleport.TeleportChunkCommand;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.platform.argument.ArgumentFactory;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Root command manager for the '/dirtcore' command.
 */
public class CommandManager {

    private static final Set<Class<? extends AbstractCommand<DirtCorePlugin, Sender>>>
            commandClasses =
            ImmutableSet.of(DiscordCommand.class, StoreCommand.class, TeleportChunkCommand.class,
                    TimeCommand.class, VoteCommand.class);

    private final DirtCorePlugin plugin;
    private final CommandDispatcher<DirtCorePlugin, Sender> dispatcher;

    public CommandManager(final DirtCorePlugin plugin) {
        this.plugin = plugin;
        this.dispatcher = new CommandDispatcher<>(plugin);
    }

    /**
     * Runs a command.
     *
     * @return The success value of the command, or 0 if an exception occurred.
     */
    public int performCommand(final Sender source, final String rawCommandLine) {
        this.plugin.getBootstrap().getScheduler().executeAsync(() -> {
            final StringReader reader = new StringReader(rawCommandLine);

            if (reader.canRead() && reader.peek() == '/') {
                reader.skip();
            }

            final String commandLine = reader.getRemaining();

            // FIXME: Move to event listener.
            this.plugin.getLogger()
                    .info("{} issued server command: /{}", source.getName(), commandLine);

            final ParseResults<DirtCorePlugin, Sender> parse =
                    this.dispatcher.parse(reader, source);

            try {
                this.dispatcher.execute(parse);
            } catch (final CommandSyntaxException e) {
                source.sendMessage(Component.text("ERROR: ")
                        .append(Component.text(e.getRawMessage().getString()))
                        .color(NamedTextColor.RED));

                final String input = e.getInput();
                final int cursor = e.getCursor();
                final int end = parse.getContext().getRange().getEnd();
                final int usageCursor = 0 < end && end < cursor ? end : cursor;

                if (input != null && cursor >= 0) {
                    final int j = Math.min(input.length(), cursor);
                    final TextComponent.Builder primaryBuilder =
                            Component.text("/").color(NamedTextColor.GRAY)
                                    .clickEvent(ClickEvent.suggestCommand('/' + commandLine))
                                    .toBuilder();

                    if (j > 10) {
                        primaryBuilder.append(Component.text("..."));
                    }

                    primaryBuilder.append(Component.text(input.substring(Math.max(0, j - 10), j)));

                    final TextComponent.Builder secondaryBuilder = Component.text()
                            .append(primaryBuilder.build());

                    if (j < input.length()) {
                        secondaryBuilder.append(
                                Component.text(input.substring(j), NamedTextColor.RED,
                                        TextDecoration.UNDERLINED));
                    }

                    secondaryBuilder.append(
                            Component.text("<--[HERE]", NamedTextColor.RED, TextDecoration.ITALIC));
                    source.sendMessage(secondaryBuilder.build());

                    final List<ParsedCommandNode<DirtCorePlugin, Sender>> nodes =
                            parse.getContext().getNodes();

                    if (!nodes.isEmpty()) {
                        final ParsedCommandNode<DirtCorePlugin, Sender> parsedCommandNode =
                                nodes.get(nodes.size() - 1);
                        final CommandNode<DirtCorePlugin, Sender> node =
                                parsedCommandNode.getNode();
                        final List<String> usages = this.dispatcher.getSmartUsage(node, source);

                        if (!usages.isEmpty()) {
                            final int k = Math.min(input.length(), usageCursor);

                            final TextComponent.Builder usagePrimaryBuilder =
                                    Component.text("/").color(NamedTextColor.GRAY).clickEvent(
                                            ClickEvent.suggestCommand(
                                                    '/' + input.substring(0, k) + ' ')).toBuilder();

                            if (k > 10) {
                                usagePrimaryBuilder.append(Component.text("..."));
                            }

                            final String prevInput = input.substring(Math.max(0, k - 10), k);

                            usagePrimaryBuilder.append(Component.text(prevInput));
                            source.sendMessage(Component.empty());

                            if (usages.size() == 1) {
                                final TextComponent.Builder usageBuilder =
                                        Component.text().color(NamedTextColor.RED)
                                                .append(Component.text("Usage: "))
                                                .append(usagePrimaryBuilder);

                                if (!prevInput.endsWith(" ")) {
                                    usageBuilder.appendSpace();
                                }

                                source.sendMessage(
                                        usageBuilder.append(Component.text(usages.get(0))).build());
                            } else {
                                source.sendMessage(Component.text("Usages:", NamedTextColor.RED));

                                final TextComponent.Builder usageBuilder =
                                        Component.text().color(NamedTextColor.RED)
                                                .append(Component.text("- "))
                                                .append(usagePrimaryBuilder);

                                if (!prevInput.endsWith(" ")) {
                                    usageBuilder.appendSpace();
                                }

                                for (final String usage : usages) {
                                    source.sendMessage(usageBuilder.build()
                                            .append(Component.text(usage)));
                                }
                            }
                        }
                    }
                }
            } catch (final Exception exception) {
                this.plugin.getLogger().severe("Command exception: /" + rawCommandLine, exception);

                final Component failure = Component.text(
                        "An unexpected error occurred trying to execute that command").hoverEvent(
                        HoverEvent.showText(Component.text(
                                exception.getMessage() == null ? exception.getClass().getName()
                                        : exception.getMessage()))).color(NamedTextColor.RED);

                source.sendMessage(failure);
            }
        });


        return Command.SINGLE_SUCCESS;
    }

    @NonNull
    public CommandDispatcher<DirtCorePlugin, Sender> getDispatcher() {
        return this.dispatcher;
    }

    public void buildCommands(final ArgumentFactory<? extends DirtCorePlugin> argumentFactory) {
        final Set<AbstractCommand<DirtCorePlugin, Sender>> commands = new HashSet<>();

        commandClasses.forEach(c -> {
            try {
                final AbstractCommand<DirtCorePlugin, Sender> abstractCommand =
                        c.getConstructor(DirtCorePlugin.class).newInstance(this.plugin);

                commands.add(abstractCommand);
            } catch (final InstantiationException | IllegalAccessException |
                           InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        this.dispatcher.findAmbiguities((parent, child, sibling, inputs) -> this.plugin.getLogger()
                .warn("Ambiguity between arguments {} and {} with inputs: {}",
                        this.dispatcher.getPath(child), this.dispatcher.getPath(sibling), inputs));

        final String base = DirtCorePlugin.MOD_ID;

        commands.forEach(command -> {
            @SuppressWarnings("unchecked") final ArgumentBuilder<DirtCorePlugin, Sender, ?>
                    builtCommand = command.build((ArgumentFactory<DirtCorePlugin>) argumentFactory);

            this.dispatcher.register(builtCommand);
            this.dispatcher.register(Commands.literal(base)
                    .then(builtCommand));
        });
    }
}
