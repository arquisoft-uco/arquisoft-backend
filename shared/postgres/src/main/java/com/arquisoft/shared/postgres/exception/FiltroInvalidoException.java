package com.arquisoft.shared.postgres.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;

public final class FiltroInvalidoException extends ApplicationException {

    public FiltroInvalidoException(String mensaje) {
        super(BaseError.of("FILTRO_INVALIDO", mensaje));
    }

    public FiltroInvalidoException(String mensaje, Throwable cause) {
        super(BaseError.of("FILTRO_INVALIDO", mensaje, cause), cause);
    }
}
