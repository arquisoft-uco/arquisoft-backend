package com.arquisoft.shared.logger;

import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.Mensajes;
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
        delegate.error(message, conCausa(cause, args));
    }

    @Override
    public void debug(ClaveMensaje clave, Object... args) {
        if (delegate.isDebugEnabled()) {
            delegate.debug(Mensajes.obtener(clave), args);
        }
    }

    @Override
    public void info(ClaveMensaje clave, Object... args) {
        if (delegate.isInfoEnabled()) {
            delegate.info(Mensajes.obtener(clave), args);
        }
    }

    @Override
    public void warn(ClaveMensaje clave, Object... args) {
        if (delegate.isWarnEnabled()) {
            delegate.warn(Mensajes.obtener(clave), args);
        }
    }

    @Override
    public void error(ClaveMensaje clave, Object... args) {
        if (delegate.isErrorEnabled()) {
            delegate.error(Mensajes.obtener(clave), args);
        }
    }

    @Override
    public void error(ClaveMensaje clave, Throwable cause, Object... args) {
        if (delegate.isErrorEnabled()) {
            delegate.error(Mensajes.obtener(clave), conCausa(cause, args));
        }
    }

    private static Object[] conCausa(Throwable cause, Object... args) {
        Object[] argsConCausa = new Object[args.length + 1];
        System.arraycopy(args, 0, argsConCausa, 0, args.length);
        argsConCausa[args.length] = cause;
        return argsConCausa;
    }
}
