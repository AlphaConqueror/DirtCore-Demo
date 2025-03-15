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

package net.dirtcraft.dirtcore.common.api.implementation;

import java.util.Objects;
import java.util.SortedSet;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.api.actionlog.ActionLog;
import net.dirtcraft.dirtcore.common.actionlog.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ApiActionLog implements ActionLog {

    private final Log handle;

    public ApiActionLog(final Log handle) {
        this.handle = handle;
    }

    @Override
    public @NonNull SortedSet<Action> getContent() {
        return (SortedSet) this.handle.getContent();
    }

    @Override
    public @NonNull SortedSet<Action> getContent(@NonNull final UUID actor) {
        Objects.requireNonNull(actor, "actor");
        return (SortedSet) this.handle.getContent(actor);
    }
}
