package com.arquisoft.seguridad.infrastructure.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.SeguridadMessages;

public final class ProveedorIdentidadNoDisponibleException extends InfrastructureException {

    public ProveedorIdentidadNoDisponibleException(String message, Throwable cause) {
        super(BaseError.of(SeguridadMessages.Login.PROVEEDOR_IDENTIDAD_NO_DISPONIBLE, message, cause), cause);
    }
}
