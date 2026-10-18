package com.arquisoft.usuarios.domain.jurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;

import java.util.UUID;

public final class JuradoUsuarioDuplicadoException extends DomainException {

    public JuradoUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarJuradoKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.Jurado.USUARIO_DUPLICADO
        );
    }
}
