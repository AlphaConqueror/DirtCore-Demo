/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
