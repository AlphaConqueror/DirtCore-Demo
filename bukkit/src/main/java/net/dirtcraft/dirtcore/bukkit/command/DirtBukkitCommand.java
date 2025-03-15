/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.bukkit.DirtCoreBukkitPlugin;
import net.dirtcraft.dirtcore.common.command.abstraction.ParseResults;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestion;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.platform.sender.Sender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class DirtBukkitCommand extends Command {

    private final DirtCoreBukkitPlugin plugin;

    public DirtBukkitCommand(@NonNull final DirtCoreBukkitPlugin plugin,
            @NonNull final CommandNode<Sender> commandNode) {
        super(commandNode.getName(), "", commandNode.getUsageText(), Collections.emptyList());
        this.plugin = plugin;
    }

    @Override
    public boolean execute(@NotNull final CommandSender sender, @NotNull final String commandLabel,
            @NotNull final String[] args) {
        final String input = commandLabel + (args.length == 0 ? "" : ' ' + String.join(" ", args));

        this.plugin.getCommandManager().performCommand(this.getSource(sender), input);
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull final CommandSender commandSender,
            @NotNull final String alias, @NotNull final String[] args) {
        final Sender sender = this.getSource(commandSender);

        final String input = alias + (args.length == 0 ? "" : ' ' + String.join(" ", args));
        final StringReader stringreader = new StringReader(input);
        final ParseResults<Sender> parse =
                this.plugin.getCommandManager().getDispatcher().parse(stringreader, sender);

        try {
            final List<Suggestion> suggestions =
                    this.plugin.getCommandManager().getDispatcher().getCompletionSuggestions(parse)
                            .get().getList();
            final String lastArg = args[args.length - 1];

            // prevents reset of tab completion when no more suggestions are available
            if (suggestions.stream().anyMatch(suggestion -> suggestion.getText().equals(lastArg))) {
                return Collections.emptyList();
            }

            final int lastArgIndex = this.getLastArgIndex(alias, args);

            // cuts down last argument to start index of suggestion
            // uses Math#max for suggestions containing space
            // cuts down suggestion text to first space
            return suggestions.stream().map(suggestion -> lastArg.substring(0,
                    Math.max(0, suggestion.getRange().getStart() - lastArgIndex))
                    + suggestion.getText().split(" ", 2)[0]).collect(Collectors.toList());
        } catch (final InterruptedException | ExecutionException e) {
            return Collections.emptyList();
        }
    }

    private int getLastArgIndex(final String label, final String[] args) {
        // label + space
        int index = label.length() + 1;

        for (int i = 0; i < args.length - 1; i++) {
            final String arg = args[i];

            if (arg.isEmpty()) {
                break;
            }

            // arg + space
            index += arg.length() + 1;
        }

        return index;
    }

    private Sender getSource(final CommandSender sender) {
        return this.plugin.getSenderFactory().wrap(sender);
    }
}
