package com.arquisoft.usuarios.domain.coordinador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.RemoverCoordinadorKey;

import java.util.UUID;

public final class CoordinadorNoEncontradoException extends DomainException {

    public CoordinadorNoEncontradoException(UUID usuario) {
        super(
                Mensajes.formatear(RemoverCoordinadorKey.ERROR_NO_ENCONTRADO, usuario),
                UsuariosCodes.Coordinador.COORDINADOR_NO_ENCONTRADO
        );
    }
}
