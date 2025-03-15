/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity;

import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.SharedSuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelector;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.selector.AbstractEntitySelectorParser;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.Player;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class AbstractEntityArgument<P extends DirtCorePlugin> implements ArgumentType<P,
        AbstractEntitySelector> {

    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Only one entity is allowed, but the provided selector allows more than one"));
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Only one player is allowed, but the provided selector allows more than one"));
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Only players may be affected by this command, but the provided selector "
                            + "includes entities"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED =
            new SimpleCommandExceptionType(new LiteralMessage("Selector not allowed"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND =
            new SimpleCommandExceptionType(new LiteralMessage("No entity was found"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND =
            new SimpleCommandExceptionType(new LiteralMessage("No player was found"));
    private static final Collection<String> EXAMPLES =
            Arrays.asList("Player", "0123", "@e", "@e[type=foo]",
                    "dd12be42-52a9-4a91-a8a1-11c01849e498");
    protected final boolean single;
    protected final boolean playersOnly;
    protected final String name;

    protected AbstractEntityArgument(final boolean single, final boolean playersOnly) {
        this.single = single;
        this.playersOnly = playersOnly;
        this.name =
                playersOnly ? (single ? "player" : "players") : (single ? "entity" : "entities");
    }

    public static <P extends DirtCorePlugin> Entity getEntity(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        return context.getArgument(name, AbstractEntitySelector.class)
                .findSingleEntity(context.getSource());
    }

    public static <P extends DirtCorePlugin> Collection<? extends Entity> getEntities(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        final Collection<? extends Entity> collection = getOptionalEntities(context, name);

        if (collection.isEmpty()) {
            throw NO_ENTITIES_FOUND.create();
        }

        return collection;
    }

    public static <P extends DirtCorePlugin> Collection<? extends Entity> getOptionalEntities(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        return context.getArgument(name, AbstractEntitySelector.class)
                .findEntities(context.getSource());
    }

    public static <P extends DirtCorePlugin> Player getPlayer(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        return context.getArgument(name, AbstractEntitySelector.class)
                .findSinglePlayer(context.getSource());
    }

    public static <P extends DirtCorePlugin> Collection<Player> getPlayers(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        final List<Player> list = context.getArgument(name, AbstractEntitySelector.class)
                .findPlayers(context.getSource());

        if (list.isEmpty()) {
            throw NO_PLAYERS_FOUND.create();
        }

        return list;
    }

    public static <P extends DirtCorePlugin> Collection<Player> getOptionalPlayers(
            final CommandContext<P, Sender> context,
            final String name) throws CommandSyntaxException {
        return context.getArgument(name, AbstractEntitySelector.class)
                .findPlayers(context.getSource());
    }

    @NonNull
    protected abstract AbstractEntitySelectorParser<P, ?, ?> provideEntitySelectorParser(
            @NonNull final StringReader reader, boolean allowSelectors, final boolean single,
            final boolean playersOnly);

    @Override
    public @NonNull AbstractEntitySelector parse(final P plugin,
            final StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final AbstractEntitySelectorParser<P, ?, ?> parser =
                this.provideEntitySelectorParser(reader, true, this.single, this.playersOnly);
        final AbstractEntitySelector entitySelector = parser.parse();

        if (entitySelector.getMaxResults() > 1 && this.single) {
            reader.setCursor(start);

            if (this.playersOnly) {
                throw ERROR_NOT_SINGLE_PLAYER.createWithContext(reader);
            }

            throw ERROR_NOT_SINGLE_ENTITY.createWithContext(reader);
        }

        return entitySelector;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(final P plugin,
            final CommandContext<P, S> context, final SuggestionsBuilder builder) {
        final StringReader reader = new StringReader(builder.getInput());

        reader.setCursor(builder.getStart());

        final S sender = context.getSource();
        final AbstractEntitySelectorParser<P, ?, ?> parser =
                this.provideEntitySelectorParser(reader,
                        AbstractEntitySelector.canUseSelectors(sender), this.single,
                        this.playersOnly);

        try {
            parser.parse();
        } catch (final CommandSyntaxException ignored) {}

        return parser.fillSuggestions(builder, consumer -> {
            final Collection<String> playerSuggestions =
                    plugin.getPlatformFactory().getPlayerNames();
            SharedSuggestionProvider.suggest(this.playersOnly ? playerSuggestions
                    : Iterables.concat(playerSuggestions, sender.getSelectedEntities()), consumer);
        });
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
