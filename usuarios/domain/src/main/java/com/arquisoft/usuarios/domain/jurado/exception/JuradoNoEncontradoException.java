package com.arquisoft.usuarios.domain.jurado.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverJuradoKey;

import java.util.UUID;

public final class JuradoNoEncontradoException extends DomainException {

    public JuradoNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverJuradoKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Jurado.JURADO_NO_ENCONTRADO
        );
    }
}
