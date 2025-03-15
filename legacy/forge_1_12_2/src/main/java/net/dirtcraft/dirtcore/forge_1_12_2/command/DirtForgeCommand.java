/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.command;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.dirtcraft.dirtcore.common.command.abstraction.ParseResults;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestion;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DirtForgeCommand extends CommandBase {

    private final DirtCoreForgePlugin plugin;
    private final CommandNode<DirtCorePlugin, Sender> commandNode;

    public DirtForgeCommand(final DirtCoreForgePlugin plugin,
            final CommandNode<DirtCorePlugin, Sender> commandNode) {
        this.plugin = plugin;
        this.commandNode = commandNode;
    }

    @Override
    public @NonNull String getName() {
        return this.commandNode.getName();
    }

    @Override
    public @NonNull String getUsage(@NonNull final ICommandSender sender) {
        return this.commandNode.getUsageText();
    }

    @Override
    public void execute(@NonNull final MinecraftServer server, @NonNull final ICommandSender sender,
            final String @NonNull [] args) {
        final String input =
                this.getName() + (args.length == 0 ? "" : ' ' + String.join(" ", args));

        this.plugin.getCommandManager().performCommand(this.getSource(sender), input);
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean checkPermission(final @NonNull MinecraftServer server,
            final @NonNull ICommandSender sender) {
        return true;
    }

    @Override
    public @NonNull List<String> getTabCompletions(final @NonNull MinecraftServer server,
            final @NonNull ICommandSender commandSender, final String @NonNull [] args,
            @Nullable final BlockPos targetPos) {
        final Sender sender = this.getSource(commandSender);

        final String input =
                this.getName() + (args.length == 0 ? "" : ' ' + String.join(" ", args));
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
                return Collections.emptyList();
            }

            final int lastArgIndex = this.getLastArgIndex(args);

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

    private int getLastArgIndex(final String[] args) {
        // command name + space
        int index = this.getName().length() + 1;

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
