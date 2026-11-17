package com.arquisoft.usuarios.infrastructure.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.ConsultarMiPerfilKey;

import java.util.UUID;

public final class PerfilUsuarioNoDisponibleException extends InfrastructureException {

    public PerfilUsuarioNoDisponibleException(UUID usuario) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.PERFIL_NO_DISPONIBLE,
                Mensajes.formatear(ConsultarMiPerfilKey.ERROR_PERFIL_NO_DISPONIBLE),
                Mensajes.formatear(ConsultarMiPerfilKey.ERROR_PERFIL_NO_DISPONIBLE_DETALLE, usuario)));
    }
}
