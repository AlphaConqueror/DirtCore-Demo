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

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package net.dirtcraft.dirtcore.common.command.abstraction.exceptions;

import net.dirtcraft.dirtcore.common.command.abstraction.LiteralMessage;

public class BuiltInExceptions implements BuiltInExceptionProvider {

    private static final Dynamic2CommandExceptionType DOUBLE_TOO_SMALL =
            new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage(
                    "Double must not be less than " + min + ", found " + found));
    private static final Dynamic2CommandExceptionType DOUBLE_TOO_BIG =
            new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage(
                    "Double must not be more than " + max + ", found " + found));

    private static final Dynamic2CommandExceptionType FLOAT_TOO_SMALL =
            new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage(
                    "Float must not be less than " + min + ", found " + found));
    private static final Dynamic2CommandExceptionType FLOAT_TOO_BIG =
            new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage(
                    "Float must not be more than " + max + ", found " + found));

    private static final Dynamic2CommandExceptionType INTEGER_TOO_SMALL =
            new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage(
                    "Integer must not be less than " + min + ", found " + found));
    private static final Dynamic2CommandExceptionType INTEGER_TOO_BIG =
            new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage(
                    "Integer must not be more than " + max + ", found " + found));

    private static final Dynamic2CommandExceptionType LONG_TOO_SMALL =
            new Dynamic2CommandExceptionType((found, min) -> new LiteralMessage(
                    "Long must not be less than " + min + ", found " + found));
    private static final Dynamic2CommandExceptionType LONG_TOO_BIG =
            new Dynamic2CommandExceptionType((found, max) -> new LiteralMessage(
                    "Long must not be more than " + max + ", found " + found));

    private static final DynamicCommandExceptionType LITERAL_INCORRECT =
            new DynamicCommandExceptionType(
                    expected -> new LiteralMessage("Expected literal " + expected));
    private static final SimpleCommandExceptionType OPTION_INCOMPLETE =
            new SimpleCommandExceptionType(new LiteralMessage("Option incomplete"));
    private static final DynamicCommandExceptionType OPTION_INCORRECT =
            new DynamicCommandExceptionType(
                    expected -> new LiteralMessage("Expected option " + expected));

    private static final SimpleCommandExceptionType READER_EXPECTED_START_OF_QUOTE =
            new SimpleCommandExceptionType(new LiteralMessage("Expected quote to start a string"));
    private static final SimpleCommandExceptionType READER_EXPECTED_END_OF_QUOTE =
            new SimpleCommandExceptionType(new LiteralMessage("Unclosed quoted string"));
    private static final DynamicCommandExceptionType READER_INVALID_ESCAPE =
            new DynamicCommandExceptionType(character -> new LiteralMessage(
                    "Invalid escape sequence '" + character + "' in quoted string"));
    private static final DynamicCommandExceptionType READER_INVALID_BOOL =
            new DynamicCommandExceptionType(value -> new LiteralMessage(
                    "Invalid bool, expected true or false but found '" + value + "'"));
    private static final DynamicCommandExceptionType READER_INVALID_INT =
            new DynamicCommandExceptionType(
                    value -> new LiteralMessage("Invalid integer '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_INT =
            new SimpleCommandExceptionType(new LiteralMessage("Expected integer"));
    private static final DynamicCommandExceptionType READER_INVALID_LONG =
            new DynamicCommandExceptionType(
                    value -> new LiteralMessage("Invalid long '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_LONG =
            new SimpleCommandExceptionType((new LiteralMessage("Expected long")));
    private static final DynamicCommandExceptionType READER_INVALID_DOUBLE =
            new DynamicCommandExceptionType(
                    value -> new LiteralMessage("Invalid double '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_DOUBLE =
            new SimpleCommandExceptionType(new LiteralMessage("Expected double"));
    private static final DynamicCommandExceptionType READER_INVALID_FLOAT =
            new DynamicCommandExceptionType(
                    value -> new LiteralMessage("Invalid float '" + value + "'"));
    private static final SimpleCommandExceptionType READER_EXPECTED_FLOAT =
            new SimpleCommandExceptionType(new LiteralMessage("Expected float"));
    private static final SimpleCommandExceptionType READER_EXPECTED_BOOL =
            new SimpleCommandExceptionType(new LiteralMessage("Expected bool"));
    private static final DynamicCommandExceptionType READER_EXPECTED_SYMBOL =
            new DynamicCommandExceptionType(
                    symbol -> new LiteralMessage("Expected '" + symbol + "'"));

    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_COMMAND =
            new SimpleCommandExceptionType(new LiteralMessage("Unknown command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_ARGUMENT =
            new SimpleCommandExceptionType(new LiteralMessage("Incorrect argument for command"));
    private static final SimpleCommandExceptionType DISPATCHER_UNKNOWN_OPTION =
            new SimpleCommandExceptionType(new LiteralMessage("Incorrect option for command"));
    private static final SimpleCommandExceptionType DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR =
            new SimpleCommandExceptionType(new LiteralMessage(
                    "Expected whitespace to end one argument, but found trailing data"));
    private static final DynamicCommandExceptionType DISPATCHER_PARSE_EXCEPTION =
            new DynamicCommandExceptionType(
                    message -> new LiteralMessage("Could not parse command: " + message));

    private static final SimpleCommandExceptionType DISPATCHER_CONSOLE_USAGE_EXCEPTION =
            new SimpleCommandExceptionType(
                    new LiteralMessage("This command can not be run via console."));

    @Override
    public Dynamic2CommandExceptionType doubleTooLow() {
        return DOUBLE_TOO_SMALL;
    }

    @Override
    public Dynamic2CommandExceptionType doubleTooHigh() {
        return DOUBLE_TOO_BIG;
    }

    @Override
    public Dynamic2CommandExceptionType floatTooLow() {
        return FLOAT_TOO_SMALL;
    }

    @Override
    public Dynamic2CommandExceptionType floatTooHigh() {
        return FLOAT_TOO_BIG;
    }

    @Override
    public Dynamic2CommandExceptionType integerTooLow() {
        return INTEGER_TOO_SMALL;
    }

    @Override
    public Dynamic2CommandExceptionType integerTooHigh() {
        return INTEGER_TOO_BIG;
    }

    @Override
    public Dynamic2CommandExceptionType longTooLow() {
        return LONG_TOO_SMALL;
    }

    @Override
    public Dynamic2CommandExceptionType longTooHigh() {
        return LONG_TOO_BIG;
    }

    @Override
    public DynamicCommandExceptionType literalIncorrect() {
        return LITERAL_INCORRECT;
    }

    @Override
    public SimpleCommandExceptionType optionIncomplete() {
        return OPTION_INCOMPLETE;
    }

    @Override
    public DynamicCommandExceptionType optionIncorrect() {
        return OPTION_INCORRECT;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedStartOfQuote() {
        return READER_EXPECTED_START_OF_QUOTE;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedEndOfQuote() {
        return READER_EXPECTED_END_OF_QUOTE;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidEscape() {
        return READER_INVALID_ESCAPE;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidBool() {
        return READER_INVALID_BOOL;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidInt() {
        return READER_INVALID_INT;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedInt() {
        return READER_EXPECTED_INT;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidLong() {
        return READER_INVALID_LONG;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedLong() {
        return READER_EXPECTED_LONG;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidDouble() {
        return READER_INVALID_DOUBLE;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedDouble() {
        return READER_EXPECTED_DOUBLE;
    }

    @Override
    public DynamicCommandExceptionType readerInvalidFloat() {
        return READER_INVALID_FLOAT;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedFloat() {
        return READER_EXPECTED_FLOAT;
    }

    @Override
    public SimpleCommandExceptionType readerExpectedBool() {
        return READER_EXPECTED_BOOL;
    }

    @Override
    public DynamicCommandExceptionType readerExpectedSymbol() {
        return READER_EXPECTED_SYMBOL;
    }

    @Override
    public SimpleCommandExceptionType dispatcherUnknownCommand() {
        return DISPATCHER_UNKNOWN_COMMAND;
    }

    @Override
    public SimpleCommandExceptionType dispatcherUnknownArgument() {
        return DISPATCHER_UNKNOWN_ARGUMENT;
    }

    @Override
    public SimpleCommandExceptionType dispatcherUnknownOption() {
        return DISPATCHER_UNKNOWN_OPTION;
    }

    @Override
    public SimpleCommandExceptionType dispatcherExpectedArgumentSeparator() {
        return DISPATCHER_EXPECTED_ARGUMENT_SEPARATOR;
    }

    @Override
    public DynamicCommandExceptionType dispatcherParseException() {
        return DISPATCHER_PARSE_EXCEPTION;
    }

    @Override
    public SimpleCommandExceptionType dispatcherConsoleUsageException() {
        return DISPATCHER_CONSOLE_USAGE_EXCEPTION;
    }
}
