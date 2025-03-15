/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.pagination;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import java.util.function.ObjIntConsumer;
import org.jetbrains.annotations.NotNull;

public interface Paginator {

    static <T> void forEachPageEntry(final @NotNull Collection<? extends T> content,
            final int pageSize, final int page, final @NotNull ObjIntConsumer<? super T> consumer) {
        final int size = content.size();
        final int start = pageSize * (page - 1);
        final int end = pageSize * page;

        if (content instanceof List<?> && content instanceof RandomAccess) {
            final List<? extends T> list = (List<? extends T>) content;

            for (int i = start; i < end && i < size; i++) {
                consumer.accept(list.get(i), i);
            }
        } else {
            final Iterator<? extends T> it = content.iterator();

            // skip entries on previous pages
            for (int i = 0; i < start; i++) {
                it.next();
            }

            for (int i = start; i < end && i < size; i++) {
                consumer.accept(it.next(), i);
            }
        }
    }
}
