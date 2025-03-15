/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction;

import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;

public class StringReader implements ImmutableStringReader {

    private static final char SYNTAX_ESCAPE = '\\';
    private static final char SYNTAX_DOUBLE_QUOTE = '"';
    private static final char SYNTAX_SINGLE_QUOTE = '\'';

    private final String string;
    private int cursor;

    public StringReader(final StringReader other) {
        this.string = other.string;
        this.cursor = other.cursor;
    }

    public StringReader(final String string) {
        this.string = string;
    }

    public static boolean isAllowedNumber(final char c) {
        return c >= '0' && c <= '9' || c == '.' || c == '-';
    }

    public static boolean isQuotedStringStart(final char c) {
        return c == SYNTAX_DOUBLE_QUOTE || c == SYNTAX_SINGLE_QUOTE;
    }

    public static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == '_'
                || c == '-' || c == '.' || c == '+';
    }

    @Override
    public String getString() {
        return this.string;
    }

    @Override
    public int getRemainingLength() {
        return this.string.length() - this.cursor;
    }

    @Override
    public int getTotalLength() {
        return this.string.length();
    }

    @Override
    public int getCursor() {
        return this.cursor;
    }

    public void setCursor(final int cursor) {
        this.cursor = cursor;
    }

    @Override
    public String getRead() {
        return this.string.substring(0, this.cursor);
    }

    @Override
    public String getRemaining() {
        return this.string.substring(this.cursor);
    }

    @Override
    public boolean canRead(final int length) {
        return this.cursor + length <= this.string.length();
    }

    @Override
    public boolean canRead() {
        return this.canRead(1);
    }

    @Override
    public char peek() {
        return this.string.charAt(this.cursor);
    }

    @Override
    public char peek(final int offset) {
        return this.string.charAt(this.cursor + offset);
    }

    public char read() {
        return this.string.charAt(this.cursor++);
    }

    public void skip() {
        this.skip(1);
    }

    public void skip(final int offset) {
        this.cursor += offset;
    }

    public void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) {
            this.skip();
        }
    }

    public int readInt() throws CommandSyntaxException {
        final int start = this.cursor;
        while (this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }
        final String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt()
                    .createWithContext(this);
        }
        try {
            return Integer.parseInt(number);
        } catch (final NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt()
                    .createWithContext(this, number);
        }
    }

    public long readLong() throws CommandSyntaxException {
        final int start = this.cursor;
        while (this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }
        final String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong()
                    .createWithContext(this);
        }
        try {
            return Long.parseLong(number);
        } catch (final NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong()
                    .createWithContext(this, number);
        }
    }

    public double readDouble() throws CommandSyntaxException {
        final int start = this.cursor;
        while (this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }
        final String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble()
                    .createWithContext(this);
        }
        try {
            return Double.parseDouble(number);
        } catch (final NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble()
                    .createWithContext(this, number);
        }
    }

    public float readFloat() throws CommandSyntaxException {
        final int start = this.cursor;
        while (this.canRead() && isAllowedNumber(this.peek())) {
            this.skip();
        }
        final String number = this.string.substring(start, this.cursor);
        if (number.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat()
                    .createWithContext(this);
        }
        try {
            return Float.parseFloat(number);
        } catch (final NumberFormatException ex) {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat()
                    .createWithContext(this, number);
        }
    }

    public String readUnquotedString() {
        final int start = this.cursor;
        while (this.canRead() && isAllowedInUnquotedString(this.peek())) {
            this.skip();
        }
        return this.string.substring(start, this.cursor);
    }

    public String readQuotedString() throws CommandSyntaxException {
        if (!this.canRead()) {
            return "";
        }
        final char next = this.peek();
        if (!isQuotedStringStart(next)) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedStartOfQuote()
                    .createWithContext(this);
        }
        this.skip();
        return this.readStringUntil(next);
    }

    public String readStringUntil(final char terminator) throws CommandSyntaxException {
        final StringBuilder result = new StringBuilder();
        boolean escaped = false;
        while (this.canRead()) {
            final char c = this.read();
            if (escaped) {
                if (c == terminator || c == SYNTAX_ESCAPE) {
                    result.append(c);
                    escaped = false;
                } else {
                    this.setCursor(this.getCursor() - 1);
                    throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape()
                            .createWithContext(this, String.valueOf(c));
                }
            } else if (c == SYNTAX_ESCAPE) {
                escaped = true;
            } else if (c == terminator) {
                return result.toString();
            } else {
                result.append(c);
            }
        }

        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedEndOfQuote()
                .createWithContext(this);
    }

    public String readString() throws CommandSyntaxException {
        if (!this.canRead()) {
            return "";
        }
        final char next = this.peek();
        if (isQuotedStringStart(next)) {
            this.skip();
            return this.readStringUntil(next);
        }
        return this.readUnquotedString();
    }

    public boolean readBoolean() throws CommandSyntaxException {
        final int start = this.cursor;
        final String value = this.readString();
        if (value.isEmpty()) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool()
                    .createWithContext(this);
        }

        if (value.equals("true")) {
            return true;
        } else if (value.equals("false")) {
            return false;
        } else {
            this.cursor = start;
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool()
                    .createWithContext(this, value);
        }
    }

    public void expect(final char c) throws CommandSyntaxException {
        if (!this.canRead() || this.peek() != c) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedSymbol()
                    .createWithContext(this, String.valueOf(c));
        }
        this.skip();
    }
}
