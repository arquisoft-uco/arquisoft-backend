package com.arquisoft.shared.exceptions;

/**
 * Clase base abstracta para todas las excepciones personalizadas del proyecto.
 * Define la estructura común: un {@link BaseError} con código, mensaje y traza.
 *
 * <p>La jerarquía de excepciones derivadas es:</p>
 * <pre>
 *   BaseException
 *   ├── DomainException       → violaciones de reglas de negocio        (HTTP 422)
 *   ├── ApplicationException  → errores de orquestación/estado de app   (HTTP 400)
 *   └── InfrastructureException → fallos de servicios externos/infra    (HTTP 503)
 * </pre>
 */
public abstract class BaseException extends RuntimeException {

    private final BaseError error;

    protected BaseException(BaseError error) {
        super(error.getMessage());
        this.error = error;
    }

    protected BaseException(BaseError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }

    public BaseError getError() {
        return error;
    }

    public String getErrorCode() {
        return error.getErrorCode();
    }
}
