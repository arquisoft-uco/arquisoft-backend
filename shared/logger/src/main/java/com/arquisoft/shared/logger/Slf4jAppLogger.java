package com.arquisoft.shared.logger;

import org.slf4j.Logger;

/**
 * Implementación por defecto de {@link AppLogger} sobre SLF4J.
 *
 * <p>Package-private: los consumidores dependen únicamente del puerto {@link AppLogger};
 * la estrategia concreta la decide {@link AppLoggerConfig}.</p>
 */
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
        // SLF4J trata un Throwable en la última posición como causa con stack trace.
        Object[] argsConCausa = new Object[args.length + 1];
        System.arraycopy(args, 0, argsConCausa, 0, args.length);
        argsConCausa[args.length] = cause;
        delegate.error(message, argsConCausa);
    }
}
