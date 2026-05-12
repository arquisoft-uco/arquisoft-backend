package com.arquisoft.shared.exceptions;

/**
 * Excepción para errores de orquestación o estado de la aplicación.
 * Representa fallos a nivel de caso de uso que no son violaciones de reglas
 * de negocio puras ni fallos de infraestructura.
 *
 * <p>HTTP sugerido: {@code 400 Bad Request}</p>
 *
 * <p>Ejemplos: precondición de entrada inválida, estado inconsistente
 * detectado en la capa de aplicación antes de llegar al dominio.</p>
 */
public class ApplicationException extends BaseException {

    public ApplicationException(BaseError error) {
        super(error);
    }

    public ApplicationException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public ApplicationException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public ApplicationException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
