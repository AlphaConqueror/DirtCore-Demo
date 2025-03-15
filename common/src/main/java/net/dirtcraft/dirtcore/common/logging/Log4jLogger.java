/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.logging;

public class Log4jLogger implements Logger {

    private final org.apache.logging.log4j.Logger logger;

    public Log4jLogger(final org.apache.logging.log4j.Logger logger) {this.logger = logger;}

    @Override
    public void info(final String s, final Object... args) {
        this.logger.info(s, args);
    }

    @Override
    public void warn(final String s, final Object... args) {
        this.logger.warn(s, args);
    }

    @Override
    public void severe(final String s, final Object... args) {
        this.logger.error(s, args);
    }

    @Override
    public void severe(final String s, final Throwable throwable) {
        this.logger.error(s, throwable);
    }
}
