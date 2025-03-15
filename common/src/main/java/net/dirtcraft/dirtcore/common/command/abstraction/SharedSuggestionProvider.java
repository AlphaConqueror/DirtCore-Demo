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

package net.dirtcraft.dirtcore.common.command.abstraction;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SharedSuggestionProvider {

    int DEFAULT_RAY_TRACE_DISTANCE = 10;
    Pattern RESOURCE_PATTERN = Pattern.compile(".+:.+");

    static CompletableFuture<Suggestions> suggestCoordinates(final String remaining,
            final Collection<TextCoordinates> coordinates, final SuggestionsBuilder builder,
            final Predicate<String> validator) {
        final List<String> list = new ArrayList<>();

        if (Strings.isNullOrEmpty(remaining)) {
            for (final TextCoordinates textCoordinates : coordinates) {
                final String s =
                        textCoordinates.x + " " + textCoordinates.y + " " + textCoordinates.z;

                if (validator.test(s)) {
                    list.add(textCoordinates.x);
                    list.add(textCoordinates.x + " " + textCoordinates.y);
                    list.add(s);
                }
            }
        } else {
            final String[] astring = remaining.split(" ");

            if (astring.length == 1) {
                for (final TextCoordinates textCoordinates : coordinates) {
                    final String s1 =
                            astring[0] + " " + textCoordinates.y + " " + textCoordinates.z;

                    if (validator.test(s1)) {
                        list.add(astring[0] + " " + textCoordinates.y);
                        list.add(s1);
                    }
                }
            } else if (astring.length == 2) {
                for (final TextCoordinates textCoordinates : coordinates) {
                    final String s2 = astring[0] + " " + astring[1] + " " + textCoordinates.z;

                    if (validator.test(s2)) {
                        list.add(s2);
                    }
                }
            }
        }

        return suggestFuture(list, builder);
    }

    static CompletableFuture<Suggestions> suggest2DCoordinates(final String remaining,
            final Collection<TextCoordinates> coordinates, final SuggestionsBuilder builder,
            final Predicate<String> validator) {
        final List<String> list = Lists.newArrayList();

        if (Strings.isNullOrEmpty(remaining)) {
            for (final TextCoordinates textCoordinates : coordinates) {
                final String s = textCoordinates.x + " " + textCoordinates.z;

                if (validator.test(s)) {
                    list.add(textCoordinates.x);
                    list.add(s);
                }
            }
        } else {
            final String[] astring = remaining.split(" ");

            if (astring.length == 1) {
                for (final TextCoordinates textCoordinates : coordinates) {
                    final String s1 = astring[0] + " " + textCoordinates.z;

                    if (validator.test(s1)) {
                        list.add(s1);
                    }
                }
            }
        }

        return suggestFuture(list, builder);
    }

    static Suggestions suggest(final Iterable<String> strings, final SuggestionsBuilder builder) {
        final String s = builder.getRemaining().toLowerCase(Locale.ROOT);
        boolean isResource = true;

        // check if all suggestions match the resource pattern
        for (final String s1 : strings) {
            if (!RESOURCE_PATTERN.matcher(s1).matches()) {
                isResource = false;
                break;
            }
        }

        if (isResource) {
            return suggestResource(strings, builder);
        }

        for (final String s1 : strings) {
            if (matchesSubStr(s, s1.toLowerCase(Locale.ROOT))) {
                builder.suggest(s1);
            }
        }

        return builder.build();
    }

    static CompletableFuture<Suggestions> suggestFuture(final Iterable<String> strings,
            final SuggestionsBuilder builder) {
        return CompletableFuture.completedFuture(suggest(strings, builder));
    }

    static Suggestions suggest(final Stream<String> strings, final SuggestionsBuilder builder) {
        return suggest(strings.collect(Collectors.toList()), builder);
    }

    static CompletableFuture<Suggestions> suggestFuture(final Stream<String> strings,
            final SuggestionsBuilder builder) {
        return CompletableFuture.completedFuture(suggest(strings, builder));
    }

    static boolean matchesSubStr(final String input, final String substring) {
        for (int k = 0; !substring.startsWith(input, k); k++) {
            final int i = substring.indexOf(46, k);
            final int j = substring.indexOf(95, k);

            if (Math.max(i, j) < 0) {
                return false;
            }

            if (i >= 0 && j >= 0) {
                k = Math.min(j, i);
            } else {
                k = i >= 0 ? i : j;
            }
        }

        return true;
    }

    static <T> void filterResources(final Iterable<T> iterable, final String remaining,
            final Function<T, String> function, final Consumer<T> consumer) {
        final boolean flag = remaining.indexOf(58) > -1;

        for (final T t : iterable) {
            final String suggestion = function.apply(t);

            if (flag) {
                if (matchesSubStr(remaining, suggestion)) {
                    consumer.accept(t);
                }
            } else {
                final String[] split = suggestion.split(":");

                if (split.length == 2 && matchesSubStr(remaining, split[1])) {
                    consumer.accept(t);
                }
            }
        }
    }

    static Suggestions suggestResource(final Iterable<String> iterable,
            final SuggestionsBuilder builder) {
        final String s = builder.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(iterable, s, Function.identity(), builder::suggest);
        return builder.build();
    }

    @NonNull Collection<TextCoordinates> getRelevantCoordinates();

    @NonNull Collection<TextCoordinates> getAbsoluteCoordinates();

    @NonNull Collection<String> getSelectedEntities();

    class TextCoordinates {

        public static final TextCoordinates DEFAULT_GLOBAL = new TextCoordinates("~", "~", "~");
        public static final TextCoordinates DEFAULT_LOCAL = new TextCoordinates("^", "^", "^");
        public final String x;
        public final String y;
        public final String z;

        public TextCoordinates(final String x, final String y, final String z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public static TextCoordinates of(final String x, final String y, final String z) {
            return new TextCoordinates(x, y, z);
        }

        public static TextCoordinates from(final int x, final int y, final int z) {
            return new TextCoordinates(Integer.toString(x), Integer.toString(y),
                    Integer.toString(z));
        }

        public static TextCoordinates from(final double x, final double y, final double z) {
            return new TextCoordinates(prettyPrint(x), prettyPrint(y), prettyPrint(z));
        }

        private static String prettyPrint(final double d) {
            return String.format(Locale.ROOT, "%.2f", d);
        }
    }
}
