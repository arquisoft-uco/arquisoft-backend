package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.BaseError;
import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.EliminarUsuarioKey;

import java.util.UUID;

public final class UsuarioEliminadoException extends DomainException {

    public UsuarioEliminadoException(UUID usuario, String nombre) {
        super(BaseError.conDetalle(
                UsuariosCodes.Usuario.ELIMINADO,
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ELIMINADO, nombre),
                Mensajes.formatear(EliminarUsuarioKey.ERROR_ELIMINADO_DETALLE, usuario)
        ));
    }
}
