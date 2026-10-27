package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;

import java.util.UUID;

public final class UsuarioRolesVigentesException extends DomainException {

    public UsuarioRolesVigentesException(UUID usuario, String roles) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.ROLES_VIGENTES,
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ROLES_VIGENTES),
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ROLES_VIGENTES_DETALLE, usuario, roles)
        ));
    }
}
