/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.command;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.ParseResults;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestion;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class DirtForgeCommand extends CommandBase {

    private final DirtCoreForgePlugin plugin;
    private final CommandNode<DirtCorePlugin, Sender> commandNode;

    public DirtForgeCommand(final DirtCoreForgePlugin plugin,
            final CommandNode<DirtCorePlugin, Sender> commandNode) {
        this.plugin = plugin;
        this.commandNode = commandNode;
    }

    @Override
    public String getCommandName() {
        return this.commandNode.getName();
    }

    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return this.commandNode.getUsageText();
    }

    @Override
    public void processCommand(final ICommandSender commandSender, final String[] args) {
        final String input =
                this.getCommandName() + (args.length == 0 ? "" : ' ' + String.join(" ", args));

        this.plugin.getCommandManager().performCommand(this.getSource(commandSender), input);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(final ICommandSender commandSender,
            final String[] args) {
        final Sender sender = this.getSource(commandSender);

        final String input =
                this.getCommandName() + (args.length == 0 ? "" : ' ' + String.join(" ", args));
        final StringReader stringreader = new StringReader(input);
        final ParseResults<DirtCorePlugin, Sender> parse =
                this.plugin.getCommandManager().getDispatcher().parse(stringreader, sender);

        try {
            final List<Suggestion> suggestions =
                    this.plugin.getCommandManager().getDispatcher().getCompletionSuggestions(parse)
                            .get().getList();
            final String lastArg = args[args.length - 1];

            // prevents reset of tab completion when no more suggestions are available
            if (suggestions.stream().anyMatch(suggestion -> suggestion.getText().equals(lastArg))) {
                return null;
            }

            final int lastArgIndex = this.getLastArgIndex(args);

            // cuts down last argument to start index of suggestion
            // uses Math#max for suggestions containing space
            // cuts down suggestion text to first space
            return suggestions.stream().map(suggestion -> lastArg.substring(0,
                    Math.max(0, suggestion.getRange().getStart() - lastArgIndex))
                    + suggestion.getText().split(" ", 2)[0]).collect(Collectors.toList());
        } catch (final InterruptedException | ExecutionException e) {
            return null;
        }
    }

    private int getLastArgIndex(final String[] args) {
        // command name + space
        int index = this.getCommandName().length() + 1;

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

    private Sender getSource(final ICommandSender sender) {
        return this.plugin.getSenderFactory().wrap(sender);
    }
}
