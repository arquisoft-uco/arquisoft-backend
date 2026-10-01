package com.arquisoft.usuarios.domain.estadousuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;

public final class EstadoUsuarioNoEncontradoException extends DomainException {

    public EstadoUsuarioNoEncontradoException(String id) {
        super(
                Mensajes.formatear(RegistrarUsuarioKey.ERROR_ESTADO_NO_ENCONTRADO, id),
                UsuariosCodes.EstadoUsuario.NO_ENCONTRADO
        );
    }
}
