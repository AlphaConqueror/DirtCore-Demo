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

package net.dirtcraft.dirtcore.common.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface FormatUtils {

    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(DATE_FORMAT).withZone(ZoneId.systemDefault());

    static String formatDateDiff(@NonNull final Instant from, @NonNull final Instant to,
            final boolean printFull, final boolean addAgo) {
        // this is to combat the small time difference between issuing a punishment and
        // displaying the notification, e.g. 1h 0s -> 59m 59s
        final long millisDiff = from.until(to, ChronoUnit.MILLIS);
        final long ceilSeconds = (long) Math.ceil(millisDiff / 1000d);

        return formatDateDiff(ceilSeconds, printFull, addAgo);
    }

    static String formatDateDiff(long seconds, final boolean printFull, final boolean addAgo) {
        if (seconds <= 0) {
            return "now";
        }

        long minute = seconds / 60;
        seconds = seconds % 60;
        long hour = minute / 60;
        minute = minute % 60;
        final long day = hour / 24;
        hour = hour % 24;

        final StringBuilder sb = new StringBuilder();
        boolean check = false; // used to check if previous time unit was > 0

        if (day > 0) {
            sb.append(day)
                    .append("d ");
            check = true;
        }

        if (hour > 0 || (printFull && check)) {
            sb.append(hour)
                    .append("h ");
            check = true;
        }

        if (minute > 0 || (printFull && check)) {
            sb.append(minute)
                    .append("m ");
            check = true;
        }

        if (seconds > 0 || (printFull && check)) {
            sb.append(seconds)
                    .append("s");
        }

        if (addAgo) {
            sb.append(" ago");
        }

        return sb.toString().trim();
    }

    static String formatDate(final Instant instant) {
        return formatter.format(instant);
    }

    static String formatMinecraftHead(@NonNull final String user) {
        return String.format("https://render.skinmc.net/3d"
                + ".php?user=%s&vr=-10&hr0&hrh=25&aa=&headOnly=true&ratio=50", user);
    }

    static boolean isBlank(@NonNull String s) {
        return s.trim().isEmpty();
    }

    @NonNull
    static String repeatChar(final char ch, final int count) {
        final StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < count; i++) {
            buffer.append(ch);
        }

        return buffer.toString();
    }
}
