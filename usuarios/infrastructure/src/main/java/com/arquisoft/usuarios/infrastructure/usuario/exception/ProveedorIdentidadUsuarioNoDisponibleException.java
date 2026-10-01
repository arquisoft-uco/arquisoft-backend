package com.arquisoft.usuarios.infrastructure.usuario.exception;

import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.constant.UsuariosCodes;

public final class ProveedorIdentidadUsuarioNoDisponibleException extends InfrastructureException {

    public ProveedorIdentidadUsuarioNoDisponibleException(String message) {
        super(message, UsuariosCodes.Usuario.IDP_NO_DISPONIBLE);
    }
}
