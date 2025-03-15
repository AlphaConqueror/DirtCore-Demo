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

package net.dirtcraft.dirtcore.common.discord.command.builder.node;

import net.dirtcraft.dirtcore.common.discord.DiscordBotClient;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandResult;
import net.dirtcraft.dirtcore.common.discord.permission.NoPermissionException;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.entities.User;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommandNode<T> {

    @NonNull
    protected final String name;
    @NonNull
    protected final String description;
    @NonNull
    protected final Permission permission;

    protected AbstractCommandNode(@NonNull final String name, @NonNull final String description,
            @NonNull final Permission permission) {
        this.name = name;
        this.description = description;
        this.permission = permission;
    }

    @NonNull
    public abstract T create();

    /**
     * Executed on slash interaction.
     *
     * @param interactionContext the interaction context
     */
    protected abstract CommandResult onInteraction(
            @NonNull final InteractionContext interactionContext);

    public CommandResult interact(@NonNull final InteractionContext interactionContext) {
        this.checkPermission(interactionContext.getClient(),
                interactionContext.getEvent().getUser(), this.permission);
        return this.onInteraction(interactionContext);
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    public String getDescription() {
        return this.description;
    }

    @NotNull
    public Permission getPermission() {
        return this.permission;
    }

    protected void checkPermission(final @NonNull DiscordBotClient client, @NonNull final User user,
            final Permission permission) throws NoPermissionException {
        if (!this.canUse(client, user, permission)) {
            throw new NoPermissionException(permission);
        }
    }

    protected boolean canUse(final @NonNull DiscordBotClient client, @NonNull final User user,
            final Permission permission) {
        return client.getPermissionManager().hasPermission(user, permission);
    }
}
