package com.arquisoft.usuarios.domain.usuario.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;

public final class UsuarioIdentificadorDuplicadoException extends DomainException {

    public UsuarioIdentificadorDuplicadoException(String identificador) {
        super(
                Mensajes.formatear(RegistrarUsuarioKey.ERROR_IDENTIFICADOR_DUPLICADO, identificador),
                UsuariosCodes.Usuario.IDENTIFICADOR_DUPLICADO
        );
    }
}
