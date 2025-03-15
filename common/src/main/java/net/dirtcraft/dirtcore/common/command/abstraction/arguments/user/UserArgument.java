/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.user;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UserArgument implements ArgumentType<DirtCorePlugin, UserParser.UserInformation> {

    protected static final SimpleCommandExceptionType NO_USER_FOUND =
            new SimpleCommandExceptionType(new LiteralMessage("No user was found"));
    protected static final SimpleCommandExceptionType USER_OFFLINE =
            new SimpleCommandExceptionType(new LiteralMessage("User is offline"));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498");

    private final boolean includeOffline;

    protected UserArgument(final boolean includeOffline) {this.includeOffline = includeOffline;}

    public static UserArgument user() {
        return new UserArgument(true);
    }

    public static UserArgument onlineUser() {
        return new UserArgument(false);
    }

    public static UserParser.UserInformation getUser(
            final CommandContext<DirtCorePlugin, Sender> pContext, final String pName) {
        return pContext.getArgument(pName, UserParser.UserInformation.class);
    }

    @Override
    public UserParser.@NonNull UserInformation parse(final DirtCorePlugin plugin,
            final StringReader pReader) throws CommandSyntaxException {
        final UserParser userParser = new UserParser(plugin, pReader, this.includeOffline).parse();
        return userParser.getUserInformation();
    }

    @Override
    public @NonNull String getName() {
        return "user";
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCorePlugin plugin, final CommandContext<DirtCorePlugin, S> pContext,
            final SuggestionsBuilder pBuilder) {
        final StringReader stringreader = new StringReader(pBuilder.getInput());

        stringreader.setCursor(pBuilder.getStart());

        final UserParser userParser = new UserParser(plugin, stringreader, this.includeOffline);

        try {
            userParser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return userParser.fillSuggestions(pBuilder, (builder) -> SharedSuggestionProvider.suggest(
                this.includeOffline ? plugin.getUserManager().getUserMap().values()
                        : plugin.getPlatformFactory().getPlayerNames(), builder));
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
