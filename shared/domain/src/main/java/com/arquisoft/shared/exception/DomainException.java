package com.arquisoft.shared.exception;

/**
 * Excepción para violaciones de reglas de negocio del dominio.
 *
 * <p>HTTP sugerido: {@code 422 Unprocessable Entity}</p>
 *
 * <p>Ejemplos: estado inválido de una entidad, invariante de dominio rota,
 * precondición de negocio no cumplida.</p>
 */
public class DomainException extends BaseException {

    public DomainException(BaseError error) {
        super(error);
    }

    public DomainException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    // Constructores de compatibilidad para código existente
    public DomainException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public DomainException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
