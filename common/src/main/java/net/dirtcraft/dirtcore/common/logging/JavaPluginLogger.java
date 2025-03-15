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

package net.dirtcraft.dirtcore.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;

public class JavaPluginLogger implements net.dirtcraft.dirtcore.common.logging.Logger {

    private static final Pattern PATTERN = Pattern.compile("\\{\\}");
    private final Logger logger;

    public JavaPluginLogger(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String s, final Object... args) {
        this.logger.log(Level.INFO, this.parse(s, args));
    }

    @Override
    public void warn(final String s, final Object... args) {
        this.logger.log(Level.WARNING, this.parse(s, args));
    }

    @Override
    public void severe(final String s, final Object... args) {
        this.logger.log(Level.SEVERE, this.parse(s, args));
    }

    @Override
    public void severe(final String s, final Throwable t) {
        this.logger.log(Level.SEVERE, s, t);
    }

    @NonNull
    private String parse(@NonNull final String input, @NonNull final Object... args) {
        final Matcher matcher = PATTERN.matcher(input);
        final StringBuilder builder = new StringBuilder(input);
        int counter = 0;
        int shift = 0;

        while (counter < args.length && matcher.find()) {
            final String replacement = this.argToString(args[counter]);

            builder.replace(matcher.start() + shift, matcher.end() + shift, replacement);
            shift += replacement.length() - matcher.group().length();
            counter++;
        }

        return builder.toString();
    }

    @NonNull
    private String argToString(@NonNull final Object arg) {
        if (arg instanceof Throwable) {
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);

            pw.println();
            ((Throwable) arg).printStackTrace(pw);
            pw.close();

            return sw.toString();
        }

        return arg.toString();
    }
}
