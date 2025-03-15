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

package net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity;

import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.arguments.minecraft.entity.AbstractEntityArgument;
import net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgePlugin;
import net.dirtcraft.dirtcore.neoforge_1_21_1.platform.argument.entity.selector.NeoForgeEntitySelectorParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NeoForgeEntityArgument extends AbstractEntityArgument<DirtCoreNeoForgePlugin> {

    private final DirtCoreNeoForgePlugin plugin;

    protected NeoForgeEntityArgument(final DirtCoreNeoForgePlugin plugin, final boolean single,
            final boolean playersOnly) {
        super(single, playersOnly);
        this.plugin = plugin;
    }

    public static NeoForgeEntityArgument entity(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, true, false);
    }

    public static NeoForgeEntityArgument entities(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, false, false);
    }

    public static NeoForgeEntityArgument player(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, true, true);
    }

    public static NeoForgeEntityArgument players(@NonNull final DirtCoreNeoForgePlugin plugin) {
        return new NeoForgeEntityArgument(plugin, false, true);
    }

    @Override
    protected @NonNull NeoForgeEntitySelectorParser provideEntitySelectorParser(
            @NonNull final StringReader reader, final boolean allowSelectors, final boolean single,
            final boolean playersOnly) {
        return new NeoForgeEntitySelectorParser(this.plugin, reader, allowSelectors, single,
                playersOnly);
    }
}
