/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.util.filter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class JavaFilter implements Filter {

    public static void applyFilter() {
        Logger.getLogger("").setFilter(new JavaFilter());
    }

    @Override
    public boolean isLoggable(final LogRecord record) {
        return !record.getLoggerName().contains("org.hibernate") && !record.getLoggerName()
                .contains("SQL dialect");
    }
}
