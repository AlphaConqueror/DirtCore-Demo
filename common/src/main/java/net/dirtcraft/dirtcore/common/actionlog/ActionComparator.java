/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.actionlog;

import java.util.Comparator;
import java.util.Optional;
import net.dirtcraft.dirtcore.api.actionlog.Action;

public final class ActionComparator implements Comparator<Action> {

    public static final Comparator<Action> INSTANCE = new ActionComparator();

    private ActionComparator() {}

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "OptionalIsPresent"})
    private static <T extends Comparable<T>> int compareOptionals(final Optional<T> a,
            final Optional<T> b) {
        if (!a.isPresent()) {
            return b.isPresent() ? -1 : 0;
        } else if (!b.isPresent()) {
            return 1;
        } else {
            return a.get().compareTo(b.get());
        }
    }

    @Override
    public int compare(final Action o1, final Action o2) {
        int cmp = o1.getTimestamp().compareTo(o2.getTimestamp());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1.getSource().compareTo(o2.getSource());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1.getType().compareTo(o2.getType());
        if (cmp != 0) {
            return cmp;
        }

        cmp = o1.getAuthorization().compareTo(o2.getAuthorization());
        if (cmp != 0) {
            return cmp;
        }

        cmp = compareOptionals(o1.getTarget(), o2.getTarget());
        if (cmp != 0) {
            return cmp;
        }

        cmp = compareOptionals(o1.getTitle(), o2.getTitle());
        if (cmp != 0) {
            return cmp;
        }

        cmp = compareOptionals(o1.getDescription(), o2.getDescription());
        if (cmp != 0) {
            return cmp;
        }

        return compareOptionals(o1.getIncidentId(), o2.getIncidentId());
    }
}
