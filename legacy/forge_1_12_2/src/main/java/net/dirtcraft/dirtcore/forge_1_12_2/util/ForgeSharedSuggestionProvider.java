/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.util;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.minecraft.util.ResourceLocation;

public interface ForgeSharedSuggestionProvider {

    static <T> void filterResources(final Iterable<T> pResources, final String pInput,
            final Function<T, ResourceLocation> pLocationFunction,
            final Consumer<T> pResourceConsumer) {
        final boolean flag = pInput.indexOf(58) > -1;

        for (final T t : pResources) {
            final ResourceLocation resourcelocation = pLocationFunction.apply(t);

            if (flag) {
                final String s = resourcelocation.toString();

                if (matchesSubStr(pInput, s)) {
                    pResourceConsumer.accept(t);
                }
            } else if (matchesSubStr(pInput, resourcelocation.getNamespace())
                    || resourcelocation.getNamespace().equals("minecraft") && matchesSubStr(pInput,
                    resourcelocation.getPath())) {
                pResourceConsumer.accept(t);
            }
        }

    }

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

    static CompletableFuture<Suggestions> suggestResource(
            final Iterable<ResourceLocation> pResources, final SuggestionsBuilder pBuilder) {
        final String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(pResources, s, resourceLocation -> resourceLocation,
                resourceLocation -> pBuilder.suggest(resourceLocation.toString()));
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
