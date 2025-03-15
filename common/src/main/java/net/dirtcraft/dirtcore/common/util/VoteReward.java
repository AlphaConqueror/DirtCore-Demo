/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util;

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

public class VoteReward {

    private final int space;
    @NonNull
    private final List<String> commands;

    private VoteReward(final int space, @NonNull final List<String> commands) {
        this.space = space;
        this.commands = commands;
    }

    @NonNull
    public static VoteReward of(final int space, @NonNull final List<String> commands) {
        return new VoteReward(space, commands);
    }

    public int getSpace() {
        return this.space;
    }

    @NonNull
    public List<String> getCommands() {
        return this.commands;
    }
}
