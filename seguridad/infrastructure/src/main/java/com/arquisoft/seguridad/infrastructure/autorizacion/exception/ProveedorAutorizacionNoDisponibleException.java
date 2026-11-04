package com.arquisoft.seguridad.infrastructure.autorizacion.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.constant.SeguridadCodes;

public class ProveedorAutorizacionNoDisponibleException extends InfrastructureException {

    public ProveedorAutorizacionNoDisponibleException(String message, Throwable cause) {
        super(message, SeguridadCodes.Autorizacion.PROVEEDOR_AUTORIZACION_NO_DISPONIBLE, cause);
    }

    public ProveedorAutorizacionNoDisponibleException(String message) {
        super(message, SeguridadCodes.Autorizacion.PROVEEDOR_AUTORIZACION_NO_DISPONIBLE);
    }
}
