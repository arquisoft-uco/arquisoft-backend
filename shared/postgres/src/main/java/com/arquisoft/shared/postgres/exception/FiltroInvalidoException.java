package com.arquisoft.shared.postgres.exception;

import com.arquisoft.shared.exception.ApplicationException;
import com.arquisoft.shared.exception.BaseError;

/**
 * Lanzada cuando un filtro recibido no puede traducirse a un predicado JPA válido:
 * campo desconocido, operador incompatible con el tipo del campo, o valor no parseable.
 *
 * Extiende ApplicationException (HTTP 400) porque el origen es siempre
 * un parámetro inválido enviado por el consumidor de la API.
 */
public final class FiltroInvalidoException extends ApplicationException {

    public FiltroInvalidoException(String mensaje) {
        super(BaseError.of("FILTRO_INVALIDO", mensaje));
    }

    public FiltroInvalidoException(String mensaje, Throwable cause) {
        super(BaseError.of("FILTRO_INVALIDO", mensaje, cause), cause);
    }
}
