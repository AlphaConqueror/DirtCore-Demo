/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.abstraction;

import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
public interface CommandFunction {

    @NonNull WebhookMessageCreateAction<Message> apply(
            @NonNull InteractionContext interactionContext);
}
