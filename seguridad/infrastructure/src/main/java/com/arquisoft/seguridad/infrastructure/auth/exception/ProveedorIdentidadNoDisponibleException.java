package com.arquisoft.seguridad.infrastructure.auth.exception;

import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;

public final class ProveedorIdentidadNoDisponibleException extends InfrastructureException {

    public ProveedorIdentidadNoDisponibleException(String message, Throwable cause) {
        super(BaseError.of(SeguridadCodes.Autenticacion.PROVEEDOR_IDENTIDAD_NO_DISPONIBLE, message, cause), cause);
    }
}
