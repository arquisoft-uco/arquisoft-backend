package com.arquisoft.seguridad.domain.exception;

import com.arquisoft.shared.exceptions.BaseError;
import com.arquisoft.shared.exceptions.InfrastructureException;

/**
 * Excepción que indica que el proveedor de identidad no está disponible.
 *
 * <p>Se lanza cuando ocurre un fallo de conectividad o timeout de red al intentar
 * comunicarse con el proveedor de identidad configurado. Es un fallo de infraestructura,
 * no de lógica de negocio, por lo que extiende {@link InfrastructureException}.
 * El handler la mapea a 503 Service Unavailable.</p>
 */
public class IdentityProviderUnavailableException extends InfrastructureException {

    public IdentityProviderUnavailableException(String message, Throwable cause) {
        super(BaseError.of("PROVEEDOR_IDENTIDAD_NO_DISPONIBLE", message, cause), cause);
    }
}
