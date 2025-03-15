/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.suggestion;

import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;

@FunctionalInterface
public interface SuggestionProvider<P extends DirtCorePlugin, S extends Sender> {

    CompletableFuture<Suggestions> getSuggestions(final CommandContext<P, S> context,
            final SuggestionsBuilder builder) throws CommandSyntaxException;
}
