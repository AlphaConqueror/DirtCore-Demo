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

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.primitive;

import java.util.Arrays;
import java.util.Collection;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class StringArgumentType implements ArgumentType<DirtCorePlugin, String> {

    private final StringType type;

    private StringArgumentType(final StringType type) {
        this.type = type;
    }

    public static StringArgumentType word() {
        return new StringArgumentType(StringType.SINGLE_WORD);
    }

    public static StringArgumentType string() {
        return new StringArgumentType(StringType.QUOTABLE_PHRASE);
    }

    public static StringArgumentType greedyString() {
        return new StringArgumentType(StringType.GREEDY_PHRASE);
    }

    public static String getString(final CommandContext<DirtCorePlugin, ?> context,
            final String name) {
        return context.getArgument(name, String.class);
    }

    public static String escapeIfRequired(final String input) {
        for (final char c : input.toCharArray()) {
            if (!StringReader.isAllowedInUnquotedString(c)) {
                return escape(input);
            }
        }
        return input;
    }

    private static String escape(final String input) {
        final StringBuilder result = new StringBuilder("\"");

        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if (c == '\\' || c == '"') {
                result.append('\\');
            }
            result.append(c);
        }

        result.append("\"");
        return result.toString();
    }

    public StringType getType() {
        return this.type;
    }

    @Override
    public @NonNull String parse(final DirtCorePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        if (this.type == StringType.GREEDY_PHRASE) {
            final String text = reader.getRemaining();

            reader.setCursor(reader.getTotalLength());
            return text;
        } else if (this.type == StringType.SINGLE_WORD) {
            return reader.readUnquotedString();
        } else {
            return reader.readString();
        }
    }

    @Override
    public @NonNull String getName() {
        return "string";
    }

    @Override
    public Collection<String> getExamples() {
        return this.type.getExamples();
    }

    @Override
    public String toString() {
        return "string()";
    }

    public enum StringType {
        SINGLE_WORD("word", "words_with_underscores"),
        QUOTABLE_PHRASE("\"quoted phrase\"", "word", "\"\""),
        GREEDY_PHRASE("word", "words with spaces", "\"and symbols\""),
        ;

        private final Collection<String> examples;

        StringType(final String... examples) {
            this.examples = Arrays.asList(examples);
        }

        public Collection<String> getExamples() {
            return this.examples;
        }
    }
}
