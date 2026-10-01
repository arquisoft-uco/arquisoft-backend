package com.arquisoft.shared.query.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.message.constant.AppCodes;

public final class FiltroInvalidoException extends ApplicationException {

    public FiltroInvalidoException(String mensaje) {
        super(BaseError.of(AppCodes.Consulta.FILTRO_INVALIDO, mensaje));
    }

    public FiltroInvalidoException(String mensaje, Throwable cause) {
        super(BaseError.of(AppCodes.Consulta.FILTRO_INVALIDO, mensaje, cause), cause);
    }
}
