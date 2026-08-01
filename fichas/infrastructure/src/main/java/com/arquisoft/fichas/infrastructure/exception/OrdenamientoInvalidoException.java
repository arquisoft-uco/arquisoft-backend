package com.arquisoft.fichas.infrastructure.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;

public final class OrdenamientoInvalidoException extends ApplicationException {

    public OrdenamientoInvalidoException(String propertyName) {
        super("El campo de ordenamiento '" + propertyName + "' no es válido", "ORDENAMIENTO_INVALIDO");
    }

    public OrdenamientoInvalidoException(String propertyName, Throwable cause) {
        super(BaseError.of(
                "ORDENAMIENTO_INVALIDO",
                "El campo de ordenamiento '" + propertyName + "' no es válido",
                cause
        ), cause);
    }
}
