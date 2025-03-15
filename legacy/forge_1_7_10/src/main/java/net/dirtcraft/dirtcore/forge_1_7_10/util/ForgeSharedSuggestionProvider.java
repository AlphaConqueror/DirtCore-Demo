/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.util;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;

public interface ForgeSharedSuggestionProvider {

    static CompletableFuture<Suggestions> suggest(final Iterable<String> pStrings,
            final SuggestionsBuilder pBuilder) {
        final String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);

        for (final String s1 : pStrings) {
            if (matchesSubStr(s, s1.toLowerCase(Locale.ROOT))) {
                pBuilder.suggest(s1);
            }
        }

        return pBuilder.buildFuture();
    }

    static boolean matchesSubStr(final String pInput, final String pSubstring) {
        for (int i = 0; !pSubstring.startsWith(pInput, i); ++i) {
            i = pSubstring.indexOf(95, i);

            if (i < 0) {
                return false;
            }
        }

        return true;
    }
}
