/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.platform.argument.item;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.AbstractItemArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.item.ItemResult;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_20_1.DirtCoreForgePlugin;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeItemArgument extends AbstractItemArgument<DirtCoreForgePlugin> {

    private static final Collection<String> EXAMPLES =
            Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
    private final HolderLookup<Item> items;

    private ForgeItemArgument(@NonNull final CommandBuildContext context) {
        this.items = context.holderLookup(Registries.ITEM);
    }

    public static ForgeItemArgument item(@NonNull final CommandBuildContext context) {
        return new ForgeItemArgument(context);
    }

    @Override
    public @NonNull ItemResult parse(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        return ForgeItemParser.parseForItem(plugin, this.items, reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreForgePlugin plugin, final CommandContext<DirtCoreForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return ForgeItemParser.fillSuggestions(this.items, builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
