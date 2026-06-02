package com.arquisoft.shared.exception;

/**
 * Excepción para fallos de infraestructura: servicios externos no disponibles,
 * errores de red, timeouts o dependencias de terceros inalcanzables.
 *
 * <p>HTTP sugerido: {@code 503 Service Unavailable}</p>
 *
 * <p>Ejemplos: proveedor de identidad inaccesible, base de datos no disponible,
 * broker de mensajes caído, timeout en llamada a API externa.</p>
 */
public class InfrastructureException extends BaseException {

    public InfrastructureException(BaseError error) {
        super(error);
    }

    public InfrastructureException(BaseError error, Throwable cause) {
        super(error, cause);
    }

    public InfrastructureException(String message, String errorCode) {
        super(BaseError.of(errorCode, message));
    }

    public InfrastructureException(String message, String errorCode, Throwable cause) {
        super(BaseError.of(errorCode, message, cause), cause);
    }
}
