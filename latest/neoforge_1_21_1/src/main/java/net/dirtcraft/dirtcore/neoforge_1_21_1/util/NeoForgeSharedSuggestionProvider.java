/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.util;

import com.google.common.base.Strings;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.dirtcraft.dirtcore.common.command.abstraction.Message;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.minecraft.resources.ResourceLocation;

public interface NeoForgeSharedSuggestionProvider {

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

    static <T> void filterResources(final Iterable<T> pResources, final String pRemaining,
            final String pPrefix, final Function<T, ResourceLocation> pLocationFunction,
            final Consumer<T> pResourceConsumer) {
        if (pRemaining.isEmpty()) {
            pResources.forEach(pResourceConsumer);
        } else {
            final String s = Strings.commonPrefix(pRemaining, pPrefix);
            if (!s.isEmpty()) {
                final String s1 = pRemaining.substring(s.length());
                filterResources(pResources, s1, pLocationFunction, pResourceConsumer);
            }
        }

    }

    static CompletableFuture<Suggestions> suggestResource(
            final Iterable<ResourceLocation> pResources, final SuggestionsBuilder pBuilder,
            final String pPrefix) {
        final String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(pResources, s, pPrefix, resourceLocation -> resourceLocation,
                resourceLocation -> pBuilder.suggest(pPrefix + resourceLocation));
        return pBuilder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestResource(final Stream<ResourceLocation> pResources,
            final SuggestionsBuilder pBuilder, final String pPrefix) {
        return suggestResource(pResources::iterator, pBuilder, pPrefix);
    }

    static CompletableFuture<Suggestions> suggestResource(
            final Iterable<ResourceLocation> pResources, final SuggestionsBuilder pBuilder) {
        final String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(pResources, s, resourceLocation -> resourceLocation,
                resourceLocation -> pBuilder.suggest(resourceLocation.toString()));
        return pBuilder.buildFuture();
    }

    static <T> CompletableFuture<Suggestions> suggestResource(final Iterable<T> pResources,
            final SuggestionsBuilder pBuilder,
            final Function<T, ResourceLocation> pLocationFunction,
            final Function<T, Message> pSuggestionFunction) {
        final String s = pBuilder.getRemaining().toLowerCase(Locale.ROOT);
        filterResources(pResources, s, pLocationFunction,
                t -> pBuilder.suggest(pLocationFunction.apply(t).toString(),
                        pSuggestionFunction.apply(t)));
        return pBuilder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggestResource(
            final Stream<ResourceLocation> pResourceLocations, final SuggestionsBuilder pBuilder) {
        return suggestResource(pResourceLocations::iterator, pBuilder);
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
