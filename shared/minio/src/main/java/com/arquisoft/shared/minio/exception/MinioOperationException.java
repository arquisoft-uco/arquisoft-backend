package com.arquisoft.shared.minio.exception;

import com.arquisoft.shared.exception.InfrastructureException;

/**
 * Fallo al operar contra el almacenamiento de objetos.
 *
 * <p>Extiende {@link InfrastructureException} (→ 503) y no {@code RuntimeException}: una caída de
 * MinIO es indisponibilidad de un servicio externo, no un defecto del backend. Heredando de
 * {@code RuntimeException} caía en el manejador genérico y salía como 500 sin código de error,
 * indistinguible de un bug propio.
 */
public final class MinioOperationException extends InfrastructureException {

    public MinioOperationException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
