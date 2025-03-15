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

package net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.entity;

import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgePlugin;
import net.dirtcraft.dirtcore.forge_1_7_10.platform.argument.entity.selector.ForgeEntitySelectorParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeEntityArgument extends AbstractEntityArgument<DirtCoreForgePlugin> {

    private final DirtCoreForgePlugin plugin;

    protected ForgeEntityArgument(final DirtCoreForgePlugin plugin, final boolean single,
            final boolean playersOnly) {
        super(single, playersOnly);
        this.plugin = plugin;
    }

    public static ForgeEntityArgument entity(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, true, false);
    }

    public static ForgeEntityArgument entities(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, false, false);
    }

    public static ForgeEntityArgument player(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, true, true);
    }

    public static ForgeEntityArgument players(@NonNull final DirtCoreForgePlugin plugin) {
        return new ForgeEntityArgument(plugin, false, true);
    }

    @Override
    protected @NonNull ForgeEntitySelectorParser provideEntitySelectorParser(
            @NonNull final StringReader reader, final boolean allowSelectors, final boolean single,
            final boolean playersOnly) {
        return new ForgeEntitySelectorParser(this.plugin, reader, allowSelectors, single,
                playersOnly);
    }
}
