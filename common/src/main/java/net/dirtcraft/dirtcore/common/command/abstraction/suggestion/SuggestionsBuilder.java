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

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.suggestion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;
import net.dirtcraft.dirtcore.common.command.abstraction.context.StringRange;

public class SuggestionsBuilder {

    public static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>>
            SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    public static final BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>,
            CompletableFuture<Suggestions>>
            SUGGEST_NOTHING_BI = (builder, consumer) -> builder.buildFuture();

    private final String input;
    private final String inputLowerCase;
    private final int start;
    private final String remaining;
    private final String remainingLowerCase;
    private final List<Suggestion> result = new ArrayList<>();

    public SuggestionsBuilder(final String input, final String inputLowerCase, final int start) {
        this.input = input;
        this.inputLowerCase = inputLowerCase;
        this.start = start;
        this.remaining = input.substring(start);
        this.remainingLowerCase = inputLowerCase.substring(start);
    }

    public SuggestionsBuilder(final String input, final int start) {
        this(input, input.toLowerCase(Locale.ROOT), start);
    }

    public String getInput() {
        return this.input;
    }

    public int getStart() {
        return this.start;
    }

    public String getRemaining() {
        return this.remaining;
    }

    public String getRemainingLowerCase() {
        return this.remainingLowerCase;
    }

    public Suggestions build() {
        return Suggestions.create(this.input, this.result);
    }

    public CompletableFuture<Suggestions> buildFuture() {
        return CompletableFuture.completedFuture(this.build());
    }

    public SuggestionsBuilder suggest(final String text) {
        if (text.equals(this.remaining)) {
            return this;
        }
        this.result.add(new Suggestion(StringRange.between(this.start, this.input.length()), text));
        return this;
    }

    public SuggestionsBuilder suggest(final String text, final Message tooltip) {
        if (text.equals(this.remaining)) {
            return this;
        }
        this.result.add(new Suggestion(StringRange.between(this.start, this.input.length()), text,
                tooltip));
        return this;
    }

    public SuggestionsBuilder suggest(final int value) {
        this.result.add(
                new IntegerSuggestion(StringRange.between(this.start, this.input.length()), value));
        return this;
    }

    public SuggestionsBuilder suggest(final int value, final Message tooltip) {
        this.result.add(
                new IntegerSuggestion(StringRange.between(this.start, this.input.length()), value,
                        tooltip));
        return this;
    }

    public SuggestionsBuilder add(final SuggestionsBuilder other) {
        this.result.addAll(other.result);
        return this;
    }

    public SuggestionsBuilder createOffset(final int start) {
        return new SuggestionsBuilder(this.input, this.inputLowerCase, start);
    }

    public SuggestionsBuilder restart() {
        return this.createOffset(this.start);
    }
}
