package com.arquisoft.shared.query.exception;

import com.arquisoft.shared.exception.ApplicationException;

public final class FiltroException extends ApplicationException {

    public FiltroException(String mensaje, String errorCode) {
        super(mensaje, errorCode);
    }
}
