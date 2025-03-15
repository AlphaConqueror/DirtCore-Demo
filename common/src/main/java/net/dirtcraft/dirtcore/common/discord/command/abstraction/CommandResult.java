/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.discord.command.abstraction;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class CommandResult {

    @NonNull
    private final WebhookMessageCreateAction<Message> message;
    @Nullable
    private Runnable executeAfter;

    public CommandResult(@NonNull final WebhookMessageCreateAction<Message> message,
            @Nullable final Runnable executeAfter) {
        this.message = message;
        this.executeAfter = executeAfter;
    }

    public CommandResult(@NonNull final WebhookMessageCreateAction<Message> message) {
        this.message = message;
    }

    public @NotNull WebhookMessageCreateAction<Message> getMessage() {
        return this.message;
    }

    public void executeAfter() {
        if (this.executeAfter != null) {
            this.executeAfter.run();
        }
    }
}
