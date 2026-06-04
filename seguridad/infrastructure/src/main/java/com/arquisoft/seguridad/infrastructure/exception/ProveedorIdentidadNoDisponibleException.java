package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;

public class ProveedorIdentidadNoDisponibleException extends InfrastructureException {

    public ProveedorIdentidadNoDisponibleException(String message, Throwable cause) {
        super(BaseError.of("PROVEEDOR_IDENTIDAD_NO_DISPONIBLE", message, cause), cause);
    }
}
