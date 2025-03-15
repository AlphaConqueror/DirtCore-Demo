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

package net.dirtcraft.dirtcore.common.command.abstraction.builder.option;

import net.dirtcraft.dirtcore.common.command.abstraction.arguments.ArgumentType;
import net.dirtcraft.dirtcore.common.command.abstraction.suggestion.SuggestionProvider;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionArgumentCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class OptionRequiredArgumentBuilder<P extends DirtCorePlugin, S extends Sender, T> extends OptionArgumentBuilder<P, S, OptionRequiredArgumentBuilder<P, S, T>> {

    @NonNull
    private final ArgumentType<P, T> type;
    @Nullable
    private SuggestionProvider<P, S> suggestionsProvider = null;

    private OptionRequiredArgumentBuilder(@NonNull final String optionName,
            @NonNull final ArgumentType<P, T> type) {
        super(optionName);
        this.type = type;
    }

    public static <P extends DirtCorePlugin, S extends Sender, T> OptionRequiredArgumentBuilder<P
            , S, T> option(
            @NonNull final String name, @NonNull final ArgumentType<P, T> type) {
        return new OptionRequiredArgumentBuilder<>(name, type);
    }

    @Override
    public OptionArgumentCommandNode<P, S, T> build() {
        return new OptionArgumentCommandNode<>(this.getOptionName(), this.getRequirement(),
                this.getRequiredPermission(), this.isOverridingConsoleUsage(), this.getType(),
                this.getSuggestionsProvider());
    }

    @Override
    protected OptionRequiredArgumentBuilder<P, S, T> getThis() {
        return this;
    }

    public OptionRequiredArgumentBuilder<P, S, T> suggests(
            final SuggestionProvider<P, S> provider) {
        this.suggestionsProvider = provider;
        return this.getThis();
    }

    @NonNull
    public ArgumentType<P, T> getType() {
        return this.type;
    }

    @Nullable
    public SuggestionProvider<P, S> getSuggestionsProvider() {
        return this.suggestionsProvider;
    }
}
