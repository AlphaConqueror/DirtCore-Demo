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

package net.dirtcraft.dirtcore.common.discord.command.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.SubcommandGroupNode;
import net.dirtcraft.dirtcore.common.discord.command.builder.node.SubcommandNode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

public class SubcommandGroupBuilder extends AbstractCommandBuilder<SubcommandGroupNode,
        SubcommandGroupBuilder> {

    private final Map<String, SubcommandNode> children = new LinkedHashMap<>();
    @Nullable
    private CommandFunction function;
    @Nullable
    private Runnable executeAfter;

    protected SubcommandGroupBuilder(@NonNull final String name,
            @NonNull final String description) {
        super(name, description);
    }

    @Override
    public @NotNull SubcommandGroupNode build() {
        return new SubcommandGroupNode(this.name, this.description, this.permission, this.children,
                this.function, this.executeAfter);
    }

    @Override
    protected SubcommandGroupBuilder getThis() {
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandGroupBuilder then(final SubcommandBuilder child) {
        final SubcommandNode node = child.build();

        if (this.children.containsKey(node.getName())) {
            throw new IllegalArgumentException(
                    "Node already has child with name '" + node.getName() + "'.");
        }

        this.children.put(node.getName(), node);
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandGroupBuilder executes(@NonNull final CommandFunction function) {
        this.function = function;
        return this;
    }

    @NonNull
    @CheckReturnValue
    public SubcommandGroupBuilder executesAfter(@NonNull final Runnable run) {
        this.executeAfter = run;
        return this;
    }
}
