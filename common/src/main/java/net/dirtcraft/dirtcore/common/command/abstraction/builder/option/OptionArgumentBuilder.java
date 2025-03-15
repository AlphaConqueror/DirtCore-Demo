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

import net.dirtcraft.dirtcore.common.command.abstraction.builder.AbstractArgumentBuilderLike;
import net.dirtcraft.dirtcore.common.command.abstraction.tree.option.OptionCommandNode;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class OptionArgumentBuilder<P extends DirtCorePlugin, S extends Sender,
        T extends OptionArgumentBuilder<P, S, T>> extends AbstractArgumentBuilderLike<S, T> {

    @NonNull
    protected final String optionName;
    protected boolean overridesConsoleUsage = false;

    protected OptionArgumentBuilder(@NonNull final String optionName) {
        this.optionName = optionName;
    }

    public static <P extends DirtCorePlugin, S extends Sender, T extends OptionArgumentBuilder<P,
            S, T>> OptionArgumentBuilder<P, S, T> option(
            @NonNull final String name) {
        return new OptionArgumentBuilder<>(name);
    }

    public OptionCommandNode<P, S> build() {
        return new OptionCommandNode<>(this.getOptionName(), this.getRequirement(),
                this.getRequiredPermission(), this.isOverridingConsoleUsage());
    }

    public boolean isOverridingConsoleUsage() {
        return this.overridesConsoleUsage;
    }

    public T overridesConsoleUsage() {
        this.overridesConsoleUsage = true;
        return this.getThis();
    }

    @NonNull
    public String getOptionName() {
        return this.optionName;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected T getThis() {
        return (T) this;
    }
}
