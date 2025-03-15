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

package net.dirtcraft.dirtcore.forge_1_12_2.platform.argument.world;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.world.AbstractWorldArgument;
import net.dirtcraft.dirtcore.common.command.abstraction.context.CommandContext;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.SimpleCommandExceptionType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.Suggestions;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionsBuilder;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeSharedSuggestionProvider;
import net.dirtcraft.dirtcore.forge_1_12_2.util.ForgeUtils;
import net.minecraft.server.MinecraftServer;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeWorldArgument extends AbstractWorldArgument<DirtCoreForgePlugin> {

    private static final SimpleCommandExceptionType ERROR_SERVER =
            new SimpleCommandExceptionType(new LiteralMessage("Server not found"));
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");

    private ForgeWorldArgument() {}

    public static ForgeWorldArgument world() {
        return new ForgeWorldArgument();
    }

    @Override
    public @NonNull World parse(final DirtCoreForgePlugin plugin,
            final StringReader reader) throws CommandSyntaxException {
        final MinecraftServer server = plugin.getBootstrap().getServer()
                .orElseThrow(() -> ERROR_SERVER.createWithContext(reader));
        final int start = reader.getCursor();

        while (reader.canRead() && this.isAllowedInWorldName(reader.peek())) {
            reader.skip();
        }

        final String worldAsString =
                reader.getString().substring(start, reader.getCursor()).toLowerCase(Locale.ROOT);

        for (final net.minecraft.world.World world : server.worlds) {
            if (ForgeUtils.getWorldName(world).toLowerCase(Locale.ROOT).equals(worldAsString)) {
                return plugin.getPlatformFactory().wrapWorld(world);
            }
        }

        reader.setCursor(start);
        throw ERROR_WORLD.createWithContext(reader);
    }

    @Override
    public <S extends Sender> CompletableFuture<Suggestions> listSuggestions(
            final DirtCoreForgePlugin plugin, final CommandContext<DirtCoreForgePlugin, S> context,
            final SuggestionsBuilder builder) {
        return plugin.getBootstrap().getServer()
                .map(server -> ForgeSharedSuggestionProvider.suggest(
                        Arrays.stream(server.worlds).map(ForgeUtils::getWorldName)
                                .collect(Collectors.toList()), builder))
                .orElse(Suggestions.empty());
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private boolean isAllowedInWorldName(final char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_'
                || c == '-' || c == '.' || c == '+';
    }
}
