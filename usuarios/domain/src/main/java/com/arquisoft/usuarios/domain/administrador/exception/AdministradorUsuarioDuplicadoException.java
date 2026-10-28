package com.arquisoft.usuarios.domain.administrador.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarAdministradorKey;

import java.util.UUID;

public final class AdministradorUsuarioDuplicadoException extends DomainException {

    public AdministradorUsuarioDuplicadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Administrador.USUARIO_DUPLICADO,
                Mensajes.formatear(AgregarAdministradorKey.ERROR_USUARIO_DUPLICADO, nombre),
                Mensajes.formatear(AgregarAdministradorKey.ERROR_USUARIO_DUPLICADO_DETALLE, usuario)
        ));
    }
}
