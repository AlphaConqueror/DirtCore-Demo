/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
