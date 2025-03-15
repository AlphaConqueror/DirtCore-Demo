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

package net.dirtcraft.dirtcore.common.discord.command.builder.node;

import java.util.Map;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.discord.command.InteractionContext;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandFunction;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.CommandResult;
import net.dirtcraft.dirtcore.common.discord.command.abstraction.FunctionHandler;
import net.dirtcraft.dirtcore.common.discord.permission.Permission;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class OptionNode extends AbstractCommandNode<OptionData> implements FunctionHandler {

    @NonNull
    private final OptionType type;
    @NonNull
    private final Map<String, ChoiceNode> choices;
    private final boolean isRequired;
    private final boolean isAutoComplete;
    @Nullable
    private final CommandFunction function;
    @Nullable
    private final Runnable executeAfter;
    @Nullable
    private final Number minValue;
    @Nullable
    private final Number maxValue;

    public OptionNode(@NonNull final String name, @NonNull final String description,
            @NonNull final Permission requiredPermission, @NonNull final OptionType type,
            final boolean isRequired, final boolean isAutoComplete, @Nullable final Number minValue,
            @Nullable final Number maxValue, @NonNull final Map<String, ChoiceNode> choices,
            @Nullable final CommandFunction function, @Nullable final Runnable executeAfter) {
        super(name, description, requiredPermission);
        this.type = type;
        this.isRequired = isRequired;
        this.isAutoComplete = isAutoComplete;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.choices = choices;
        this.function = function;
        this.executeAfter = executeAfter;
    }

    @NotNull
    @Override
    public OptionData create() {
        final OptionData optionData =
                new OptionData(this.type, this.name, this.description, this.isRequired,
                        this.isAutoComplete).addChoices(
                        this.choices.values().stream().map(ChoiceNode::create)
                                .collect(Collectors.toList()));

        if (this.minValue != null) {
            if (this.type == OptionType.NUMBER) {
                optionData.setMinValue((long) this.minValue);
            } else {
                optionData.setMinValue((int) this.minValue);
            }
        }

        if (this.maxValue != null) {
            if (this.type == OptionType.NUMBER) {
                optionData.setMinValue((long) this.maxValue);
            } else {
                optionData.setMinValue((int) this.maxValue);
            }
        }

        return optionData;
    }

    @Override
    protected CommandResult onInteraction(@NonNull final InteractionContext interactionContext) {
        final ChoiceNode choice =
                this.choices.get(interactionContext.getOptionOrException(this.name).getAsString());

        if (choice == null) {
            return new CommandResult(this.getFunction().apply(interactionContext),
                    this.executeAfter);
        }

        return choice.interact(interactionContext);
    }

    @Override
    public boolean hasFunction() {
        return this.function != null;
    }

    @NonNull
    @Override
    public CommandFunction getFunction() {
        return this.function == null ? this.getAlternativeFunction() : this.function;
    }

    @Override
    public @Nullable Runnable getExecuteAfter() {
        return this.executeAfter;
    }
}
