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

import java.text.DecimalFormat;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NumberSuffixProvider {

    private static final String[] SHORT_SUFFIXES = {
            /*  0 */ "",
            /*  1 */ "",
            /*  2 */ "",
            /*  3 */ "B",      // Billion
            /*  4 */ "T",      // Trillion
            /*  5 */ "Q",      // Quadrillion
            /*  6 */ "Qn",     // Quintillion
            /*  7 */ "S",      // Sextillion
            /*  8 */ "Sp",     // Septillion
            /*  9 */ "O",      // Octillion
            /* 10 */ "N",      // Nonillion
            /* 11 */ "D",      // Decillion
            /* 12 */ "Ud",     // Undecillion
            /* 13 */ "Dd",     // Duodecillion
            /* 14 */ "Td",     // Tredecillion
            /* 15 */ "Qd",     // Quattuordecillion
            /* 16 */ "Qnd",    // Quindecillion
            /* 17 */ "Sd",     // Sexdecillion
            /* 18 */ "Spd",    // Septendecillion
            /* 19 */ "Od",     // Octodecillion
            /* 20 */ "Nd",     // Novemdecillion
            /* 21 */ "V",      // Vigintillion
            /* 22 */ "UV",     // Unvigintillion
            /* 23 */ "DV",     // Duovigintillion
            /* 24 */ "TV",     // Trevigintillion
            /* 25 */ "QV",     // Quattuorvigintillion
            /* 26 */ "QnV",    // Quinvigintillion
            /* 27 */ "SV",     // Sexvigintillion
            /* 28 */ "SpV",    // Septenvigintillion
            /* 29 */ "Ov",     // Octovigintillion
            /* 30 */ "Nv",     // Novemvigintillion
            /* 31 */ "Tg",     // Trigintillion
            /* 32 */ "UTg",    // Untrigintillion
            /* 33 */ "DTg",    // Duotrigintillion
            /* 34 */ "TTg",    // Tretrigintillion
            /* 35 */ "QaTg",   // Quattuortrigintillion
            /* 36 */ "QiTg",   // Quintrigintillion
            /* 37 */ "SxTg",   // Sextrigintillion
            /* 38 */ "SpTg",   // Septentrigintillion
            /* 39 */ "OcTg",   // Octotrigintillion
            /* 40 */ "NoTg",   // Noventrigintillion
            /* 41 */ "Qg",     // Quadragintillion
            /* 42 */ "UQg",    // Unquadragintillion
            /* 43 */ "DQg",    // Duoquadragintillion
            /* 44 */ "TQg",    // Trequadragintillion
            /* 45 */ "QaQg",   // Quattuorquadragintillion
            /* 46 */ "QiQg",   // Quinquadragintillion
            /* 47 */ "SxQg",   // Sexquadragintillion
            /* 48 */ "SpQg",   // Septenquadragintillion
            /* 49 */ "OcQg",   // Octoquadragintillion
            /* 50 */ "NoQg"    // Novemquadragintillion
    };

    private static final String[] LONG_SUFFIXES = {
            /*  0 */ "",
            /*  1 */ "",
            /*  2 */ "",
            /*  3 */ "Billion",             // 10^9
            /*  4 */ "Trillion",            // 10^12
            /*  5 */ "Quadrillion",         // 10^15
            /*  6 */ "Quintillion",         // 10^18
            /*  7 */ "Sextillion",          // 10^21
            /*  8 */ "Septillion",          // 10^24
            /*  9 */ "Octillion",           // 10^27
            /* 10 */ "Nonillion",           // 10^30
            /* 11 */ "Decillion",           // 10^33
            /* 12 */ "Undecillion",         // 10^36
            /* 13 */ "Duodecillion",        // 10^39
            /* 14 */ "Tredecillion",        // 10^42
            /* 15 */ "Quattuordecillion",   // 10^45
            /* 16 */ "Quindecillion",       // 10^48
            /* 17 */ "Sexdecillion",        // 10^51
            /* 18 */ "Septendecillion",     // 10^54
            /* 19 */ "Octodecillion",       // 10^57
            /* 20 */ "Novemdecillion",      // 10^60
            /* 21 */ "Vigintillion",        // 10^63
            /* 22 */ "Unvigintillion",      // 10^66
            /* 23 */ "Duovigintillion",     // 10^69
            /* 24 */ "Trevigintillion",     // 10^72
            /* 25 */ "Quattuorvigintillion",// 10^75
            /* 26 */ "Quinvigintillion",    // 10^78
            /* 27 */ "Sexvigintillion",     // 10^81
            /* 28 */ "Septenvigintillion",  // 10^84
            /* 29 */ "Octovigintillion",    // 10^87
            /* 30 */ "Novemvigintillion",   // 10^90
            /* 31 */ "Trigintillion",       // 10^93
            /* 32 */ "Untrigintillion",     // 10^96
            /* 33 */ "Duotrigintillion",    // 10^99
            /* 34 */ "Tretrigintillion",    // 10^102
            /* 35 */ "Quattuortrigintillion",  // 10^105
            /* 36 */ "Quintrigintillion",      // 10^108
            /* 37 */ "Sextrigintillion",       // 10^111
            /* 38 */ "Septentrigintillion",    // 10^114
            /* 39 */ "Octotrigintillion",      // 10^117
            /* 40 */ "Noventrigintillion",     // 10^120
            /* 41 */ "Quadragintillion",       // 10^123
            /* 42 */ "Unquadragintillion",     // 10^126
            /* 43 */ "Duoquadragintillion",    // 10^129
            /* 44 */ "Trequadragintillion",    // 10^132
            /* 45 */ "Quattuorquadragintillion", // 10^135
            /* 46 */ "Quinquadragintillion",     // 10^138
            /* 47 */ "Sexquadragintillion",      // 10^141
            /* 48 */ "Septenquadragintillion",   // 10^144
            /* 49 */ "Octoquadragintillion",     // 10^147
            /* 50 */ "Novemquadragintillion"     // 10^150

    };

    public static @NonNull String formatBalance(@NonNull final Double value) {
        return formatBalanceInternal(value, false);
    }

    public static @NonNull String formatBalanceHover(@NonNull final Double value) {
        return formatBalanceInternal(value, true);
    }

    private static @NonNull String formatBalanceInternal(@NonNull final Double originalValue,
            final boolean extendedPrecision) {
        if (Double.isInfinite(originalValue) || Double.isNaN(originalValue)) {
            return "∞";
        }

        double value = Math.abs(originalValue);
        final boolean isNegative = originalValue < 0;

        if (value < 1_000_000_000) {
            final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            return (isNegative ? "-" : "") + "$" + decimalFormat.format(value);
        }

        int index = 3;

        while (value >= 1000 && index < SHORT_SUFFIXES.length - 1) {
            value /= 1000;
            index++;
        }

        if (index == SHORT_SUFFIXES.length - 1 && value >= 1000) {
            return (isNegative ? "-" : "") + "$∞";
        }

        final String suffix = extendedPrecision ? LONG_SUFFIXES[index] : SHORT_SUFFIXES[index];
        String formatPattern = extendedPrecision ? "%.8f %s" : "%.2f%s";

        if (extendedPrecision && index < 3) {
            formatPattern = "%.2f %s";
        }

        return (isNegative ? "-" : "") + "$" + String.format(formatPattern, value, suffix);
    }
}
