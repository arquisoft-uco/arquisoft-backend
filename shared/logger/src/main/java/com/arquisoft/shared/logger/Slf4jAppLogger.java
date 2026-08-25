package com.arquisoft.shared.logger;

import org.slf4j.Logger;

final class Slf4jAppLogger implements AppLogger {

    private final Logger delegate;

    Slf4jAppLogger(Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public void debug(String message, Object... args) {
        delegate.debug(message, args);
    }

    @Override
    public void info(String message, Object... args) {
        delegate.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        delegate.warn(message, args);
    }

    @Override
    public void error(String message, Object... args) {
        delegate.error(message, args);
    }

    @Override
    public void error(String message, Throwable cause, Object... args) {
        Object[] argsConCausa = new Object[args.length + 1];
        System.arraycopy(args, 0, argsConCausa, 0, args.length);
        argsConCausa[args.length] = cause;
        delegate.error(message, argsConCausa);
    }
}
