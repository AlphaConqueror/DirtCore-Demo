/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.abstraction;

import java.util.Map;
import net.dirtcraft.dirtcore.common.discord.command.CommandErrorException;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.OptionNode;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Contains options to be handled.
 */
public interface OptionHandler extends FunctionHandler {

    @NonNull Map<String, OptionNode> getOptions();

    /**
     * Check if options are applicable.
     */
    default void checkConditions() {
        if (this.getOptions().size() > 1 && !this.hasFunction()) {
            throw new IllegalArgumentException(
                    "Counted multiple options. These options need to be handled by their parent "
                            + "node.");
        }
    }

    /**
     * Interact with options.
     *
     * @param interactionContext the context
     * @return the resulting action
     */
    @NonNull
    default CommandResult interactOptions(@NonNull final InteractionContext interactionContext) {
        if (this.hasFunction()) {
            return new CommandResult(this.getFunction().apply(interactionContext),
                    this.getExecuteAfter());
        }

        for (final OptionMapping optionMapping : interactionContext.getEvent().getOptions()) {
            final OptionNode option = this.getOptions().get(optionMapping.getName());

            if (option != null) {
                return option.interact(interactionContext);
            }
        }

        throw new CommandErrorException("Could not find option.");
    }
}
