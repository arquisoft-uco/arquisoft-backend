package com.arquisoft.usuarios.domain.coordinador.exception;

import com.arquisoft.shared.exception.DomainException;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;

import java.util.UUID;

public final class CoordinadorUsuarioDuplicadoException extends DomainException {

    public CoordinadorUsuarioDuplicadoException(UUID usuario) {
        super(
                Mensajes.formatear(AgregarCoordinadorKey.ERROR_USUARIO_DUPLICADO, usuario),
                UsuariosCodes.Coordinador.USUARIO_DUPLICADO
        );
    }
}
