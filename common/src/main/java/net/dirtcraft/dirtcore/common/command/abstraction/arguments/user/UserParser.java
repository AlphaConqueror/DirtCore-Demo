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

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.user;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.User;
import net.dirtcraft.dirtcore.common.model.manager.user.UserManager;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.dirtcraft.dirtcore.common.storage.context.TaskContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class UserParser {

    public static final SimpleCommandExceptionType ERROR_INVALID_NAME_OR_UUID =
            new SimpleCommandExceptionType(new LiteralMessage("Invalid name or UUID"));

    private final DirtCorePlugin plugin;
    private final StringReader reader;
    private final boolean includeOffline;
    @Nullable
    private String userName;
    @Nullable
    private UUID userUniqueId;
    private int startPosition;
    private BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>,
            CompletableFuture<Suggestions>>
            suggestions = SuggestionsBuilder.SUGGEST_NOTHING_BI;

    public UserParser(final DirtCorePlugin plugin, final StringReader pReader,
            final boolean includeOffline) {
        this.plugin = plugin;
        this.reader = pReader;
        this.includeOffline = includeOffline;
    }

    public StringReader getReader() {
        return this.reader;
    }

    public UserParser parse() throws CommandSyntaxException {
        this.startPosition = this.reader.getCursor();
        this.parseNameOrUUID();
        return this;
    }

    public CompletableFuture<Suggestions> fillSuggestions(final SuggestionsBuilder pBuilder,
            final Consumer<SuggestionsBuilder> pConsumer) {
        return this.suggestions.apply(pBuilder.createOffset(this.reader.getCursor()), pConsumer);
    }

    public UserInformation getUserInformation() {
        return new UserInformation(this.userUniqueId, this.userName, this.includeOffline);
    }

    protected void parseNameOrUUID() throws CommandSyntaxException {
        final int i = this.reader.getCursor();
        final String s = this.reader.readString();

        this.suggestions = this::suggestNameOrUUID;

        try {
            this.userUniqueId = UUID.fromString(s);
        } catch (final IllegalArgumentException ignored) {
            if (s.isEmpty() || s.length() > 16) {
                this.reader.setCursor(i);
                throw ERROR_INVALID_NAME_OR_UUID.createWithContext(this.reader);
            }

            this.userName = s;
        }
    }

    private CompletableFuture<Suggestions> suggestNameOrUUID(final SuggestionsBuilder builder,
            final Consumer<SuggestionsBuilder> consumer) {
        final SuggestionsBuilder suggestionsbuilder = builder.createOffset(this.startPosition);

        consumer.accept(suggestionsbuilder);

        return builder.add(suggestionsbuilder).buildFuture();
    }

    public static class UserInformation {

        @Nullable
        private final UUID uniqueId;
        @Nullable
        private final String username;
        private final boolean includeOffline;

        public UserInformation(@Nullable final UUID uniqueId, @Nullable final String username,
                final boolean includeOffline) {
            this.uniqueId = uniqueId;
            this.username = username;
            this.includeOffline = includeOffline;
        }

        @NonNull
        public User getUser(@NonNull final TaskContext context) throws CommandSyntaxException {
            final UserManager<User> userManager = context.plugin().getUserManager();
            User user = null;

            if (this.username != null) {
                user = userManager.getUser(context, this.username);
            } else if (this.uniqueId != null) {
                user = userManager.getUser(context, this.uniqueId);
            }

            if (user == null || user.getUniqueId().equals(Sender.CONSOLE_UUID)) {
                throw UserArgument.NO_USER_FOUND.create();
            }

            if (!this.includeOffline && !context.plugin().getPlatformFactory()
                    .isPlayerOnline(user.getUniqueId())) {
                throw UserArgument.USER_OFFLINE.create();
            }

            return user;
        }
    }
}
