/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.executor.BrigadierExecutor;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.CommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.ForgeArgumentFactory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ForgeBrigadierExecutor extends BrigadierExecutor<CommandSourceStack> {

    // TODO: Maybe send ClientBoundCommandsPacket to players using a root command node stub to
    //  show the arg names

    private final DirtCoreForgePlugin plugin;

    public ForgeBrigadierExecutor(final DirtCoreForgePlugin plugin) {
        this.plugin = plugin;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegisterCommands(final RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // build commands before adding them
        this.plugin.buildCommands(new ForgeArgumentFactory(this.plugin, event.getBuildContext()));

        final Collection<CommandNode<DirtCorePlugin, Sender>> children =
                this.plugin.getCommandManager().getDispatcher().getRoot().getChildren();
        final Set<String> childrenNames =
                children.stream().map(CommandNode::getName).map(s -> s.toLowerCase(Locale.ROOT))
                        .collect(Collectors.toSet());

        // remove all commands with the same names before adding our own
        dispatcher.getRoot().getChildren()
                .removeIf(node -> childrenNames.contains(node.getName().toLowerCase(Locale.ROOT)));

        for (final CommandNode<DirtCorePlugin, Sender> child : children) {
            final LiteralCommandNode<CommandSourceStack> command = Commands.literal(child.getName())
                    .requires(source -> {
                        final Sender sender = this.getSender(source);

                        return child.getRequirement().test(sender) && sender.hasPermission(
                                child.getRequiredPermission());
                    })
                    .executes(this).build();
            final ArgumentCommandNode<CommandSourceStack, String> argument =
                    Commands.argument("args", StringArgumentType.greedyString()).suggests(this)
                            .executes(this).build();

            // build own command
            command.addChild(argument);
            dispatcher.getRoot().addChild(command);
        }
    }

    @Override
    protected Sender getSender(final CommandSourceStack source) {
        return this.plugin.getSenderFactory().wrap(source);
    }

    @Override
    protected DirtCorePlugin getPlugin() {
        return this.plugin;
    }
}
