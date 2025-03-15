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

package net.dirtcraft.dirtcore.common.command.abstraction.executor;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.dirtcraft.dirtcore.common.command.abstraction.CommandDispatcher;
import net.dirtcraft.dirtcore.common.command.abstraction.ParseResults;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestion;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class BrigadierExecutor<S> implements Command<S>, SuggestionProvider<S> {

    protected abstract Sender getSender(S source);

    protected abstract DirtCorePlugin getPlugin();

    @Override
    public int run(final CommandContext<S> context) {
        final Sender sender = this.getSender(context.getSource());
        return this.getPlugin().getCommandManager().performCommand(sender, context.getInput());
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context,
            final SuggestionsBuilder builder) {
        return CompletableFuture.supplyAsync(() -> {
            final String input = builder.getInput();
            final StringReader reader = new StringReader(input);

            if (reader.canRead() && reader.peek() == '/') {
                reader.skip();
            }

            final Sender sender = this.getSender(context.getSource());
            final CommandDispatcher<DirtCorePlugin, Sender> dispatcher =
                    this.getPlugin().getCommandManager().getDispatcher();
            final ParseResults<DirtCorePlugin, Sender> parse = dispatcher.parse(reader, sender);

            // adjust suggestion builder to already existing input in current argument
            int offset = input.length();

            for (int i = input.length() - 1; i >= 0; i--) {
                final char c = input.charAt(i);

                if (c == ' ') {
                    break;
                }

                offset--;
            }

            final SuggestionsBuilder offsetBuilder = builder.createOffset(offset);
            final int argIndex = this.determineArgIndex(parse.getReader().getRemaining());

            try {
                final List<Suggestion> suggestions =
                        dispatcher.getCompletionSuggestions(parse).get(10, TimeUnit.SECONDS)
                                .getList();

                for (final Suggestion suggestion : suggestions) {
                    // suggestions consisting of multiple words (separated by a space) are
                    // being adjusted to already existing input (separated by a space) in current
                    // argument
                    final String[] suggestionArgs = suggestion.getText().trim().split(" ");
                    final int length = suggestionArgs.length;

                    // just in case
                    if (argIndex >= length) {
                        continue;
                    }

                    String suggestionText = suggestionArgs[argIndex];

                    for (int i = argIndex + 1; i < length; i++) {
                        suggestionText += ' ' + suggestionArgs[i];
                    }

                    offsetBuilder.suggest(suggestionText);
                }
            } catch (final InterruptedException | TimeoutException | ExecutionException e) {
                // sometimes suggestions can cause issues,
                // use the timeout with the input to find and fix the issues
                this.getPlugin().getLogger()
                        .severe("Failed to get suggestions for sender '{}' with input '{}'.",
                                sender.getName(), input);
            }

            return offsetBuilder.build();
        });
    }

    private int determineArgIndex(@NonNull final String s) {
        final StringReader reader = new StringReader(s);

        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
        }

        int argIndex = 0;

        while (reader.canRead()) {
            if (reader.read() == ' ') {
                argIndex++;
            }
        }

        return argIndex;
    }
}
